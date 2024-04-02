package pl.sonmiike.financeapiservice.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudget;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetDTO;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetRepository;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.expenses.ExpenseRepository;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final MonthlyBudgetRepository monthlyBudgetRepository;

    private final CategoryMapper categoryMapper;

    public Set<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public Set<CategoryDTO> getUserCategories(Long userId) {
       List<Category> categories = categoryRepository.findAllCategoriesByUserId(userId);

       return categories.stream()
               .map(categoryMapper::toDTO)
               .collect(Collectors.toSet());
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with that id not found in database"));
    }

    public Category createAndAssignCategoryToUser(Long userId, AddCategoryDTO categoryDTO) {
        String categoryName = capitalizeFirstLetter(categoryDTO.getName().toLowerCase());
        Category category = categoryRepository.findByNameIgnoreCase(categoryName);

        if (category == null) {
            category = categoryMapper.toEntity(categoryDTO);
            category.setName(categoryName);
            categoryRepository.save(category);
        }

        assignCategoryToUser(userId, category.getId(), categoryDTO.getIconUrl());
        return category;
    }

    public void assignCategoryToUser(Long userId, Long categoryId, String iconUrl) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with that id not found in database"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the database"));

        UserCategory userCategory = UserCategory.builder()
                .user(user)
                .category(category)
                .assignedAt(LocalDateTime.now())
                .iconUrl(iconUrl)
                .build();
        userCategoryRepository.save(userCategory);
    }

    public void removeCategoryFromUser(Long userId, Long categoryId) {
        UserCategory userCategory = userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("User does not have this category assigned"));

        expenseRepository.deleteAllByCategoryIdAndUserUserId(userId, categoryId);

        userCategoryRepository.delete(userCategory);
    }
    public MonthlyBudgetDTO setCategoryBudgetAmount(Long userId, MonthlyBudgetDTO monthlyBudgetDTO) {
        UserCategory userCategory = userCategoryRepository.findByUserUserIdAndCategoryId(userId, monthlyBudgetDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("User does not have this category assigned"));

        YearMonth currentYearMonth = YearMonth.now();
        BigDecimal budgetToSet = monthlyBudgetDTO.getBudgetToSet();

        int updatedRows = monthlyBudgetRepository.updateBudgetAmountByUserIdAndCategoryIdAndYearMonth(userId, monthlyBudgetDTO.getCategoryId(), currentYearMonth.toString(), budgetToSet);

        if (updatedRows == 0) {
            MonthlyBudget newBudget = MonthlyBudget.builder()
                    .yearMonth(currentYearMonth.toString())
                    .budgetAmount(budgetToSet)
                    .spentAmount(BigDecimal.valueOf(0)) // COUNT THE EXPENSE FOR USER AND THAT CATEGORY
                    .category(userCategory.getCategory())
                    .updatedAt(LocalDateTime.now())
                    .user(userCategory.getUser())
                    .build();
            monthlyBudgetRepository.save(newBudget);
        }

        return MonthlyBudgetDTO.builder()
                .categoryId(monthlyBudgetDTO.getCategoryId())
                .budgetToSet(budgetToSet)
                .build();
    }

    @Transactional
    public void deleteMonthlyBudget(Long userId, Long categoryId) {
        YearMonth currentYearMonth = YearMonth.now();
        monthlyBudgetRepository.deleteByUserUserIdAndCategoryIdAndYearMonth(userId, categoryId, currentYearMonth.toString());
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
