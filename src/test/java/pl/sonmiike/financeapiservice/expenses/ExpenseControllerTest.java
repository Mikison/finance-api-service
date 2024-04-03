package pl.sonmiike.financeapiservice.expenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.sonmiike.financeapiservice.category.Category;
import pl.sonmiike.financeapiservice.category.UserCategoryRepository;
import pl.sonmiike.financeapiservice.security.auth.AuthService;
import pl.sonmiike.financeapiservice.security.auth.JwtService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserCategoryRepository userCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ExpenseDTO expenseDTO;
    private PagedExpensesDTO pagedExpensesDTO;

    @BeforeEach
    void setUp() {
        expenseDTO = ExpenseDTO.builder().id(1L).name("Groceries").date(LocalDate.now().toString()).amount(BigDecimal.valueOf(100).toString()).build();
        pagedExpensesDTO = PagedExpensesDTO.builder().page(0).totalPages(1).expenses(List.of(expenseDTO)).build();
    }

    @Test
    void getUserExpenses_ReturnsExpenses() throws Exception {
        Long userId = 1L;
        Mockito.when(authService.getUserId(any())).thenReturn(userId);
        Mockito.when(expenseService.getUserExpenses(userId, 0, 10)).thenReturn(pagedExpensesDTO);

        mockMvc.perform(get("/me/expenses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserExpensesByCategory_ReturnsExpenses() throws Exception {
        Long userId = 1L;
        Long categoryId = 2L;
        Mockito.when(authService.getUserId(any())).thenReturn(userId);
        Mockito.when(expenseService.getUserExpensesByCategory(userId, categoryId, 0, 10)).thenReturn(pagedExpensesDTO);

        mockMvc.perform(get("/me/expenses/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getExpenseById_ReturnsExpense() throws Exception {
        Long userId = 1L;
        Long expenseId = 3L;
        Mockito.when(authService.getUserId(any())).thenReturn(userId);
        Mockito.when(expenseService.getExpenseById(expenseId, userId)).thenReturn(expenseDTO);

        mockMvc.perform(get("/me/expenses/{expenseId}", expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getExpenses_WithFilters_ReturnsFilteredExpenses() throws Exception {
        Long userId = 1L;
        Mockito.when(authService.getUserId(any())).thenReturn(userId);

        Mockito.when(expenseService.findExpensesWithFilters(anyString(), any(), any(), any(), any(), any())).thenReturn(pagedExpensesDTO);

        mockMvc.perform(get("/me/expenses/filter")
                        .param("keyword", "food")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createExpense_CreatesExpense() throws Exception {
        Long userId = 1L;
        Long categoryId = 2L;
        AddExpesneDTO addExpesneDTO = new AddExpesneDTO("Groceries", "Walmart", LocalDate.now(), BigDecimal.valueOf(100));
        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);
        Mockito.doNothing().when(expenseService).createExpense(addExpesneDTO, userId, categoryId);

        mockMvc.perform(post("/me/expenses/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addExpesneDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteExpense_DeletesExpense() throws Exception {
        Long userId = 1L;
        Long expenseId = 3L;
        expenseDTO.setUserId(userId);
        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);
        Mockito.when(expenseService.getExpenseById(expenseId, userId)).thenReturn(expenseDTO);
        Mockito.doNothing().when(expenseService).deleteExpense(expenseId, userId);

        mockMvc.perform(delete("/me/expenses/{expenseId}", expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }





}
