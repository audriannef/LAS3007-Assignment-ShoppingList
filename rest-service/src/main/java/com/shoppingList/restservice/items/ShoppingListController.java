package com.shoppingList.restservice.items;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/shopList", params = {"key"})

public class ShoppingListController {

	@Autowired
	private ShoppingListService shopListService;

	// Add a new item
	@PostMapping
	public Item addItem(@Valid @RequestBody Item item) {
		return shopListService.addItem(item);
	}

	// Add a new items
	@PostMapping("/addItems")
	public List<Item> addItems(@Valid @RequestBody List<Item> items) {
		return shopListService.addItems(items);
	}

	// Get shopping list
	@GetMapping
	public List<Item> getShopList() {
		return shopListService.getShoppingList();
	}

	// Get details for a particular item listed in shopping list
	@GetMapping("/id/{id}")
	public ResponseEntity<Item> getItemInListById(@PathVariable Long id) {
		return shopListService.getItemInListById(id).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Update quantity of an item
	@PutMapping("/id/{id}")
	public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item updatedItem) {
		return shopListService.updateItemQuantity(id, updatedItem).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// Remove an Item from shopping list
	@DeleteMapping("/id/{id}")
	public ResponseEntity<Void> removeItem(@PathVariable Long id) {
		return shopListService.removeItemFromList(id) ? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}

	// Remove all Items from shopping list
	@DeleteMapping
	public ResponseEntity<Void> removeAllItems() {
		return shopListService.removeAllItems() ? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}

	// Display only items having a particular category
	@GetMapping("/category/{category}")
	public ResponseEntity<List<Item>> getItemInListByCategory(@PathVariable String category) {
		return shopListService.getItemInListByCategory(category).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Remove all Items in a particular category from shopping list
	@DeleteMapping("/category/{category}")
	public ResponseEntity<Void> removeItemsInCategory(@PathVariable String category) {
		return shopListService.removeItemsInCategoryFromList(category) ? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}

	// Sort Items based on category
	@GetMapping("/sort")
	public List<Item> sortItemInListByCategory() {
		return shopListService.sortShoppingListByCategory();
	}

	// Sort Item based on category and according to provided order
	@GetMapping("/sortBy")
	public List<Item> sortByItemInListCategory(@RequestParam String ord) {
		return shopListService.sortShoppingListByCategory(ord);
	}
	
}
