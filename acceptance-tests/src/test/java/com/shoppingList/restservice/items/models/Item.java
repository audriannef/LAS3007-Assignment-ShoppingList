package com.shoppingList.restservice.items.models;

public record Item(Long id, String category, String description, Integer qty) {
}