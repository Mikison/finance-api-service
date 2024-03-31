package pl.sonmiike.financeapiservice.category;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetDTO;
import pl.sonmiike.financeapiservice.security.auth.AuthService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Set<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()); // TODO think about moving it to Admin Controller
    }

    @GetMapping("/user")
    public ResponseEntity<Set<CategoryDTO>> getUserCategories(Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(categoryService.getUserCategories(userId));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid AddCategoryDTO categoryDTO, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(categoryService.createAndAssignCategoryToUser(userId, categoryDTO));
    }


    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignCategory(@PathVariable Long categoryId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        categoryService.removeCategoryFromUser(userId, categoryId);
    }

    @PostMapping("/budget")
    public ResponseEntity<MonthlyBudgetDTO> setBudgetForSpecificCategory(@RequestBody @Valid MonthlyBudgetDTO monthlyBudgetDTO, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(categoryService.setCategoryBudgetAmount(userId, monthlyBudgetDTO));
    }


}
