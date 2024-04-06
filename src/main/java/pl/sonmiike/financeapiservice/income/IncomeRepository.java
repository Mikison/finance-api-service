package pl.sonmiike.financeapiservice.income;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long>, JpaSpecificationExecutor<Income> {

    Page<Income> findByUserUserId(Long userId, Pageable pageable);

    Optional<Income> findByIdAndUserUserId(Long id, Long userId);

    void deleteIncomeByIdAndUserUserId(Long id, Long userId);

    void deleteAllByUserUserId(Long userId);

//    Page<Income> findIncomesByUserUserIdAndIncomeDateBetweenAndAmountBetweenAndDescriptionNotContainsIgnoreCase
}

