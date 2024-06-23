package com.shoppingList.restservice.items;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.shoppingList.restservice.environments.LocalEnvironmentExtension;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;

@Tag("acceptance")
@ExtendWith(LocalEnvironmentExtension.class)
//@ExtendWith(DevEnvironmentExtension.class)
//@ExtendWith(CIEnvironmentExtension.class)
public class ShoppingListCategoryFilterIT {

	private static String KEY;
	
	@BeforeAll
	static void setup() throws IOException {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.BODY);
		KEY = System.getProperty("api_key");
	}
    
	@Test
    public void testFilterCategory() {
		// Test filter on a category which exists in the shopping list
    	loadShoppingListSample();
    	given()
    		.queryParam("key",KEY)
        .when()
        	.get("/shopList/category/food")
    	.then()
        	.statusCode(200)
        	.body("category", everyItem(is("food")))
        	.body("findAll{i -> i.category != 'food'}", empty());
    }
    
    
    @Test
    public void testFilterNonExistingCategory() {
    	// Test filter on a category which does not exist in the shopping list
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/category/{category}","abcdef")
        .then()
        	.statusCode(200)
        	.body("size()", equalTo(0));
    }
    
    @Test
    public void testDeleteCategory() {
    	// Test deleting all items having 'food' category from the shopping list
    	loadShoppingListSample();
    	
    	given()
    		.queryParam("key",KEY)
        .when()
        	.delete("/shopList/category/food")
        .then()
        	.statusCode(204);
        	
    	given()
    		.queryParam("key",KEY)
        .when()
            .get("/shopList")
        .then()
        	.statusCode(200)
        	.body("findAll{i -> i.category == 'food'}", empty());

        
    }
    
    @Test
    public void testFilterOnEmptyList() {
    	// Test filter against an empty shopping list
    	given()
    		.queryParam("key",KEY)
        .when()
        	.delete("/shopList")
        .then()
        	.statusCode(anyOf(equalTo(404),equalTo(204))); // Not Found or No content
        	
    	given()
    		.queryParam("key",KEY)
        .when()
            .get("/shopList/category/drink")
        .then()
            .statusCode(200) // OK
            .body("size()", equalTo(0));
        
    }
    
    @Test
    public void testFilterDeleteOnEmptyList() {
    	// Test delete a category from an empty shopping list
    	given()
    		.queryParam("key",KEY)
        .when()
        	.delete("/shopList")
        .then()
        	.statusCode(anyOf(equalTo(404),equalTo(204)));
        	
    	given()
    		.queryParam("key",KEY)
        .when()
            .delete("/shopList/category/drink")
        .then()
            .statusCode(404); // Not Found
        
    }
    
    private void loadShoppingListSample() {
    	File jsonFile = new File("src/test/resources/create-shoppingList-request.json");
    	
    	// clear all list items
    	given()
    		.queryParam("key",KEY)
    	.when()
    		.delete("/shopList")
    	.then()
    		.statusCode(anyOf(equalTo(404),equalTo(204))) // Not found or No content
    		;
    	
        given()
        	.body(jsonFile)
        	.contentType("application/json")
        	.queryParam("key",KEY)
        .when()
        	.post("/shopList/addItems")
        .then()
            .statusCode(200) // OK
            .body("size()", greaterThanOrEqualTo(0));
        }
    
}
