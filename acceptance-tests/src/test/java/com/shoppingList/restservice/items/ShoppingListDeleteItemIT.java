package com.shoppingList.restservice.items;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.shoppingList.restservice.environments.CIEnvironmentExtension;
import com.shoppingList.restservice.items.models.Item;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;


@Tag("acceptance")
//@ExtendWith(LocalEnvironmentExtension.class)
//@ExtendWith(DevEnvironmentExtension.class)
@ExtendWith(CIEnvironmentExtension.class)
public class ShoppingListDeleteItemIT {

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
	
	@ParameterizedTest
    @MethodSource("itemCreator")
	public void testDeleteItem(Item item) {
    	// Test deleting an item which exists in the shopping list
       given()
       	.queryParam("key",KEY)
       .when()
           .delete("/shopList/id/{id}", item.id())
       .then()
           .statusCode(204); // No content
   }
    
	@ParameterizedTest
    @MethodSource("itemCreator")
	public void testDeleteItemWithoutKey(Item item) {
    	// Test deleting an item without providing the key
       given()
       .when()
           .delete("/shopList/id/{id}", item.id())
       .then()
       		.body("detail",is("Invalid request parameters."))
       		.statusCode(400); // Bad request
   }
	
    @Test
    public void testDeleteNonExistingItem() {
        // Test delete an item which does not exist in the shopping list
        given()
        	.queryParam("key",KEY)
        .when()
            .delete("/shopList/id/{id}", 99999)
        .then()
            .statusCode(404);  // Not Found
    }
       
   private static Stream<Item> itemCreator() {
	   // create item and add to shopping list
        Item item = new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100));

        Item newItem = given()
            .contentType(ContentType.JSON)
            .body(item)
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
        	.extract()
        	.as(Item.class);
        
        return Stream.of(newItem);
	}
   
}
