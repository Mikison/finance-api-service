package pl.sonmiike.financeapiservice.income;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFound;
import pl.sonmiike.financeapiservice.user.UserService;


@Service
@RequiredArgsConstructor
public class IncomeService {
    
    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final UserService userService;

    public PagedIncomesDTO getUserIncome(Long userId, int page, int size) {
        Page<Income> incomes = incomeRepository.findIncomeByUserUserId(userId, PageRequest.of(page,size));
        return incomeMapper.toPagedDTO(incomes);
    }

    public IncomeDTO getIncomeById(Long id, Long userId) {
        return incomeRepository.findIncomeByIdAndUserUserId(id, userId)
                .map(incomeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFound("Income not found"));
    }

    public void createIncome(AddIncomeDTO incomeDTO, Long userId) {
        Income income = incomeMapper.toEntity(incomeDTO);
        income.setUser(userService.getUserById(userId));
        incomeRepository.save(income);
    }

    public Income updateIncome(IncomeDTO incomeDTOtoUpdate, Long userId) {
        Income income = incomeMapper.toEntity(incomeDTOtoUpdate);
        income.setUser(userService.getUserById(userId));
        return incomeRepository.save(income);
    }
    @Transactional
    public void deleteIncome(Long incomeId, Long userId) {
        incomeRepository.deleteIncomeByIdAndUserUserId(incomeId, userId);
    }
}
