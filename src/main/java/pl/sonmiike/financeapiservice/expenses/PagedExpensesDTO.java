package pl.sonmiike.financeapiservice.expenses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedExpensesDTO {

    private int page;
    private int totalPages;
    private List<ExpenseDTO> expenses;
}
