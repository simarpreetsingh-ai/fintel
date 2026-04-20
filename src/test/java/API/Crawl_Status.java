package API;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Crawl_Status {
	

        // Base URI
		 @BeforeClass
		    public void setup()  {
        RestAssured.baseURI = "https://webcrawler.qa3.fintelsandbox.com";
       // RestAssured.baseURI = "https://qa3.fintelsandbox.com";
		 }
		 
		 @Test
		    public void getPostTest() {
        // Send GET request
        Response response = 
            given()
            .header("x-api-key", "12345-ABCDE-67890")
            .header("Content-Type", "application/json")
            .when()
                .get("/crawl/status/019d4855-9651-76ad-9e70-e061721abcd1")
            .then()
                .statusCode(200)
                //.body("id", equalTo(1))
                .log().all()
                .extract().response();

        // Print response
        System.out.println("Response Body: " + response.asString());
    }

}
