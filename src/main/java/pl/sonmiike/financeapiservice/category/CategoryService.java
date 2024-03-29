package pl.sonmiike.financeapiservice.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.expenses.ExpenseRepository;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;

import java.time.LocalDateTime;
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

        assignCategoryToUser(userId, category.getId());
        return category;
    }

    public void assignCategoryToUser(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with that id not found in database"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the database"));

        UserCategory userCategory = UserCategory.builder()
                .user(user)
                .category(category)
                .assignedAt(LocalDateTime.now())
                .build();
        userCategoryRepository.save(userCategory);
    }


    // THINK IF THIS IS NECESSARY
//    @Transactional
//    public void removeCategoryFromUser(Long userId, Long categoryId) {
//        UserCategory userCategory = userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId)
//                .orElseThrow(() -> new ResourceNotFoundException("User does not have this category assigned"));
//
//        // Remove all expenses from category that THIS user has
//        expenseRepository.deleteAllByCategoryIdAndUserUserId(userId, categoryId);
//        // Remove category from user
//        userCategoryRepository.delete(userCategory);
//
//
//   }

    public void removeCategoryFromUser(Long userId, Long categoryId) {
        UserCategory userCategory = userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("User does not have this category assigned"));

        // Remove all expenses from category that THIS user has
        expenseRepository.deleteAllByCategoryIdAndUserUserId(userId, categoryId);
        // Remove category from user
        userCategoryRepository.delete(userCategory);
    }

    public void setCategoryBudgetAmount(Long userId, Long CategoryId) {
        // TODO
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
