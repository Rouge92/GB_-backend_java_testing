import groovy.util.logging.Slf4j;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseTest {
    protected static Map<String, String> headers = new HashMap<>();
    protected static String username;
    static Properties properties = new Properties();

    @BeforeAll
    static void beforeAll() throws IOException {
        loadProperties();
        RestAssured.baseURI = properties.getProperty("base.url");
        headers.put("Authorization", properties.getProperty("token"));
        username = properties.getProperty("username");
    }

    private static void loadProperties() throws IOException {
        properties.load(new FileInputStream("src/test/resources/application.properties"));

    }

}