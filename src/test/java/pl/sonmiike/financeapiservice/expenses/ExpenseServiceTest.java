package pl.sonmiike.financeapiservice.expenses;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import pl.sonmiike.financeapiservice.category.Category;
import pl.sonmiike.financeapiservice.category.CategoryService;
import pl.sonmiike.financeapiservice.category.UserCategory;
import pl.sonmiike.financeapiservice.category.UserCategoryRepository;
import pl.sonmiike.financeapiservice.exceptions.custom.IdNotMatchingException;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private UserCategoryRepository userCategoryRepository;
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ExpenseMapper expenseMapper;


    @InjectMocks
    private ExpenseService expenseService;

    @Captor
    private ArgumentCaptor<Expense> expenseCaptor;


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
    void testGetUserExpenses() {
        Long userId = 1L;
        int page = 0;
        int size = 10;
        Expense mockExpense = mock(Expense.class);

        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(mockExpense));

        PagedExpensesDTO mockPagedExpensesDTO = mock(PagedExpensesDTO.class);

        when(expenseRepository.findExpenseByUserUserId(eq(userId), any(PageRequest.class))).thenReturn(expensePage);
        when(expenseMapper.toPagedDTO(any(Page.class))).thenReturn(mockPagedExpensesDTO);

        PagedExpensesDTO result = expenseService.getUserExpenses(userId, page, size);
        assertEquals(mockPagedExpensesDTO, result);
        verify(expenseRepository).findExpenseByUserUserId(eq(userId), eq(PageRequest.of(page, size)));
        verify(expenseMapper).toPagedDTO(expensePage);
    }


    @Test
    public void testGetUserExpensesByCategory() {
        Long userId = 1L;
        Long categoryId = 1L;
        int page = 0;
        int size = 10;

        Expense testExpense = new Expense();
        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(testExpense), PageRequest.of(page, size), 1);

        PagedExpensesDTO expectedDto = mock(PagedExpensesDTO.class);

        when(expenseRepository.findExpenseByUserUserIdAndCategoryId(eq(userId), eq(categoryId), any(PageRequest.class))).thenReturn(expensePage);
        when(expenseMapper.toPagedDTO(any(Page.class))).thenReturn(expectedDto);

        PagedExpensesDTO result = expenseService.getUserExpensesByCategory(userId, categoryId, page, size);

        assertEquals(expectedDto, result);
        verify(expenseRepository).findExpenseByUserUserIdAndCategoryId(eq(userId), eq(categoryId), eq(PageRequest.of(page, size)));
        verify(expenseMapper).toPagedDTO(expensePage);
    }

    @Test
    public void testGetExpenseById_Success() {
        Long id = 1L;
        Long userId = 1L;
        Expense testExpense = new Expense();
        ExpenseDTO expectedDto = new ExpenseDTO();

        when(expenseRepository.findByIdAndUserUserId(eq(id), eq(userId))).thenReturn(Optional.of(testExpense));
        when(expenseMapper.toDTO(any(Expense.class))).thenReturn(expectedDto);

        ExpenseDTO result = expenseService.getExpenseById(id, userId);

        assertNotNull(result, "The result should not be null.");
        assertEquals(expectedDto, result, "The returned DTO should match the expected DTO.");
        verify(expenseRepository).findByIdAndUserUserId(id, userId);
        verify(expenseMapper).toDTO(testExpense);
    }

    @Test
    public void testGetExpenseById_NotFound_ThrowsException() {
        Long id = 1L;
        Long userId = 1L;

        when(expenseRepository.findByIdAndUserUserId(eq(id), eq(userId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> expenseService.getExpenseById(id, userId), "Expected getExpenseById to throw, but it didn't");

        verify(expenseRepository).findByIdAndUserUserId(id, userId);
        verify(expenseMapper, never()).toDTO(any(Expense.class));
    }

    @Test
    public void createExpense_SuccessfulCreation() {
        Long userId = 1L, categoryId = 1L;
        AddExpesneDTO expenseDTO = new AddExpesneDTO("apteka", "leki", LocalDate.now(), BigDecimal.valueOf(150));
        Expense expense = new Expense(1L,"apteka", "leki", LocalDate.now(), BigDecimal.valueOf(150), null, null);

        UserCategory userCategory = new UserCategory();
        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId))
                .thenReturn(Optional.of(userCategory));
        when(expenseMapper.toEntity(expenseDTO)).thenReturn(expense);
        when(userService.getUserById(userId)).thenReturn(new UserEntity());
        when(categoryService.getCategoryById(categoryId)).thenReturn(new Category());
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);


        expenseService.createExpense(expenseDTO, userId, categoryId);

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    public void createExpense_IdNotMatchingException() {
        Long userId = 1L, categoryId = 1L;
        AddExpesneDTO expenseDTO = new AddExpesneDTO();

        when(userCategoryRepository.findByUserUserIdAndCategoryId(userId, categoryId))
                .thenReturn(Optional.empty());


        assertThrows(IdNotMatchingException.class, () -> expenseService.createExpense(expenseDTO, userId, categoryId));
    }

    @Test
    public void findExpensesWithFilters_AllFilters() {
        String keyword = "test";
        LocalDate dateFrom = LocalDate.of(2020, 1, 1);
        LocalDate dateTo = LocalDate.of(2020, 12, 31);
        BigDecimal amountFrom = BigDecimal.valueOf(100);
        BigDecimal amountTo = BigDecimal.valueOf(500);
        PageRequest pageable = PageRequest.of(0, 10);

        Expense testExpense = new Expense(); // Assuming a suitable constructor or setters
        PageImpl<Expense> pagedExpenses = new PageImpl<>(Collections.singletonList(testExpense), pageable, 1);
        PagedExpensesDTO expectedDTO = new PagedExpensesDTO(pagedExpenses.getNumber(), pagedExpenses.getTotalPages(), pagedExpenses.getContent().stream().map(expenseMapper::toDTO).toList());

        when(expenseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagedExpenses);
        when(expenseMapper.toPagedDTO(any())).thenReturn(expectedDTO);

        PagedExpensesDTO result = expenseService.findExpensesWithFilters(keyword, dateFrom, dateTo, amountFrom, amountTo, pageable);


        assertEquals(expectedDTO, result);
        verify(expenseRepository).findAll(any(Specification.class), eq(pageable));
        verify(expenseMapper).toPagedDTO(pagedExpenses);
    }

    @Test
    public void updateExpense_SuccessfulUpdate() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        Expense expense = new Expense();

        when(expenseRepository.existsById(expenseDTO.getId())).thenReturn(true);
        when(expenseMapper.toEntity(expenseDTO)).thenReturn(expense);
        when(userService.getUserById(userId)).thenReturn(user);

        expenseService.updateExpense(expenseDTO, userId);

        verify(expenseRepository).save(expenseCaptor.capture());
        Expense savedExpense = expenseCaptor.getValue();
        assertEquals(userId, savedExpense.getUser().getUserId());
    }

    @Test
    public void updateExpense_ExpenseNotFound_ThrowsException() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        Long userId = 1L;

        when(expenseRepository.existsById(expenseDTO.getId())).thenReturn(false);

        Exception exception = assertThrows(IdNotMatchingException.class, () -> expenseService.updateExpense(expenseDTO, userId));

        assertEquals("Expense not found", exception.getMessage());
    }


    @Test
    public void deleteExpense_VerifyRepositoryInteraction() {
        Long expenseId = 1L;
        Long userId = 1L;

        expenseService.deleteExpense(expenseId, userId);

        verify(expenseRepository).deleteByIdAndUserUserId(expenseId, userId);
    }


}
