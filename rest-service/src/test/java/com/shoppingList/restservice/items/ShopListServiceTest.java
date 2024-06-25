package com.shoppingList.restservice.items;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.List;
import java.util.Random;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShopListServiceTest {
	
	private static Random RANDOM = new Random();

    @InjectMocks
    private ShoppingListService shopListService;

    @Test
    public void testAddItem_Success() throws Exception {
    	// Given
    	Item newItem = new Item(1L,"category","description", 1);

    	// When
    	Item savedItem = shopListService.addItem(newItem);

    	// Then
    	Assertions.assertThat(savedItem.qty()).isNotNull();
    	Assertions.assertThat(savedItem.qty()).isEqualTo(newItem.qty());
    	
    	Assertions.assertThat(savedItem.description()).isNotNull();
    	Assertions.assertThat(savedItem.description()).isEqualTo(newItem.description());
    	
    	Assertions.assertThat(savedItem.category()).isNotNull();
    	Assertions.assertThat(savedItem.category()).isEqualTo(newItem.category());
    }
    
    @Test
    public void testGetAllReturnsShoppingList() throws Exception {
    	// Given
    	Item newItem = new Item(RANDOM.nextLong(), randomAlphabetic(10), randomAlphabetic(10), RANDOM.nextInt(100));
    	Item savedItem = shopListService.addItem(newItem);

    	// When
    	List<Item> shoppingList = shopListService.getShoppingList();

    	// Then
    	Assertions.assertThat(shoppingList).containsExactly(savedItem);
    }

}
