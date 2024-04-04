package pl.sonmiike.financeapiservice.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudget;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetDTO;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetRepository;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.expenses.ExpenseRepository;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserCategoryRepository userCategoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private MonthlyBudgetRepository monthlyBudgetRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private AutoCloseable openMocks;

    @BeforeEach
    public void init() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void close() throws Exception {
        openMocks.close();
    }

    @Test
    public void getAllCategories_ShouldReturnAllCategories() {
        List<Category> categories = Arrays.asList(Category.builder().id(1L).name("Food").build(), Category.builder().id(2L).name("Utilities").build());
        Set<CategoryDTO> categoryDTOs = categories.stream().map(category -> new CategoryDTO(category.getId(), category.getName())).collect(Collectors.toSet());

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDTO(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            return new CategoryDTO(c.getId(), c.getName());
        });

        Set<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(categoryDTOs.size(), result.size());
        assertTrue(result.containsAll(categoryDTOs));
        verify(categoryRepository).findAll();
        verify(categoryMapper, times(categories.size())).toDTO(any(Category.class));
    }


    @Test
    void getUserCategories_ShouldReturnUserCategories() {
        Long userId = 1L;
        List<Category> categories = Arrays.asList(Category.builder().id(1L).name("Food").build(), Category.builder().id(2L).name("Utilities").build());
        Set<CategoryDTO> categoryDTOs = categories.stream().map(category -> new CategoryDTO(category.getId(), category.getName())).collect(Collectors.toSet());

        when(categoryRepository.findAllCategoriesByUserId(userId)).thenReturn(categories);
        when(categoryMapper.toDTO(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            return new CategoryDTO(c.getId(), c.getName());
        });

        Set<CategoryDTO> result = categoryService.getUserCategories(userId);

        assertEquals(categoryDTOs.size(), result.size());
        assertTrue(result.containsAll(categoryDTOs));
        verify(categoryRepository).findAllCategoriesByUserId(userId);
        verify(categoryMapper, times(categories.size())).toDTO(any(Category.class));
    }


    @Test
    void getCategoryById_ShouldReturnCategory() {
        Long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("Food").build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(categoryId);

        assertEquals(category, result);
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getCategoryById_ShouldThrowResourceNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void testCreateAndAssignCategoryToUserWithNewCategory() {
        Long userId = 1L;
        AddCategoryDTO categoryDTO = AddCategoryDTO.builder().name("TestCategory").build();
        Category category = Category.builder().id(1L).name("TestCategory").build();
        UserEntity userEntity = UserEntity.builder().userId(userId).build();

        when(categoryRepository.findByNameIgnoreCase(anyString())).thenReturn(null);
        when(categoryMapper.toEntity(any(AddCategoryDTO.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(userCategoryRepository.save(any(UserCategory.class))).thenReturn(new UserCategory());

        // When
        Category createdCategory = categoryService.createAndAssignCategoryToUser(userId, categoryDTO);

        assertEquals(category.getName(), createdCategory.getName());
        verify(categoryRepository, times(1)).save(category);
        verify(userCategoryRepository, times(1)).save(any(UserCategory.class));
        assertNotNull(createdCategory);
    }

    @Test
    void testCreateAndAssignCategoryToUserWithExistingCategory() {
        // Given
        Long userId = 1L;
        AddCategoryDTO categoryDTO = AddCategoryDTO.builder().name("TestCategory").build();
        Category category = Category.builder().id(1L).name("TestCategory").build();
        UserEntity userEntity = UserEntity.builder().userId(userId).build();

        when(categoryRepository.findByNameIgnoreCase(anyString())).thenReturn(category);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(userCategoryRepository.save(any(UserCategory.class))).thenReturn(new UserCategory());

        // When
        Category createdCategory = categoryService.createAndAssignCategoryToUser(userId, categoryDTO);

        assertEquals(category.getName(), createdCategory.getName());
        verify(categoryRepository, never()).save(category);
        verify(userCategoryRepository, times(1)).save(any(UserCategory.class));
        assertNotNull(createdCategory);
    }

    @Test
    void testAssignUserToNonExistingCategory() {
        Long userId = 1L;
        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserEntity.builder().userId(userId).build()));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.assignCategoryToUser(userId, categoryId, ""));

        verify(userCategoryRepository, never()).save(any(UserCategory.class));
    }

    @Test
    void testAssignCategoryToNonExistingUser() {
        Long userId = 999L;
        Long categoryId = 1L;
        Category category = Category.builder().id(categoryId).name("TestCategory").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.assignCategoryToUser(userId, categoryId, ""));

        verify(userCategoryRepository, never()).save(any(UserCategory.class));
    }


    @Test
    void testUnassignCategoryFromUser_Success() {
        Long userId = 1L;
        Long categoryId = 1L;
        UserCategory userCategory = UserCategory.builder().id(1L).build();

        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId)).thenReturn(Optional.of(userCategory));

        categoryService.removeCategoryFromUser(userId, categoryId);

        assertEquals(userCategoryRepository.count(), 0);
        verify(expenseRepository, times(1)).deleteAllByCategoryIdAndUserUserId(userId, categoryId);
        verify(userCategoryRepository, times(1)).delete(userCategory);
    }

    @Test
    void testUnassignCategoryFromUser_throwException() {
        Long userId = 1L;
        Long categoryId = 1L;

        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> categoryService.removeCategoryFromUser(userId, categoryId));

        verify(expenseRepository, never()).deleteAllByCategoryIdAndUserUserId(userId, categoryId);
        verify(userCategoryRepository, never()).delete(any(UserCategory.class));
    }

    @Test
    void whenCreatingNewBudget_thenNewBudgetIsSaved() {
        Long userId = 1L;
        MonthlyBudgetDTO inputDTO = MonthlyBudgetDTO.builder().budgetToSet(BigDecimal.valueOf(1000)).build();
        UserCategory mockUserCategory = UserCategory.builder().id(1L).build();

        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, inputDTO.getCategoryId())).thenReturn(Optional.of(mockUserCategory));
        when(monthlyBudgetRepository.updateBudgetAmountByUserIdAndCategoryIdAndYearMonth(eq(userId), eq(inputDTO.getCategoryId()), any(String.class), eq(inputDTO.getBudgetToSet()))).thenReturn(0);
        when(monthlyBudgetRepository.save(any(MonthlyBudget.class))).thenAnswer(i -> i.getArgument(0));

        // Execution
        MonthlyBudgetDTO result = categoryService.setCategoryBudgetAmount(userId, inputDTO);

        // Verification
        verify(monthlyBudgetRepository).save(any(MonthlyBudget.class));
        assertNotNull(result);
        assertEquals(inputDTO.getBudgetToSet(), result.getBudgetToSet());
    }

    @Test
    void whenUpdatingExistingBudget_thenBudgetIsUpdated() {
        // Setup
        Long userId = 1L;
        MonthlyBudgetDTO inputDTO = MonthlyBudgetDTO.builder().budgetToSet(BigDecimal.valueOf(1000)).build();
        UserCategory mockUserCategory = UserCategory.builder().id(1L).build();
        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, inputDTO.getCategoryId())).thenReturn(Optional.of(mockUserCategory));
        when(monthlyBudgetRepository.updateBudgetAmountByUserIdAndCategoryIdAndYearMonth(eq(userId), eq(inputDTO.getCategoryId()), any(String.class), eq(inputDTO.getBudgetToSet()))).thenReturn(1);

        MonthlyBudgetDTO result = categoryService.setCategoryBudgetAmount(userId, inputDTO);

        // Verification
        verify(monthlyBudgetRepository, never()).save(any(MonthlyBudget.class));
        assertNotNull(result);
        assertEquals(inputDTO.getBudgetToSet(), result.getBudgetToSet());
    }

    @Test
    void testSettingBudgetWhenCategoryIsNotAssignedToUserOrDoesntExist() {
        Long userId = 1L;
        MonthlyBudgetDTO inputDTO = MonthlyBudgetDTO.builder().budgetToSet(BigDecimal.valueOf(1000)).build();

        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, inputDTO.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.setCategoryBudgetAmount(userId, inputDTO));

        verify(monthlyBudgetRepository, never()).save(any(MonthlyBudget.class));
    }

    @Test
    void testDeleteMonthlyBudgedWithCategoryAssigned() {
        Long userId = 1L;
        Long categoryId = 1L;
        YearMonth currentYearMonth = YearMonth.now();
        when(userCategoryRepository.existsByUserUserIdAndCategoryId(userId, categoryId)).thenReturn(true);

        categoryService.deleteMonthlyBudget(userId, categoryId);

        verify(monthlyBudgetRepository, times(1)).deleteByUserUserIdAndCategoryIdAndYearMonth(userId, categoryId, currentYearMonth.toString());
    }

    @Test
    void testDeleteMonthlyBudgedWithoutCategoryAssigned() {
        Long userId = 1L;
        Long categoryId = 1L;
        YearMonth currentYearMonth = YearMonth.now();
        when(userCategoryRepository.existsByUserUserIdAndCategoryId(userId, categoryId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteMonthlyBudget(userId, categoryId));

        verify(monthlyBudgetRepository, never()).deleteByUserUserIdAndCategoryIdAndYearMonth(userId, categoryId, currentYearMonth.toString());
    }

}
