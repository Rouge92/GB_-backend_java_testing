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

public class UpdateImageInfoUnauthTests {
    String imageName = "make random here";
    String imageDescription= "make random here";
    String longImageName = "make random here";
    String longImageDescription= "make random here";
   public static String deleteHash;

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
    @DisplayName("Update image info")
    void updateImageInfoTest() {
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
        deleteHash = given()
                .header("Authorization",UploadImagesTests.token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .body("data.link",notNullValue())
                .body("success",equalTo(true))
                .body("status",equalTo(200))
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
        given()
                .multiPart("title",imageName)
                .multiPart("description",imageDescription)
                .header("Authorization",UploadImagesTests.token)
                .when()
                .post("https://api.imgur.com/3/image/{imageDeleteHash}",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data",equalTo(true))
                .body("success",equalTo(true))
                .body("status",equalTo(200));
        given()
                .header("Authorization",UploadImagesTests.token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.title",notNullValue())
                .body("data.description", notNullValue())
                .body("data.link",notNullValue())
                .body("success",equalTo(true))
                .body("status",equalTo(200));

    }

    @Test
    @DisplayName("Update image info with long title and description")
    void updateImageInfoLongTextTest() {
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
        deleteHash = given()
                .header("Authorization",UploadImagesTests.token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .body("data.link",notNullValue())
                .body("success",equalTo(true))
                .body("status",equalTo(200))
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
        given()
                .multiPart("title",longImageName)
                .multiPart("description",longImageDescription)
                .header("Authorization",UploadImagesTests.token)
                .when()
                .post("https://api.imgur.com/3/image/{imageDeleteHash}",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data",equalTo(true))
                .body("success",equalTo(true))
                .body("status",equalTo(200));
        given()
                .header("Authorization",UploadImagesTests.token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}",UploadImagesTests.uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.title",notNullValue())
                .body("data.description", notNullValue())
                .body("data.link",notNullValue())
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
