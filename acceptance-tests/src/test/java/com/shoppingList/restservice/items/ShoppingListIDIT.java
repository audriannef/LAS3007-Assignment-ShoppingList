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
public class ShoppingListIDIT {

	@SuppressWarnings("unused")
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
			.statusCode(anyOf(equalTo(404),equalTo(204)))
		;
	}
	
	@Test
    public void testGetNonExistingItem() {
        // Test retrieving an item not found in list
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList/id/{id}", 99999)
        .then()
            .statusCode(404);  // Not Found
    }
	
    
    @ParameterizedTest
    @MethodSource("itemCreator")
    public void testGetItemDetailsById(Item item) {
        Long itemIdInList = item.id();

        given()
        	.queryParam("key",KEY)	
        .when()
            .get("/shopList/id/{id}", itemIdInList)
        .then()
            .statusCode(200)
            .body("id", equalTo(item.id()))
            .body("category", equalTo(item.category()))
            .body("qty", equalTo(item.qty().intValue()))
            .body("description", is(item.description()));
    }
    
    @ParameterizedTest
    @MethodSource("itemCreator")
	public void testDeleteItem(Item item) {
	
       given()
       	.queryParam("key",KEY)
       .when()
           .delete("/shopList/id/{id}", item.id())
       .then()
           .statusCode(204);
   }
       
   private static Stream<Item> itemCreator() {
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
