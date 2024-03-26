package pl.sonmiike.financeapiservice.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.sonmiike.financeapiservice.expenses.ExpenseRepository;
import pl.sonmiike.financeapiservice.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private AutoCloseable openMocks;

    @BeforeEach
    public void init() {
        openMocks = MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository, userCategoryRepository, expenseRepository, userRepository, categoryMapper);
    }

    @AfterEach
    public void close() throws Exception {
        openMocks.close();
    }

    @Test
    public void getAllCategories_ShouldReturnAllCategories() {
        List<Category> categories = Arrays.asList(
                Category.builder().id(1L).name("Food").iconUrl("url1").build(),
                Category.builder().id(2L).name("Utilities").iconUrl("url2").build()
        );
        Set<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getIconUrl()))
                .collect(Collectors.toSet());

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDTO(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            return new CategoryDTO(c.getId(), c.getName(), c.getIconUrl());
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
        List<Category> categories = Arrays.asList(
                Category.builder().id(1L).name("Food").iconUrl("url1").build(),
                Category.builder().id(2L).name("Utilities").iconUrl("url2").build()
        );
        Set<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getIconUrl()))
                .collect(Collectors.toSet());

        when(categoryRepository.findAllCategoriesByUserId(userId)).thenReturn(categories);
        when(categoryMapper.toDTO(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            return new CategoryDTO(c.getId(), c.getName(), c.getIconUrl());
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
        Category category = Category.builder().id(categoryId).name("Food").iconUrl("url1").build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(categoryId);

        assertEquals(category, result);
        verify(categoryRepository).findById(categoryId);
    }
}
