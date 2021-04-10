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


public class UploadImagesTests extends BaseTest{
    static final String INPUT_IMG_NORMAL_BMP_PATH = "src/test/resources/TSR_Sonic_the_Hedgehog_bmp.bmp";
    static final String INPUT_IMG_SMALL_PNG_LINK = "https://upload.wikimedia.org/wikipedia/commons/c/ca/1x1.png";
    static final String INPUT_IMG_NORMAL_PNG_PATH = "src/test/resources/normapPNG.png";
    static final String INPUT_IMG_SMALL_JPG_PATH = "src/test/resources/1x1.jpg";
    static final String INPUT_IMG_NORMAL_JPG_LINK = "https://cdn02.nintendo-europe.com/media/images/10_share_images/games_15/nintendo_switch_download_software_1/H2x1_NSwitchDS_SonicMania.jpg";
    static final String INPUT_IMG_SMALL_GIF_PATH = "src/test/resources/1x1_gif.gif";
    static final String INPUT_IMG_NORMAL_GIF_PATH = "src/test/resources/normalGIF.gif";
    static final String INPUT_IMG_SMALL_BMP_LINK= "https://lmcnulty.gitlab.io/blog/bmp-output/1x1.bmp";


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
        File inputFile = new File(INPUT_IMG_NORMAL_PNG_PATH );
        File inputFile2 = new File(INPUT_IMG_SMALL_JPG_PATH);
        File inputFile3 = new File(INPUT_IMG_NORMAL_GIF_PATH );



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

    @Test
    @DisplayName("Normal BMP from file upload")
    void uploadNormalBMPFromFileTest() {
        uploadedImageId = given()
                .multiPart("image", new File(INPUT_IMG_NORMAL_BMP_PATH))
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.type",equalTo("image/png"))
                .body("success",equalTo(true))
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }



    @Test
    @DisplayName("Small PNG from link upload")
    void uploadSmallPNGFromLinkTest() {
        uploadedImageId = given()
                .multiPart("image", INPUT_IMG_SMALL_PNG_LINK )
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .log()
                .ifStatusCodeIsEqualTo(200)
                .body("data.type",equalTo("image/png"))
                .body("success",equalTo(true))
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }


    @Test
    @DisplayName("Normal PNG from Base64 upload")
    void uploadNormalPNGFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(normalPNG);
        uploadedImageId = given()
                .multiPart("image", fileContentBase64)
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .body("data.type",equalTo("image/png"))
                .body("success",equalTo(true))
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @DisplayName("Normal JPG from link upload")
    void uploadNormalJPGFromLinkTest() {
        uploadedImageId = given()
                .multiPart("image", INPUT_IMG_NORMAL_JPG_LINK )
                .header("Authorization", token)
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
    }

    @Test
    @DisplayName("Small JPG from Base64 upload")
    void uploadSmallJPGFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(smallJPG);
        uploadedImageId = given()
                .multiPart("image", fileContentBase64)
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .body("data.type",equalTo("image/jpeg"))
                .body("success",equalTo(true))
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }


    @Test
    @DisplayName("Small GIF from file upload")
    void uploadSmallGIFFromFileTest() {
        uploadedImageId = given()
                .multiPart("image", new File(INPUT_IMG_SMALL_GIF_PATH))
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
    @DisplayName("Normal GIF from Base64 upload")
    void uploadNormalGIFFromBase64Test() {
        String fileContentBase64 = Base64.encodeBase64String(normalGIF);
        uploadedImageId = given()
                .multiPart("image", fileContentBase64)
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
    @DisplayName("Small BMP from link upload")
    void uploadSmallBMPFromLinkTest() {
        uploadedImageId = given()
                .multiPart("image", INPUT_IMG_SMALL_BMP_LINK)
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .body("data.type",equalTo("image/png"))
                .body("success",equalTo(true))
                .body("data.id",notNullValue())
                .body("data.deletehash", notNullValue())
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }


    @AfterEach
    void tearDown() {
        given()
                .header("Authorization", token)
                .when()
                .delete("/account/{username}/image/{deleteHash}",username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}