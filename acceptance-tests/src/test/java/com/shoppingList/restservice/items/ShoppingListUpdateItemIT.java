package com.shoppingList.restservice.items;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.io.File;
import java.io.IOException;

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
public class ShoppingListUpdateItemIT {

	private static String KEY;
	
	@BeforeAll
	static void setup() throws IOException {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.BODY);
		
		KEY = System.getProperty("api_key");
	}
	
	@Test
    public void testUpdateItemDetailsById() {
    	loadShoppingListSample();
    	File jsonFile = new File("src/test/resources/update-shoppingListItem-request.json");
    	
        given()
        	.body(jsonFile)
        	.contentType("application/json")
        	.queryParam("key",KEY)
        .when()
            .put("/shopList/id/{id}", 9L)
        .then()
            .statusCode(200)
            .body("id", equalTo(9))
        	.body("category", equalTo("clothes"))
        	.body("description", equalTo("trousers"))
        	.body("qty", equalTo(4));
    }
    
    @Test
    public void testUpdateItemDetailsForNonExistantID() {
    	File jsonFile = new File("src/test/resources/update-shoppingListItem-request.json");
    	
        given()
        	.body(jsonFile)
        	.contentType("application/json")
        	.queryParam("key",KEY)
        .when()
            .put("/shopList/id/{id}", 9999999)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testUpdateItemDetailsWithInvalidBody() {
    	
        given()
        	.body("{123456789}")
        	.contentType("application/json")
        	.queryParam("key",KEY)
        .when()
            .put("/shopList/id/{id}", 9999999)
        .then()
            .statusCode(400);
    }
    
    @Test
    public void testUpdateItemDetailsOnEmptyList() {
       	// clear all list items
       	given()
       		.queryParam("key",KEY)
       	.when()
       		.delete("/shopList")
       	.then()
       		.statusCode(anyOf(equalTo(404),equalTo(204)))
       		;
    	
    	File jsonFile = new File("src/test/resources/update-shoppingListItem-request.json");
    	
        given()
        	.body(jsonFile)
        	.contentType("application/json")
        	.queryParam("key",KEY)
        .when()
            .put("/shopList/id/{id}", 9)
        .then()
            .statusCode(404);;
    }
    

    

   private void loadShoppingListSample() {
   	File jsonFile = new File("src/test/resources/create-shoppingList-request.json");
   	
   	// clear all list items
   	given()
   		.queryParam("key",KEY)
   	.when()
   		.delete("/shopList")
   	.then()
   		.statusCode(anyOf(equalTo(404),equalTo(204)))
   		;
   	
       given()
       		.body(jsonFile)
       		.contentType("application/json")
       		.queryParam("key",KEY)
       .when()
       	.post("/shopList/addItems")
       .then()
           .statusCode(200)
           .body("size()", greaterThanOrEqualTo(0));
           
       }
}
