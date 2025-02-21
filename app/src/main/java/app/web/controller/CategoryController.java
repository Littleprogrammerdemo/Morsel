package app.web.controller;

import app.category.model.Category;
import app.category.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public Category createCategory(@RequestParam String name) {
        return categoryService.createCategory(name);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }

}
