package com.shoppingList.restservice.items;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.shoppingList.restservice.environments.CIEnvironmentExtension;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;


@Tag("acceptance")
//@ExtendWith(LocalEnvironmentExtension.class)
//@ExtendWith(DevEnvironmentExtension.class)
@ExtendWith(CIEnvironmentExtension.class)
public class ShoppingListGetListIT {

	private static String KEY;
	
	private static Random RANDOM = new Random();
	
	@BeforeAll
	static void setup() throws IOException {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.BODY);
		KEY = System.getProperty("api_key");
	}
	
	//@BeforeEach
	public void clearList() {
		given()
			.queryParam("key",KEY)
		.when()
			.delete("/shopList")
		.then()
			.statusCode(anyOf(equalTo(404),equalTo(204))) // Not Found or  No Content
		;
	}
	
	@Test
    public void testGetShoppingList() {
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0));
    }
	
	@Test
    public void testGetShoppingListWithoutKey() {
		// Test adding an item without passing an api key
        given()
        .when()
            .get("/shopList")
        .then()
        	.body("detail",is("Invalid request parameters."))
            .statusCode(400);
    }
	
}
