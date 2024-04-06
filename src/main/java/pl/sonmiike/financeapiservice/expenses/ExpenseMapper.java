package pl.sonmiike.financeapiservice.expenses;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExpenseMapper {

    public ExpenseDTO toDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .description(expense.getDescription())
                .date(expense.getDate().toString())
                .amount(expense.getAmount().toString())
                .userId(expense.getUser().getUserId())
                .categoryId(expense.getCategory().getId())
                .build();
    }

    public Expense toEntity(ExpenseDTO expenseDTO) {
        return Expense.builder()
                .id(expenseDTO.getId())
                .name(expenseDTO.getName())
                .description(expenseDTO.getDescription())
                .date(java.time.LocalDate.parse(expenseDTO.getDate()))
                .amount(new BigDecimal(expenseDTO.getAmount()))
                .build();
    }

    public Expense toEntity(AddExpesneDTO expenseDTO) {
        return Expense.builder()
                .name(expenseDTO.getName())
                .description(expenseDTO.getDescription())
                .date(expenseDTO.getDate())
                .amount(expenseDTO.getAmount())
                .build();
    }



    public PagedExpensesDTO toPagedDTO(Page<Expense> expenses) {
        return PagedExpensesDTO.builder()
                .page(expenses.getNumber())
                .totalPages(expenses.getTotalPages() > 1 ? expenses.getTotalPages() - 1 : expenses.getTotalPages())
                .expenses(expenses.getContent().stream().map(this::toDTO).toList())
                .build();
    }
}
