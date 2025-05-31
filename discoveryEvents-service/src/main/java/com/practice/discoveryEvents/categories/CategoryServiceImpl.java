package com.practice.discoveryEvents.categories;

import com.practice.discoveryEvents.events.EventRepository;
import com.practice.discoveryEvents.util.AlreadyExistsException;
import com.practice.discoveryEvents.util.ConflictException;
import com.practice.discoveryEvents.util.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Category createCategory(Category category) {
        if(categoryRepository.findByName(category.getName()).isPresent()) {
            throw new AlreadyExistsException("Category with name " + category.getName() + " already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)){
            throw new NotFoundException("Category with id " + categoryId + " does not exist");
        }
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("The category is not empty");
        }

         categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Integer categoryId, Category category) {
        Category current = getCategoryById(categoryId);

        if (!current.getName().equals(category.getName())) {
            categoryRepository.findByName(category.getName())
                    .ifPresent(existingCategory -> {
                        if (!existingCategory.getId().equals(categoryId)) {
                            throw new AlreadyExistsException("Category with name " + category.getName() + " already exists");
                        }
                    });
        }


        current.setName(category.getName());
        return categoryRepository.save(current);
    }

    @Override
    public List<Category> getAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of((from/size), size);
        return categoryRepository.findAll(pageable).getContent();
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(()-> new NotFoundException("Category with id " + categoryId + " not found"));
    }
}
