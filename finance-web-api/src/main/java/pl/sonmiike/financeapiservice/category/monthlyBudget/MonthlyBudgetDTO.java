package pl.sonmiike.financeapiservice.category.monthlyBudget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBudgetDTO {


    private Long categoryId;
    private BigDecimal budgetToSet;
}
