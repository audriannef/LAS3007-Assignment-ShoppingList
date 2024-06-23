package com.shoppingList.restservice.items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shoppingList.restservice.exceptions.ResourceAlreadyExistsException;

@Service
public class ShoppingListService {

	private final List<Item> shopList = new ArrayList<>();
	
	// Add new item
	public Item addItem(final Item item) {
		if (shopList.stream().anyMatch((b) -> b.id().equals(item.id()))) {
			throw new ResourceAlreadyExistsException();
		}
		Item newItem = new Item(item.id(), item.category(),item.description(), item.qty());
		shopList.add(newItem);
		return newItem;
	}
	
	// Add items
	public List<Item> addItems(final List<Item> items){
		for(Item item: items) {
			addItem(item);
		}
		return shopList;
	}

	// Get shoppingList
	public List<Item> getShoppingList() {
		return shopList;
	}

	// Get a particular item from list
	public Optional<Item> getItemInListById(Long id) {
		return shopList.stream().filter(item -> item.id().equals(id)).findFirst();
	}
		
	// Get a particular category from list
	public Optional<List<Item>> getItemInListByCategory(String category) {
		return Optional.of(shopList.stream()
				.filter(item -> item.category().equals(category))
				.collect(Collectors.toList()));
	}

	// Update quantity of an item
	public Optional<Item> updateItemQuantity(Long id, Item updatedItem) {
		for (int i = 0; i < shopList.size(); i++) {
			Item itemInList = shopList.get(i);
			if (itemInList.id().equals(id)) {
				shopList.set(i, new Item(id, updatedItem.category(),updatedItem.description(), updatedItem.qty()));
				return Optional.of(updatedItem);
			}
		}
		return Optional.empty();
	}
	
	// Remove item with a particular id from list
	public boolean removeItemFromList(Long id) {
		return shopList.removeIf(item -> item.id().equals(id));
	}
	
	public boolean removeAllItems() {
		return shopList.removeAll(shopList);
	}
		
	// Remove items within a particular category
	public boolean removeItemsInCategoryFromList(String category) {
		return shopList.removeIf(item -> item.category().equals(category));
	}
		
	// View a sorted shopping list by category
			public List<Item> sortShoppingListByCategory() {
				return shopList.stream()
						.sorted(Comparator.comparing(Item::category))
						.collect(Collectors.toList());
			}
				
	// View a sorted shopping list by category, in defined order
			public List<Item> sortShoppingListByCategory(String order) {
				// check that order input is valid
				
				switch (order.toUpperCase()) {
					case "DESC" -> {return shopList.stream()
							.sorted(Comparator.comparing(Item::category))
							.collect(Collectors.toList()).reversed();}
			        default -> {return shopList.stream()
							.sorted(Comparator.comparing(Item::category))
							.collect(Collectors.toList());}
					}
				}		
}
