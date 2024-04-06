package pl.sonmiike.financeapiservice.income;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class IncomeMapper {
    public IncomeDTO toDTO(Income income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .incomeDate(income.getIncomeDate())
                .name(income.getName())
                .description(income.getDescription())
                .amount(income.getAmount())
                .userId(income.getUser().getUserId())
                .build();
    }

    public Income toEntity(IncomeDTO incomeDTO) {
        return Income.builder()
                .id(incomeDTO.getId())
                .incomeDate(incomeDTO.getIncomeDate())
                .name(incomeDTO.getName())
                .description(incomeDTO.getDescription())
                .amount(incomeDTO.getAmount())
                .build();
    }

    public Income toEntity(AddIncomeDTO incomeDTO) {
        return Income.builder()
                .incomeDate(incomeDTO.getIncomeDate())
                .name(incomeDTO.getName())
                .description(incomeDTO.getDescription())
                .amount(incomeDTO.getAmount())
                .build();
    }

    public PagedIncomesDTO toPagedDTO(Page<Income> incomes) {
        return PagedIncomesDTO.builder()
                .currentPage(incomes.getNumber())
                .totalPages(incomes.getTotalPages() > 1 ? incomes.getTotalPages() - 1 : incomes.getTotalPages() )
                .incomes(incomes.map(this::toDTO).getContent())
                .build();
    }
}
