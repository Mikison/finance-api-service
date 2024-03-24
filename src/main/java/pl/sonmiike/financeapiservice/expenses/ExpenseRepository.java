package pl.sonmiike.financeapiservice.expenses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    Page<Expense> findExpenseByUserUserId(Long userId, Pageable pageable);
    Page<Expense> findExpenseByUserUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    Optional<Expense> findByIdAndUserUserId(Long id, Long userId);

    void deleteByIdAndUserUserId(Long userId, Long expenseId);

    void deleteAllByUserUserId(Long userid);

    void deleteAllByCategoryIdAndUserUserId(Long userId, Long categoryId);
}
