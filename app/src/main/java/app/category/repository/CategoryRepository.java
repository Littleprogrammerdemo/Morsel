package app.category.repository;

import app.category.model.Category;
import app.category.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByType(CategoryType type);

}
