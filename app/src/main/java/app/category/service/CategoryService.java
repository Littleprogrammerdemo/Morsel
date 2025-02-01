package app.category.service;

import app.category.model.Category;
import app.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Category already exists");
        }
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
