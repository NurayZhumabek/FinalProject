package com.practice.discoveryEvents.categories;

import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);
    void deleteCategory(Integer categoryId);
    Category updateCategory(Integer categoryId, Category category);
    List<Category> getAllCategories(Integer from,Integer size);
    Category getCategoryById(Integer categoryId);
}
