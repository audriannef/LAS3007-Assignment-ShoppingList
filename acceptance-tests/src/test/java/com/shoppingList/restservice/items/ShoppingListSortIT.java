package com.shoppingList.restservice.items;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
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
public class ShoppingListSortIT {

	private static String KEY;
	
	@BeforeAll
	static void setup() throws IOException {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.BODY);
		
		KEY = System.getProperty("api_key");
	}
	
	@Test
    public void testShoppingListSort() {
		// Test sorting a shopping list based on category
		loadShoppingListSample();
    	String[] orderedListAsc = 
    		{"beverage","beverage","beverage",
    			 "clothes",	"clothes",
    			 "food","food","food","food"};
		
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/sort")
        .then()
            .statusCode(200) // OK
            .body("size()", greaterThanOrEqualTo(0))
            .body("category", contains(orderedListAsc));
    }
	
	@Test
    public void testEmptyShoppingListSort() {
		// Test sorting an empty shopping list
    	 given()
    	 	.queryParam("key",KEY)
         .when()
         	.delete("/shopList")
         .then()
         	.statusCode(anyOf(equalTo(404),equalTo(204))); // Not found or no content
    	
    	
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/sort")
        .then()
            .statusCode(200) // OK
            .body("size()", equalTo(0));
        
    }
	
	@Test
    public void testShoppingListSortWithDesc() {
		// Test sorting a shopping list in descending order
		loadShoppingListSample();
		String[] orderedListAsc = 
    		{"food","food","food","food",
    		  "clothes",	"clothes",
    			 "beverage","beverage","beverage"};
		
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/sortBy?ord=DESC")
        .then()
            .statusCode(200) // OK
            .body("size()", equalTo(9))
            .body("category", contains(orderedListAsc));;
        
    }
	
	@Test
    public void testShoppingListSortWithAsc() {
		// Test sorting a shopping list in ascending order    	
		loadShoppingListSample();
		String[] orderedListAsc = 
    		{"beverage","beverage","beverage",
    			 "clothes",	"clothes",
    			 "food","food","food","food"};
		
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/sortBy?ord=ASC")
        .then()
            .statusCode(200) // OK
            .body("size()", equalTo(9))
            .body("category", contains(orderedListAsc));
        
    }
	
	@Test
    public void testShoppingListSortOrderNotSpecified() {
		// Test sorting a shopping list, but order of ASC or DESC is not specified
		loadShoppingListSample();
    	
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/sortBy")
        .then()
            .statusCode(400); // Bad Request
        
    }
	
	
	private void loadShoppingListSample() {
    	File jsonFile = new File("src/test/resources/create-shoppingList-request.json");
    	
    	// clear all list items
    	given()
    		.queryParam("key",KEY)
    	.when()
    		.delete("/shopList")
    	.then()
    		.statusCode(anyOf(equalTo(404),equalTo(204))) // Not found or no content
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
