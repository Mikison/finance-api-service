package pl.sonmiike.financeapiservice.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.sonmiike.financeapiservice.category.monthlyBudget.MonthlyBudgetDTO;
import pl.sonmiike.financeapiservice.security.auth.AuthService;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void givenAdminAuthority_whenGetAllCategories_thenOk() throws Exception {
        Set<CategoryDTO> categories = new HashSet<>();
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/me/category"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser
    void whenGetUserCategories_thenOk() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authService.getUserId(authentication)).thenReturn(1L);

        Set<CategoryDTO> userCategories = new HashSet<>();
        when(categoryService.getUserCategories(1L)).thenReturn(userCategories);

        mockMvc.perform(get("/me/category/user")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(authService).getUserId(authentication);
        verify(categoryService).getUserCategories(1L);
    }

    @Test
    @WithMockUser // Assumes default user authority is enough, adjust if needed
    void whenCreateCategory_thenOk() throws Exception {
        AddCategoryDTO addCategoryDTO = new AddCategoryDTO("New Category", "");
        Authentication authentication = mock(Authentication.class); // Adjust based on your entity
        when(authService.getUserId(authentication)).thenReturn(1L);
        Category createdCategory = new Category(); // Adjust constructor based on your entity
        when(categoryService.createAndAssignCategoryToUser(eq(1L), any(AddCategoryDTO.class))).thenReturn(createdCategory);

        mockMvc.perform(post("/me/category")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addCategoryDTO)))
                .andExpect(status().isCreated());

        verify(authService).getUserId(any(Authentication.class));
        verify(categoryService).createAndAssignCategoryToUser(eq(1L), any(AddCategoryDTO.class));
    }


    @Test
    @WithMockUser // Assumes default user authority is enough, adjust if needed
    void whenUnassignCategory_thenNoContent() throws Exception {
        Authentication authentication = mock(Authentication.class); // Adjust based on your entity
        when(authService.getUserId(authentication)).thenReturn(1L);
        doNothing().when(categoryService).removeCategoryFromUser(anyLong(), anyLong());

        mockMvc.perform(delete("/me/category/{categoryId}", 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent());

        verify(categoryService).removeCategoryFromUser(anyLong(), eq(1L));
    }

    @Test
    @WithMockUser // Adjust authority if needed
    void whenSetBudgetForSpecificCategory_thenOk() throws Exception {
        MonthlyBudgetDTO monthlyBudgetDTO = new MonthlyBudgetDTO();
        Authentication authentication = mock(Authentication.class);
        when(authService.getUserId(authentication)).thenReturn(1L);
        when(categoryService.setCategoryBudgetAmount(eq(1L), any(MonthlyBudgetDTO.class))).thenReturn(monthlyBudgetDTO);

        mockMvc.perform(post("/me/category/budget")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(monthlyBudgetDTO)))
                .andExpect(status().isOk());

        verify(authService).getUserId(any(Authentication.class));
        verify(categoryService).setCategoryBudgetAmount(eq(1L), any(MonthlyBudgetDTO.class));
    }





}
