package pl.sonmiike.financeapiservice.income;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long>{

    Page<Income> findIncomeByUserUserId(Long userId, Pageable pageable);

    Optional<Income> findIncomeByIdAndUserUserId(Long id, Long userId);

    void deleteIncomeByIdAndUserUserId(Long id, Long userId);

    void deleteAllByUserUserId(Long userId);

//    Page<Income> findIncomesByUserUserIdAndIncomeDateBetweenAndAmountBetweenAndDescriptionNotContainsIgnoreCase
}

