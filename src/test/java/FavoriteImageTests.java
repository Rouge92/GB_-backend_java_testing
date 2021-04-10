import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class FavoriteImageTests {

    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.filters(new AllureRestAssured());

        UploadImagesTests.properties = new Properties();
        UploadImagesTests.properties.load(new FileInputStream("src/test/resources/application.properties"));
        UploadImagesTests.token = UploadImagesTests.properties.getProperty("token");
        UploadImagesTests.username = UploadImagesTests.properties.getProperty("username");
        RestAssured.baseURI = UploadImagesTests.properties.getProperty("base.url");
    }

    @Test
    @DisplayName("Favorite image test")
    void favoriteImageTest() {
        UploadImagesTests.uploadedImageId = given()
                .multiPart("image", UploadImagesTests.INPUT_IMG_NORMAL_JPG_LINK )
                .header("Authorization", UploadImagesTests.token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.type",equalTo("image/jpeg"))
                .body("success",equalTo(true))
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
        given()
                .header("Authorization",UploadImagesTests.token)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data",equalTo("favorited"))
                .body("success",equalTo(true))
                .body("status",equalTo(200));

    }
    @AfterEach
    void tearDown() {
        given()
                .header("Authorization", UploadImagesTests.token)
                .when()
                .delete("/account/{username}/image/{deleteHash}",UploadImagesTests.username, UploadImagesTests.uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
