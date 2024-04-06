package pl.sonmiike.financeapiservice.category.monthlyBudget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE MonthlyBudget mb SET mb.budgetAmount = :budgetAmount, mb.updatedAt = CURRENT_TIMESTAMP WHERE mb.user.userId = :userId AND mb.category.id = :categoryId AND mb.yearMonth = :yearMonth")
    int updateBudgetAmountByUserIdAndCategoryIdAndYearMonth(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("yearMonth") String yearMonth, @Param("budgetAmount") BigDecimal budgetAmount);

    void deleteByUserUserIdAndCategoryIdAndYearMonth(Long userId, Long categoryId, String string);
}

