package pl.sonmiike.financeapiservice.expenses;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFilterSortingSpecifications {


    public static Specification<Expense> withFilters(
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal fromAmount,
            BigDecimal toAmount) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + keyword.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }
            if (fromDate != null && toDate != null) {
                predicates.add(criteriaBuilder.between(root.get("date"), fromDate, toDate));
            } else {
                if (fromDate != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), fromDate));
                }
                if (toDate != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), toDate));
                }
            }
            if (fromAmount != null && toAmount != null) {
                predicates.add(criteriaBuilder.between(root.get("amount"), fromAmount, toAmount));
            } else {
                if (fromAmount != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), fromAmount));
                }
                if (toAmount != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), toAmount));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
