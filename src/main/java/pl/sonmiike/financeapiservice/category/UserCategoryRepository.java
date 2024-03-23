package pl.sonmiike.financeapiservice.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

    Optional<List<UserCategory>> findAllByUserEmail(String email);
    Optional<List<UserCategory>> findAllByUserUserId(Long userId);

    void deleteAllByUserUserId(Long userid);

    Optional<UserCategory> findByUserUserIdAndCategoryId(Long userId, Long categoryId);
}
