package com.shoppingList.restservice.items;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record Item(
		  @NotNull(message = "id must be specified") Long id
		, @NotBlank(message = "Category cannot be blank") String category
		, String description
		, @PositiveOrZero(message = "Quantity must be a positive number") Integer qty
		) {
}