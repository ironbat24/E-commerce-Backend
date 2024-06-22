package com.grocery.service;

import com.grocery.model.Category;
import com.grocery.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {


    private CategoryRepository categoryRepository;
    private ImageStorageService imageStorageService;
    
    public CategoryService(CategoryRepository categoryRepository, ImageStorageService imageStorageService) {
    	this.categoryRepository = categoryRepository;
    	this.imageStorageService = imageStorageService;
    }
    
    public List<Category> findAllCategories(){
    	return categoryRepository.findAll();
    }

    public Category saveCategory(Category category, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String fileUrl = imageStorageService.storeFile(file);
            category.setImageUrl(fileUrl);
        }

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
