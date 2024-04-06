package pl.sonmiike.financeapiservice.income;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedIncomesDTO {

    private int currentPage;
    private int totalPages;
    private List<IncomeDTO> incomes;
}
