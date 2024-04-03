package pl.sonmiike.financeapiservice.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {


    void deleteAllByUserUserId(Long userid);

    Optional<UserCategory> findByUserUserIdAndCategoryId(Long userId, Long categoryId);

    boolean existsByUserUserIdAndCategoryId(Long userId, Long categoryId);
}
