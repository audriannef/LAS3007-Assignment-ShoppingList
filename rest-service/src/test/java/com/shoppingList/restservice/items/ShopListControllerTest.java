package com.shoppingList.restservice.items;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.stream.IntStreams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ShoppingListController.class)
public class ShopListControllerTest {

	private static Random RANDOM = new Random();
	private static String KEY = "_";
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ShoppingListService shopListService;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void testAddItem_Success() throws Exception {

		Item newItem = new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100));
		Item savedItem = new Item(newItem.id(), newItem.category(), newItem.description(), newItem.qty());

		Mockito.when(shopListService.addItem(newItem)).thenReturn(savedItem);

		mockMvc.perform(post("/shopList")
				.param("key", KEY)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newItem)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedItem.id()))
				.andExpect(jsonPath("$.category").value(savedItem.category()))
				.andExpect(jsonPath("$.qty").value(savedItem.qty()));
				
	}
	
	@ParameterizedTest
    @NullAndEmptySource
    public void testAddItem_MissingCategory(String category) throws Exception {
		Item newItem = new Item(RANDOM.nextLong(), category, randomAlphabetic(10), RANDOM.nextInt(100));
		
    	mockMvc.perform(post("/shopList")
    			.param("key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Category cannot be blank"));

        Mockito.verify(shopListService, Mockito.never()).addItem(Mockito.any());
    }
	
	@ParameterizedTest
    @NullSource
    public void testAddItem_MissingId(Long id) throws Exception {
		Item newItem = new Item(id, randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100));
		
    	mockMvc.perform(post("/shopList")
    			.param("key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("id must be specified"));

        Mockito.verify(shopListService, Mockito.never()).addItem(Mockito.any());
    }
	
	@Test
	public void testAddItem_QuantityLessThanZero() throws Exception {
		Item newItem = new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), -100);
		
    	mockMvc.perform(post("/shopList")
    			.param("key", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Quantity must be a positive number"));

        Mockito.verify(shopListService, Mockito.never()).addItem(Mockito.any());
    }
	
	@Test
    public void testGetAllShoppingListItems() throws Exception {
    	List<Item> shoppingList = IntStreams.range(RANDOM.nextInt(10))
    		.mapToObj(i -> new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100)))
    		.toList();

    	Mockito.when(shopListService.getShoppingList()).thenReturn(shoppingList);

        mockMvc.perform(get("/shopList")
        		.param("key", KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.*.id", hasItems(shoppingList.stream().map(Item::id).toArray())))
                .andExpect(jsonPath("$.*.description", hasItems(shoppingList.stream().map(Item::description).toArray())))
                .andExpect(jsonPath("$.*.qty", hasItems(shoppingList.stream().map(Item::qty).toArray())))
                .andExpect(jsonPath("$.*.category", hasItems(shoppingList.stream().map(Item::category).toArray())));
    }

	
	@Test
    public void testGetItemById_Found() throws Exception {
    	Item existingItem = new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100));

    	Mockito.when(shopListService.getItemInListById(existingItem.id())).thenReturn(Optional.of(existingItem));

        mockMvc.perform(get("/shopList/id/{id}", existingItem.id()).param("key", KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingItem.id()));
    }
	
	
	@Test
    public void testGetItemById_NotFound() throws Exception {
    	Mockito.when(shopListService.getItemInListById(Mockito.any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/shopList/id/{id}", RANDOM.nextLong()).param("key", KEY))
                .andExpect(status().isNotFound());
    }
	
	@Test
    public void testSortShoppingListItems() throws Exception {
    	
		List<Item> shoppingList = IntStreams.range(RANDOM.nextInt(9)+1)
    		.mapToObj(i -> new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100)))
    		.toList();
    	
    	List<Item> sortedShopList = new ArrayList<Item>(shoppingList);
    	Collections.sort(sortedShopList, Comparator.comparing(Item::category));
    	
    	Mockito.when(shopListService.sortShoppingListByCategory()).thenReturn(sortedShopList);

        mockMvc.perform(get("/shopList/sort")
        		.param("key", KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.*.category", contains(sortedShopList.stream().map(Item::category).toArray())));
                
    }
	

}
