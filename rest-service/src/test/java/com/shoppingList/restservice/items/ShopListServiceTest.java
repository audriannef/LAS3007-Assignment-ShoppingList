package com.shoppingList.restservice.items;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShopListServiceTest {
	
	//private static Random RANDOM = new Random();

    @InjectMocks
    private ShoppingListService shopListService;

    @Test
    public void testAddItem_Success() throws Exception {
    	// Given
    	Item newItem = new Item(null,"category","description", 1);

    	// When
    	Item savedItem = shopListService.addItem(newItem);

    	// Then
    	Assertions.assertThat(savedItem.qty()).isNotNull();
    	Assertions.assertThat(savedItem.qty()).isEqualTo(newItem.qty());
    	
    	Assertions.assertThat(savedItem.category()).isNotNull();
    	Assertions.assertThat(savedItem.category()).isEqualTo(newItem.category());
    }

}
