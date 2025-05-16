package com.practice.discoveryEvents.categories;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public AdminCategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@RequestBody @Valid NewCategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category created = categoryService.createCategory(category);
        return modelMapper.map(created, CategoryDTO.class);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Integer catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO updateCategory(@PathVariable("catId") Integer catId,
                                      @RequestBody @Valid NewCategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category updated = categoryService.updateCategory(catId, category);
        return modelMapper.map(updated, CategoryDTO.class);
    }

}
