package pl.sonmiike.financeapiservice.expenses;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sonmiike.financeapiservice.category.CategoryService;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFound;
import pl.sonmiike.financeapiservice.user.UserService;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;
    private final CategoryService categoryService;

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
                .orElseThrow(() -> new ResourceNotFound("Expense with that id not found in database"));

    }

    public void createExpense(AddExpesneDTO expenseDTO, Long userId, Long categoryId) {
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense.setUser(userService.getUserById(userId));

        expense.setCategory(categoryService.getCategoryById(categoryId)); //
        expenseRepository.save(expense);
    }

    public void updateExpense(ExpenseDTO expenseDTOtoUpdate, Long userId) {
        if (expenseRepository.existsById(expenseDTOtoUpdate.getId())) {
            throw new ResourceNotFound("Expense not found"); // TODO - Look into IdNotMatchingException
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
