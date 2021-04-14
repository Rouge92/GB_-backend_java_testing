import dto.CommonResponse;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import utils.Images;
import utils.parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static utils.Endpoints.POST_IMAGE_REC;


public class UploadImagesNegTests extends BaseTest{

    ResponseSpecification responseNegIMGSpecification = null;
    RequestSpecification requestSpecification = null;
    ResponseSpecification responseNegFileSpecification = null;

    static byte[] largeBMP;
    static Properties properties;
    static String token;
    static String username;


    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new Properties();
        File inputFile4 = new File(Images.INPUT_IMG_LARGE_BMP.path);
        RestAssured.filters(new AllureRestAssured());


        try {
            largeBMP = readFileToByteArray(inputFile4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties.load(new FileInputStream("src/test/resources/application.properties"));
        token = properties.getProperty("token");
        username = properties.getProperty("username");
        RestAssured.baseURI = properties.getProperty("base.url");
    }

    @BeforeEach
    void beforeTest() {
        responseNegIMGSpecification = new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectStatusLine("HTTP/1.1 400 Bad Request")
                .expectContentType(ContentType.JSON)
                .expectBody("success",equalTo(false))
                .expectBody("data.error",equalTo("File is over the size limit"))
                .expectBody("status",equalTo(400))
                .build();

        responseNegFileSpecification = new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectStatusLine("HTTP/1.1 400 Bad Request")
                .expectContentType(ContentType.JSON)
                .expectBody("success",equalTo(false))
                .expectBody("data.error.message",equalTo("File type invalid (1)"))
                .expectBody("status",equalTo(400))
                .build();

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .setAccept(ContentType.JSON)
                .build();
    }



    @Test
    @DisplayName("Large PNG from file upload")
    void uploadLargePNGFromFileTest() {
        CommonResponse response = given()
                .multiPart(parts.IMAGE, new File(Images.INPUT_IMG_LARGE_PNG.path))
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_REC)
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseNegIMGSpecification)
                .extract()
                .as(CommonResponse.class);
    }

    @Test
    @DisplayName("Large JPG from file upload")
    void uploadLargeJPGFromFileTest() {
        given()
                .multiPart(parts.IMAGE, new File(Images.INPUT_IMG_LARGE_JPG.path))
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_REC)
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseNegIMGSpecification);
    }

//    @Test
//    @DisplayName("Large GIF from link upload")
//    void uploadLargeGIFFromLinkTest() {
//        given()
//                .multiPart(parts.IMAGE, Images.INPUT_IMG_LARGE_GIF_LINK.path)
//                .spec(requestSpecification)
//                .when()
//                .post(POST_IMAGE_REC)
//                .prettyPeek()
//                .then()
//                .log()
//                .ifStatusCodeIsEqualTo(200)
//                .spec(responseNegIMGSpecification);
//    }
    @Test
    @DisplayName("Large BMP from Base64 upload")
    void uploadLargeBMPFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(largeBMP);
        given()
                .multiPart(parts.IMAGE, fileContentBase64)
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_REC)
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseNegIMGSpecification);
    }

    @Test
    @DisplayName("Invalid format upload")
    void uploadInvalidFormatTest() {

        given()
                .multiPart(parts.IMAGE, new File(Images.INPUT_INVALID_FILE.path))
                .spec(requestSpecification)
                .when()
                .post(POST_IMAGE_REC)
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .spec(responseNegFileSpecification);
    }

}