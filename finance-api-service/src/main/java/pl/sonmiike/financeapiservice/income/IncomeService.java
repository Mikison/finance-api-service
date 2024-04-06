package pl.sonmiike.financeapiservice.income;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class IncomeService {
    
    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final UserService userService;

    public PagedIncomesDTO getUserIncome(Long userId, int page, int size) {
        Page<Income> incomes = incomeRepository.findByUserUserId(userId, PageRequest.of(page,size));
        return incomeMapper.toPagedDTO(incomes);
    }

    public IncomeDTO getIncomeById(Long id, Long userId) {
        return incomeRepository.findByIdAndUserUserId(id, userId)
                .map(incomeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found"));
    }

    public void createIncome(AddIncomeDTO incomeDTO, Long userId) {
        Income income = incomeMapper.toEntity(incomeDTO);
        income.setUser(userService.getUserById(userId));
        incomeRepository.save(income);
    }

    public PagedIncomesDTO findIncomesWithFilters(String keyword, LocalDate dateFrom, LocalDate dateTo, BigDecimal amountFrom, BigDecimal amountTo, Pageable pageable) {
        Page<Income> pagedFilteredIncomes = incomeRepository.findAll(IncomeFilterSortingSpecifications.withFilters(keyword, dateFrom, dateTo, amountFrom, amountTo), pageable);
        return incomeMapper.toPagedDTO(pagedFilteredIncomes);
    }

    @Transactional
    public IncomeDTO updateIncome(IncomeDTO incomeDTOtoUpdate, Long userId) {
        if (!incomeRepository.existsById(incomeDTOtoUpdate.getId())) {
            throw new ResourceNotFoundException("Income not found");
        }
        Income income = incomeMapper.toEntity(incomeDTOtoUpdate);
        income.setUser(userService.getUserById(userId));
        return incomeMapper.toDTO(incomeRepository.save(income));
    }
    @Transactional
    public void deleteIncome(Long incomeId, Long userId) {
        incomeRepository.deleteIncomeByIdAndUserUserId(incomeId, userId);
    }
}
