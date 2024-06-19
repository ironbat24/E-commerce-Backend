package com.grocery.service;

import com.grocery.model.Category;
import com.grocery.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;
    //Constructor Injection Method
    public CategoryService(CategoryRepository categoryRepository) {
    	this.categoryRepository = categoryRepository;
    }
    
    public List<Category> findAllCategories(){
    	return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        if (category.getParentCategory() != null) {
            Category parent = categoryRepository.findByName(category.getParentCategory().getName())
                    .orElseGet(() -> categoryRepository.save(category.getParentCategory()));
            category.setParentCategory(parent);
        }
        return categoryRepository.save(category);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
