package pl.sonmiike.financeapiservice.expenses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sonmiike.financeapiservice.security.auth.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<PagedExpensesDTO> getUserExpenses(Authentication authentication,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(expenseService.getUserExpenses(userId, page, size));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagedExpensesDTO> getUserExpensesByCategory(Authentication authentication,
                                                                      @PathVariable Long categoryId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(expenseService.getUserExpensesByCategory(userId, categoryId, page, size));
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long expenseId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(expenseService.getExpenseById(expenseId, userId));
    }

    @PostMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createExpense(@RequestBody @Valid AddExpesneDTO expenseDTO, @PathVariable Long categoryId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        expenseService.createExpense(expenseDTO, userId, categoryId);
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long expenseId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        expenseService.deleteExpense(expenseId, userId);
    }

}
