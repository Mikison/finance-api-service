package pl.sonmiike.financeapiservice.income;

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
import pl.sonmiike.financeapiservice.security.auth.AuthService;
import pl.sonmiike.financeapiservice.security.auth.JwtService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(IncomeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncomeService incomeService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private IncomeDTO incomeDTO;
    private PagedIncomesDTO pagedIncomesDTO;

    @BeforeEach
    void setUp() {
        incomeDTO = IncomeDTO.builder().name("Salary").description("Monthly salary").amount(new BigDecimal("1000")).incomeDate(LocalDate.now()).id(1L).build();
        pagedIncomesDTO = PagedIncomesDTO.builder().currentPage(0).totalPages(1).incomes(List.of(incomeDTO)).build();
    }

    @Test
    void getUserIncome_ReturnsIncomePage() throws Exception {
        Long userId = 1L;

        Mockito.when(authService.getUserId(any())).thenReturn(userId);
        Mockito.when(incomeService.getUserIncome(userId, 0, 10)).thenReturn(pagedIncomesDTO);

        mockMvc.perform(get("/me/income")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.incomes[0].name").value("Salary"))
                .andExpect(jsonPath("$.incomes[0].description").value("Monthly salary"))
                .andExpect(jsonPath("$.incomes[0].amount").value(1000));
    }

    @Test
    void getIncomeById_ReturnsIncome() throws Exception {
        Long userId = 1L;
        Long incomeId = 2L;

        Mockito.when(authService.getUserId(any())).thenReturn(userId);
        Mockito.when(incomeService.getIncomeById(incomeId, userId)).thenReturn(incomeDTO);

        mockMvc.perform(get("/me/income/{incomeId}", incomeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salary"))
                .andExpect(jsonPath("$.description").value("Monthly salary"))
                .andExpect(jsonPath("$.amount").value(1000));
    }

    @Test
    void getIncomes_WithFilters_ReturnsFilteredIncomes() throws Exception {
        Long userId = 1L;

        Mockito.when(authService.getUserId(any())).thenReturn(userId);
        Mockito.when(incomeService.findIncomesWithFilters(anyString(), any(), any(), any(), any(), any())).thenReturn(pagedIncomesDTO);

        mockMvc.perform(get("/me/income/filter")
                        .param("keyword", "salary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.incomes[0].name").value("Salary"))
                .andExpect(jsonPath("$.incomes[0].description").value("Monthly salary"))
                .andExpect(jsonPath("$.incomes[0].amount").value(1000));
    }


    @Test
    void createIncome_CreatesIncome() throws Exception {
        Long userId = 1L;
        AddIncomeDTO addIncomeDTO = new AddIncomeDTO(LocalDate.now(), "Salary", "Monthly salary", new BigDecimal("1000"));

        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);
        Mockito.doNothing().when(incomeService).createIncome(addIncomeDTO, userId);

        mockMvc.perform(post("/me/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addIncomeDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateIncome_Success() throws Exception {
        Long userId = 1L;
        given(authService.getUserId(any())).willReturn(userId);
        given(incomeService.updateIncome(any(IncomeDTO.class), eq(userId))).willReturn(incomeDTO);

        mockMvc.perform(put("/me/income/{incomeId}", incomeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(incomeDTO)));
    }

    @Test
    void updateIncome_ThrowsIdNotMatchingException() throws Exception {
        Long userId = 1L;
        given(authService.getUserId(any())).willReturn(userId);
        given(incomeService.updateIncome(any(IncomeDTO.class), eq(userId))).willReturn(incomeDTO);

        System.out.println(incomeDTO);
        mockMvc.perform(put("/me/income/{incomeId}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteIncome_DeletesIncomeSuccessfully() throws Exception {
        Long userId = 1L;
        Long incomeId = 2L;

        Mockito.when(authService.getUserId(Mockito.any())).thenReturn(userId);
        Mockito.doNothing().when(incomeService).deleteIncome(incomeId, userId);

        mockMvc.perform(delete("/me/income/{incomeId}", incomeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }




}
