package APITest;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;


public class LoginAPI {
	
String token;
@BeforeClass
public void setup() {
	RestAssured.baseURI= "https://easecommerce.in";
}
// Test to get the token and storing
	@Test
	public void APILogin() {
	
	String requestBody= payLoad.LoginCredentials();
	String response = RestAssured.given().log().all().header("Content-Type","application/json").body(requestBody)
			.when().post("/api/v2/login").then().log().all().extract()
			.response().asString();
	
			JsonPath js = new JsonPath(response);
			token= js.get("token");
				
	}
	
	//Test to get the warehouse details with query parameter "default"
	
	@Test
	public void Warehouse_APITest() {
		String getResponse = RestAssured.given().log().all().header("Content-Type","application/json").
		header("Authorization","Bearer "+token)
		.queryParam("search","default")
		.when().get("/api/v2/manage/warehouse/master/list").
			then().log().all().assertThat().contentType("application/json").extract()
			.response().asString();
		JsonPath res= new JsonPath(getResponse);
		String queryFilter= res.get("docs[0].group");
		assertEquals(queryFilter, "default");
		
		
	}
	
	//Test to confirm the API handles correctly for incorrect or invalid token
	
	@Test
	public void Negative_TestCaseWithInvalidToken() {
		RestAssured.given().log().all().header("Content-Type","application/json").
		header("Authorization","Bearer "+token+"RandomKey")
		.when().get("/api/v2/manage/warehouse/master/list").
			then().log().all().assertThat().statusCode(401);
	}
	
	//Test to confirm the API handles properly for the mandatory query parameter is missing
	
	@Test
	public void Negative_TestCaseWithMissingQueryParametr() {
		RestAssured.given().log().all().header("Content-Type","application/json").
		header("Authorization","Bearer "+token)
		.queryParam("limit", "group=default")
		.when().get("/api/v2/manage/warehouse/master/list").
			then().log().all().assertThat().statusCode(400);
	}
	
	// Test to confirm the API handles properly where no warehouses exist for the given group
	@Test
	public void Negative_NoWarehouseexist() {
		RestAssured.given().log().all().header("Content-Type","application/json").
		header("Authorization","Bearer "+token)
		.queryParam("search","Somerandongroup")
		.when().get("/api/v2/manage/warehouse/master/list").
		then().log().all().assertThat().contentType("application/json").body("docs", hasSize(0));		
		
	}
	
}
