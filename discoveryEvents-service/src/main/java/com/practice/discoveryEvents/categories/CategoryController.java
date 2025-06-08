package com.practice.discoveryEvents.categories;


import com.practice.discoveryEvents.stats.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final StatsClient statsClient;

    public CategoryController(ModelMapper modelMapper, CategoryService categoryService, StatsClient statsClient) {
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.statsClient = statsClient;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDTO> getAllCategories(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                              HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        return categoryService.getAllCategories(from, size)
                .stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategory(@PathVariable("catId") Integer catId,
                                   HttpServletRequest request) {

        statsClient.sendHit(request.getRemoteAddr(), request.getRequestURI());

        Category category = categoryService.getCategoryById(catId);
        return modelMapper.map(category, CategoryDTO.class);
    }


}
