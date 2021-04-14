import dto.UploadImageResponse;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.Endpoints.DELETE_IMAGE_REC;
import static utils.Endpoints.POST_IMAGE_REC;


public class UploadImagesTests extends BaseTest{


    ResponseSpecification responseIMGSpecification = null;
    RequestSpecification requestSpecification = null;

    static byte[] normalPNG;
    static byte[] smallJPG;
    static byte[] normalGIF;
    static Properties properties;
    static String token;
    static String username;
    public static String uploadedImageId;



    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.filters(new AllureRestAssured());

        properties = new Properties();
        File inputFile = new File(Images.INPUT_IMG_NORMAL_PNG.path );
        File inputFile2 = new File(Images.INPUT_IMG_SMALL_JPG.path);
        File inputFile3 = new File(Images.INPUT_IMG_NORMAL_GIF.path );



        try {
            normalPNG = readFileToByteArray(inputFile);
            smallJPG = readFileToByteArray(inputFile2);
            normalGIF = readFileToByteArray(inputFile3);

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
        responseIMGSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectBody("success",equalTo(true))
                .expectBody("data.id",notNullValue())
                .expectBody("data.deletehash", notNullValue())
                .build();
        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Test
    @DisplayName("Normal BMP from file upload")
    void uploadNormalBMPFromFileTest() {
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
    }


    @Test
    @DisplayName("Small PNG from link upload")
    void uploadSmallPNGFromLinkTest() {
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
    }


    @Test
    @DisplayName("Normal PNG from Base64 upload")
    void uploadNormalPNGFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(normalPNG);
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, fileContentBase64)
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
    }

    @Test
    @DisplayName("Normal JPG from link upload")
    void uploadNormalJPGFromLinkTest() {
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, Images.INPUT_IMG_NORMAL_JPG_LINK.path )
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
        assertThat(response.getData().getType(),equalTo(parts.IMAGE_JPEG));
        uploadedImageId = response.getData().getId();
    }

    @Test
    @DisplayName("Small JPG from Base64 upload")
    void uploadSmallJPGFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(smallJPG);
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, fileContentBase64)
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
        assertThat(response.getData().getType(),equalTo(parts.IMAGE_JPEG));
        uploadedImageId = response.getData().getId();
    }


    @Test
    @DisplayName("Small GIF from file upload")
    void uploadSmallGIFFromFileTest() {
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, new File(Images.INPUT_IMG_SMALL_GIF.path))
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
        assertThat(response.getData().getType(),equalTo(parts.IMAGE_GIF));
        uploadedImageId = response.getData().getId();
    }

    @Test
    @DisplayName("Normal GIF from Base64 upload")
    void uploadNormalGIFFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(normalGIF);
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, fileContentBase64)
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
        assertThat(response.getData().getType(),equalTo(parts.IMAGE_GIF));
        uploadedImageId = response.getData().getId();
    }
    @Test
    @DisplayName("Small BMP from link upload")
    void uploadSmallBMPFromLinkTest() {
        UploadImageResponse response = given()
                .multiPart(parts.IMAGE, Images.INPUT_IMG_SMALL_BMP_LINK.path)
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
    }
    @Test
    @DisplayName("Large GIF from link upload")
    void uploadLargeGIFFromLinkTest() {
        UploadImageResponse response =  given()
                .multiPart(parts.IMAGE, Images.INPUT_IMG_LARGE_GIF_LINK.path)
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
        assertThat(response.getData().getType(),equalTo(parts.IMAGE_GIF));
        uploadedImageId = response.getData().getId();
    }

    @AfterEach
    void tearDown() {
        given()
                .spec(requestSpecification)
                .when()
                .delete(DELETE_IMAGE_REC,username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}