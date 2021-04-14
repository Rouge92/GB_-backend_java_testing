import com.flextrade.jfixture.JFixture;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Endpoints.*;

public class UpdateImageInfoTests {

    String imageName = fixture.create(String.class);
    String imageDescription= fixture.create(String.class);
    ResponseSpecification responseIMGSpecification = null;
    RequestSpecification requestSpecification = null;
    ResponseSpecification responseUpdIMGSpecification = null;
    ResponseSpecification responseUpdatedIMGSpecification = null;

    static String longContent;
    public static String uploadedImageId;

    static JFixture fixture = new JFixture();

    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.filters(new AllureRestAssured());
        String fileName = "src/test/resources/lorem.txt";
        longContent = Files.lines(Paths.get(fileName)).reduce("", String::concat);
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

        responseUpdatedIMGSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectBody("data.title",notNullValue())
                .expectBody("data.description", notNullValue())
                .expectBody("data.id",notNullValue())
                .expectBody("success",equalTo(true))
                .build();

        responseUpdIMGSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectBody("data",equalTo(true))
                .expectBody("success",equalTo(true))
                .build();


        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", UploadImagesTests.token)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Test
    @DisplayName("Update image info")
    void updateImageInfoTest() {
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, new File(Images.INPUT_IMG_NORMAL_BMP.path))
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
        System.out.println(uploadedImageId);
        given()
                .multiPart(parts.TITLE,imageName)
                .multiPart(parts.DESCRIPTION,imageDescription)
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_INFO_REC,uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseUpdIMGSpecification);
        given()
                .spec(requestSpecification)
                .when()
                .get(GET_IMAGE_REC,uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseUpdatedIMGSpecification);
    }


    @Test
    @DisplayName("Update image info with long title and description")
    void updateImageInfoLongTextTest() {
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, new File(Images.INPUT_IMG_NORMAL_BMP.path))
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
        System.out.println(uploadedImageId);
        given()
                .multiPart(parts.TITLE,longContent)
                .multiPart(parts.DESCRIPTION,longContent)
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_INFO_REC,uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseUpdIMGSpecification);
        given()
                .spec(requestSpecification)
                .when()
                .get(GET_IMAGE_REC,uploadedImageId )
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseUpdatedIMGSpecification);
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
