package pl.sonmiike.financeapiservice.expenses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sonmiike.financeapiservice.security.auth.AuthService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/filter")
    public ResponseEntity<PagedExpensesDTO> getExpenses(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(value = "fromAmount", required = false) BigDecimal fromAmount,
            @RequestParam(value = "toAmount", required = false) BigDecimal toAmount,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(expenseService.findExpensesWithFilters(keyword, dateFrom, dateTo, fromAmount, toAmount, pageable));
    }

    @PostMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createExpense(@RequestBody @Valid AddExpesneDTO expenseDTO, @PathVariable Long categoryId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        expenseService.createExpense(expenseDTO, userId, categoryId);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        if (expenseService.getExpenseById(expenseId, userId).getUserId().equals(userId)) {
            expenseService.deleteExpense(expenseId, userId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
