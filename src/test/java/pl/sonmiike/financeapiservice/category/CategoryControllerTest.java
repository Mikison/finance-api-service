package pl.sonmiike.financeapiservice.category;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetDTO;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.security.auth.AuthService;
import pl.sonmiike.financeapiservice.security.auth.JwtService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserCategoryRepository userCategoryRepository;


    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Health").build();
        categoryDTO = CategoryDTO.builder().id(1L).name("Health").build();
    }



    @Test
    void getAllCategories_AdminAccess() throws Exception {
        given(categoryService.getAllCategories()).willReturn(Set.of(categoryDTO));

        mockMvc.perform(get("/me/category/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(Set.of(categoryDTO))))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Health"));

    }
    @Test
    void getUserCategories() throws Exception {
        given(categoryService.getUserCategories(anyLong())).willReturn(Set.of(categoryDTO));

        mockMvc.perform(get("/me/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Set.of(categoryDTO))))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Health"));
    }

    @Test
    void createCategory_ShouldCreateAndAssignCategoryToUser() throws Exception {
        Long userId = 1L;
        AddCategoryDTO addCategoryDTO = new AddCategoryDTO();
        addCategoryDTO.setName("Utilities");
        addCategoryDTO.setIconUrl("http://example.com/icon.png");


        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);
        Mockito.when(categoryService.createAndAssignCategoryToUser(userId, addCategoryDTO)).thenReturn(category);

        mockMvc.perform(post("/me/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCategoryDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void unassignCategory_ShouldUnassignCategory() throws Exception {
        Long userId = 1L;
        Long categoryId = 2L;

        Mockito.doNothing().when(categoryService).removeCategoryFromUser(userId, categoryId);
        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);

        mockMvc.perform(delete("/me/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void setBudgetForSpecificCategory_ShouldSetBudget() throws Exception {
        Long userId = 1L;
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO(1L, BigDecimal.valueOf(100));

        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);
        Mockito.when(categoryService.setCategoryBudgetAmount(userId, monthlyBudgetDTO)).thenReturn(monthlyBudgetDTO);

        mockMvc.perform(post("/me/category/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(monthlyBudgetDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(monthlyBudgetDTO)))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.budgetToSet").value(100));
    }

    @Test
    void deleteBudgetForSpecificCategory_ShouldDeleteBudget() throws Exception {
        Long userId = 1L;
        Long categoryId = 2L;

        Mockito.doNothing().when(categoryService).deleteMonthlyBudget(userId, categoryId);
        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);

        mockMvc.perform(delete("/me/category/budget/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



}