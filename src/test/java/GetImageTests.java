import dto.UploadImageResponse;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;
import utils.Images;
import utils.parts;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Endpoints.*;

public class GetImageTests {

    ResponseSpecification responseIMGSpecification = null;
    RequestSpecification requestSpecification = null;
    public static String uploadedImageId;

    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.filters(new AllureRestAssured());
        UploadImagesTests.properties = new Properties();
        UploadImagesTests.properties.load(new FileInputStream("src/test/resources/application.properties"));
        UploadImagesTests.token = UploadImagesTests.properties.getProperty("token");
        UploadImagesTests.username = UploadImagesTests.properties.getProperty("username");
        RestAssured.baseURI = UploadImagesTests.properties.getProperty("base.url");
    }

    @BeforeEach
    void beforeTest() {
        responseIMGSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectBody("success",equalTo(true))
                .expectBody("data.id",notNullValue())
                .expectBody("data.deletehash", notNullValue())
                .build();

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", UploadImagesTests.token)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Test
    @DisplayName("Simple get image test")
    void getImageTest() {

        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, Images.INPUT_IMG_SMALL_PNG_LINK.path )
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_REC)
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseIMGSpecification)
                .extract()
                .as(UploadImageResponse.class);
        assertThat(response.getData().getType(),equalTo(parts.IMAGE_PNG));
        uploadedImageId = response.getData().getId();
        given()
                .spec(requestSpecification)
                .when()
                .get(GET_IMAGE_REC,uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseIMGSpecification);


    }
    @AfterEach
    void tearDown() {
        given()
                .spec(requestSpecification)
                .when()
                .delete(DELETE_IMAGE_REC,UploadImagesTests.username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
