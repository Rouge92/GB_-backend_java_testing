import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class UploadImagesNegTests extends BaseTest{
    static final String INPUT_IMG_LARGE_PNG_PATH = "src/test/resources/largePNG.png";
    static final String INPUT_IMG_LARGE_JPG_PATH = "src/test/resources/largeJPG.jpg";
    static final String INPUT_IMG_LARGE_GIF_LINK= "https://i.imgur.com/6fWJ7XQ.gif";
    static final String INPUT_IMG_LARGE_BMP_PATH = "src/test/resources/largeBMP.bmp";
    static final String INPUT_INVALID_FILE_PATH = "src/test/resources/encoded-20210405133937.txt";

    static byte[] largeBMP;
    static Properties properties;
    static String token;
    static String username;
    public static String uploadedImageId;

    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new Properties();
        File inputFile4 = new File(INPUT_IMG_LARGE_BMP_PATH);
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




    @Test
    @DisplayName("Large PNG from file upload")
    void uploadLargePNGFromFileTest() {
       given()
                .multiPart("image", new File(INPUT_IMG_LARGE_PNG_PATH))
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.error",equalTo("File is over the size limit"))
                .body("success",equalTo(false))
                .body("status",equalTo(400));
    }

    @Test
    @DisplayName("Large JPG from file upload")
    void uploadLargeJPGFromFileTest() {
       given()
                .multiPart("image", new File(INPUT_IMG_LARGE_JPG_PATH))
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.error",equalTo("File is over the size limit"))
                .body("success",equalTo(false))
                .body("status",equalTo(400));
    }

    @Test
    @DisplayName("Large GIF from link upload")
    void uploadLargeGIFFromLinkTest() {
        uploadedImageId = given()
                .multiPart("image", INPUT_IMG_LARGE_GIF_LINK)
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
             .body("data.type",equalTo("image/gif"))
             .body("success",equalTo(true))
             .body("data.id",notNullValue())
             .body("data.deletehash", notNullValue())
             .extract()
             .response()
             .jsonPath()
             .getString("data.id");
    }
    @Test
    @DisplayName("Large BMP from Base64 upload")
    void uploadLargeBMPFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(largeBMP);
        given()
                .multiPart("image", fileContentBase64)
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .body("data.error",equalTo("File is over the size limit"))
                .body("success",equalTo(false))
                .body("status",equalTo(400));
    }

    @Test
    @DisplayName("Invalid format upload")
    void uploadInvalidFormatTest() {

        given()
                .multiPart("image", new File (INPUT_INVALID_FILE_PATH))
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .body("data.error.message",equalTo("File type invalid (1)"))
                .body("success",equalTo(false))
                .body("status",equalTo(400));
    }

}