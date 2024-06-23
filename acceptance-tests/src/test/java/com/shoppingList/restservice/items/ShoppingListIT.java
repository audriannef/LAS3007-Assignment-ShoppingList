package com.shoppingList.restservice.items;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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
import org.junit.jupiter.params.provider.ValueSource;

import com.shoppingList.restservice.environments.CIEnvironmentExtension;
import com.shoppingList.restservice.items.models.Item;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;


@Tag("acceptance")
//@ExtendWith(LocalEnvironmentExtension.class)
//@ExtendWith(DevEnvironmentExtension.class)
@ExtendWith(CIEnvironmentExtension.class)
public class ShoppingListIT {

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
        given()
        .when()
            .get("/shopList")
        .then()
        	.body("detail",is("Invalid request parameters."))
            .statusCode(400);
    }
	
	@Test
    public void testAddItem() {
        Item newItem = createItem();
        given()
            .contentType(ContentType.JSON)
            .body(newItem)
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
            .statusCode(200)
            .body("category", equalTo(newItem.category()))
            .body("qty", equalTo(newItem.qty()));
    }
	
	@ParameterizedTest
    @MethodSource("itemCreator")
    public void testDuplicateItem(Item newItem) {
        
        given()
            .contentType(ContentType.JSON)
            .body(newItem)
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
        	.statusCode(409)// Conflict
        	.body(is("Resource already exists"))
        	;  
    }
	
	
	@Test
	public void testAddItemNegativeQuantity() {
		Item negQtyItem = new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), -1);
		
        given()
            .contentType(ContentType.JSON)
            .body(negQtyItem)
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
        	.contentType(ContentType.TEXT)
            .statusCode(400)
            .body(is("Quantity must be a positive number"))
            ;
    }
	
	@Test
	public void testAddItemWithBlankCategory() {
		Item blankCatItem = new Item(RANDOM.nextLong(), "     ", randomAlphabetic(10),  RANDOM.nextInt(100));
		
        given()
            .contentType(ContentType.JSON)
            .body(blankCatItem)
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
        	.contentType(ContentType.TEXT)
            .statusCode(400)
            .body(is("Category cannot be blank"))
            ;
    }
	
	@Test
	public void testAddItemWithNullId() {
		Item nullIdItem = new Item(null, randomAlphabetic(10), randomAlphabetic(10),  RANDOM.nextInt(100));
		
        given()
            .contentType(ContentType.JSON)
            .body(nullIdItem)
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
        	.contentType(ContentType.TEXT)
            .statusCode(400)
            .body(is("id must be specified"))
            ;
    }
	
	@Test
    public void testAddItemInvalidJsonBody() {
        // Test adding an item with invalid json body
        given()
            .contentType("application/json")
            .body("{abdef}")
            .queryParam("key",KEY)
        .when()
            .post("/shopList")
        .then()
            .statusCode(400);  // Bad Request
    }
	
    @ParameterizedTest
    @ValueSource(ints = {0,1,50})
    public void testAddNItems(int num) {
        clearList();
        
        for (int i = 0; i < num; i++) {
	        Item item = createItem();
	        	
	        given()
	            .contentType(ContentType.JSON)
	            .body(item)
	            .queryParam("key",KEY)
	        .when()
	            .post("/shopList")
	        .then()
	            .statusCode(200)
	            .body("category", equalTo(item.category()))
	            .body("qty", equalTo(item.qty()));
	        }
    	
        given()
        	.queryParam("key",KEY)
        .when()
            .get("/shopList")
        .then()
            .statusCode(200)
            .body("size()", equalTo(num));
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
   
   private static Item createItem() {
	   return new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100));
   }
}
