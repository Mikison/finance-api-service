package pl.sonmiike.financeapiservice.expenses;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sonmiike.financeapiservice.category.*;
import pl.sonmiike.financeapiservice.exceptions.custom.IdNotMatchingException;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserCategoryRepository userCategoryRepository;

    private final UserService userService;
    private final CategoryService categoryService;

    private final ExpenseMapper expenseMapper;
    public PagedExpensesDTO getUserExpenses(Long userId, int page, int size) {
        Page<Expense> pagedExpenses = expenseRepository.findExpenseByUserUserId(userId, PageRequest.of(page, size));
        return expenseMapper.toPagedDTO(pagedExpenses);
    }

    public PagedExpensesDTO getUserExpensesByCategory(Long userId, Long categoryId, int page, int size) {
        Page<Expense> pagedExpenses = expenseRepository.findExpenseByUserUserIdAndCategoryId(userId, categoryId, PageRequest.of(page, size));
        return expenseMapper.toPagedDTO(pagedExpenses);
    }

    public ExpenseDTO getExpenseById(Long id, Long userId) {
        return expenseRepository.findByIdAndUserUserId(id, userId)
                .map(expenseMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Expense with that id not found in database"));

    }

    public void createExpense(AddExpesneDTO expenseDTO, Long userId, Long categoryId) {
        userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new IdNotMatchingException("User does not have category with that id assigned"));
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense.setUser(userService.getUserById(userId));

        expense.setCategory(categoryService.getCategoryById(categoryId));
        expenseRepository.save(expense);
    }

    public PagedExpensesDTO findExpensesWithFilters(String keyword, LocalDate dateFrom, LocalDate dateTo, BigDecimal amountFrom, BigDecimal amountTo, Pageable pageable) {
        Page<Expense> pagedFilteredExpenses = expenseRepository.findAll(ExpenseFilterSortingSpecifications.withFilters(keyword, dateFrom, dateTo, amountFrom, amountTo), pageable);
        return expenseMapper.toPagedDTO(pagedFilteredExpenses);
    }

    public void updateExpense(ExpenseDTO expenseDTOtoUpdate, Long userId) {
        if (!expenseRepository.existsById(expenseDTOtoUpdate.getId())) {
            throw new IdNotMatchingException("Expense not found");
        }
        Expense expense = expenseMapper.toEntity(expenseDTOtoUpdate);
        expense.setUser(userService.getUserById(userId));
        expenseRepository.save(expense);
    }


    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        expenseRepository.deleteByIdAndUserUserId(expenseId, userId);
    }

}
