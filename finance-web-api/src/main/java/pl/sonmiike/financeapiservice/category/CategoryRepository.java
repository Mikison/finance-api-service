package pl.sonmiike.financeapiservice.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c INNER JOIN UserCategory uc ON c.id = uc.category.id WHERE uc.user.userId = :userId")
    List<Category> findAllCategoriesByUserId(@Param("userId") Long userId);

    Category findByNameIgnoreCase(String name);
}
