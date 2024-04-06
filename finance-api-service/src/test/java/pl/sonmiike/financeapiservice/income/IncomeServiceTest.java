package pl.sonmiike.financeapiservice.income;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class IncomeServiceTest {


    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private IncomeMapper incomeMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private IncomeService incomeService;

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
    void testGetUserIncome() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        List<Income> incomeList = new ArrayList<>();
        incomeList.add(Income.builder()
                .id(1L)
                .incomeDate(LocalDate.now())
                .name("Salary")
                .description("Monthly Salary")
                .amount(BigDecimal.valueOf(5000))
                .user(new UserEntity())
                .build());

        Page<Income> incomePage = new PageImpl<>(incomeList);

        when(incomeRepository.findByUserUserId(userId, PageRequest.of(page, size))).thenReturn(incomePage);

        List<IncomeDTO> incomeDTOList = new ArrayList<>();
        incomeDTOList.add(IncomeDTO.builder()
                .id(1L)
                .incomeDate(LocalDate.now())
                .name("Salary")
                .description("Monthly Salary")
                .amount(BigDecimal.valueOf(5000))
                .build());

        PagedIncomesDTO expectedPagedIncomesDTO = PagedIncomesDTO.builder()
                .currentPage(0)
                .totalPages(1)
                .incomes(incomeDTOList)
                .build();

        when(incomeMapper.toPagedDTO(incomePage)).thenReturn(expectedPagedIncomesDTO);

        // When
        PagedIncomesDTO result = incomeService.getUserIncome(userId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(expectedPagedIncomesDTO, result);
        verify(incomeRepository, times(1)).findByUserUserId(userId, PageRequest.of(page, size));
        verify(incomeMapper, times(1)).toPagedDTO(incomePage);
    }

    @Test
    void getIncomeById_ShouldReturnIncomeDTO() {
        Long incomeId = 1L, userId = 1L;
        Income income = new Income(incomeId, LocalDate.now(), "Test", "beka", BigDecimal.valueOf(100.00), null);
        IncomeDTO incomeDTO = new IncomeDTO(incomeId, LocalDate.now(), "Test", "beka", BigDecimal.valueOf(100.00), userId);
        when(incomeRepository.findByIdAndUserUserId(eq(incomeId), eq(userId))).thenReturn(Optional.of(income));
        when(incomeMapper.toDTO(eq(income))).thenReturn(incomeDTO);

        IncomeDTO result = incomeService.getIncomeById(incomeId, userId);

        assertEquals(incomeDTO, result);
        assertNotNull(result);
        verify(incomeRepository).findByIdAndUserUserId(eq(incomeId), eq(userId));
        verify(incomeMapper).toDTO(eq(income));
    }

    @Test
    void getIncomeById_ShouldThrowResourceNotFoundException() {
        Long incomeId = 1L, userId = 1L;

        when(incomeRepository.findByIdAndUserUserId(eq(incomeId), eq(userId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> incomeService.getIncomeById(incomeId, userId));

        verify(incomeRepository).findByIdAndUserUserId(incomeId, userId);
        verify(incomeMapper, never()).toDTO(any(Income.class));
    }

    @Test
    void createIncome_SuccessfulCreation() {
        Long userId = 1L;
        AddIncomeDTO addIncomeDTO = new AddIncomeDTO(LocalDate.now(), "Salary", "May Salary", BigDecimal.valueOf(150.00));
        Income income = new Income(1L, LocalDate.now(), "Salary", "May Salary", BigDecimal.valueOf(150.00), UserEntity.builder().userId(userId).build());

        when(incomeMapper.toEntity(eq(addIncomeDTO))).thenReturn(income);
        when(userService.getUserById(eq(userId))).thenReturn(UserEntity.builder().userId(userId).build());

        incomeService.createIncome(addIncomeDTO, userId);

        verify(incomeMapper, times(1)).toEntity(eq(addIncomeDTO));
        verify(userService, times(1)).getUserById(eq(userId));
        verify(incomeRepository, times(1)).save(eq(income));


    }

    @Test
    void testUpdateIncome_ShouldUpdateWithSuccess() {
        Long userId = 1L;
        IncomeDTO incomeDTO = new IncomeDTO(1L, LocalDate.now(), "Salary", "May Salary", BigDecimal.valueOf(150.00), userId);
        Income income = new Income(1L, LocalDate.now(), "Salary", "May Salary", BigDecimal.valueOf(150.00), UserEntity.builder().userId(userId).build());

        when(incomeMapper.toEntity(eq(incomeDTO))).thenReturn(income);
        when(userService.getUserById(eq(userId))).thenReturn(UserEntity.builder().userId(userId).build());
        when(incomeRepository.save(eq(income))).thenReturn(income);
        when(incomeMapper.toDTO(eq(income))).thenReturn(incomeDTO);
        when(incomeRepository.existsById(eq(incomeDTO.getId()))).thenReturn(true);

        IncomeDTO result = incomeService.updateIncome(incomeDTO, userId);

        assertNotNull(result);
        assertEquals(incomeDTO, result);
        verify(incomeMapper, times(1)).toEntity(eq(incomeDTO));
        verify(userService, times(1)).getUserById(eq(userId));
        verify(incomeRepository, times(1)).save(eq(income));
        verify(incomeMapper, times(1)).toDTO(eq(income));
    }

    @Test
    void testUpdateIncome_ShouldThrowResourceNotFoundException() {
        Long userId = 1L;
        IncomeDTO incomeDTO = new IncomeDTO(1L, LocalDate.now(), "Salary", "May Salary", BigDecimal.valueOf(150.00), userId);

        when(incomeRepository.existsById(eq(incomeDTO.getId()))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> incomeService.updateIncome(incomeDTO, userId));

        verify(incomeRepository, times(1)).existsById(eq(incomeDTO.getId()));
        verify(incomeMapper, never()).toEntity(any(IncomeDTO.class));
        verify(userService, never()).getUserById(any(Long.class));
        verify(incomeRepository, never()).save(any(Income.class));
        verify(incomeMapper, never()).toDTO(any(Income.class));
    }

    @Test
    void testDeleteIncome() {
        Long incomeId = 1L, userId = 1L;

        incomeService.deleteIncome(incomeId, userId);

        verify(incomeRepository, times(1)).deleteIncomeByIdAndUserUserId(eq(incomeId), eq(userId));
        assertEquals(incomeRepository.count(), 0);
    }

    @Test
    public void testFindIncomesWithFilters() {
        // Setup
        String keyword = "test";
        LocalDate dateFrom = LocalDate.of(2022, 1, 1);
        LocalDate dateTo = LocalDate.of(2022, 12, 31);
        BigDecimal amountFrom = new BigDecimal("100.00");
        BigDecimal amountTo = new BigDecimal("1000.00");
        Pageable pageable = Pageable.unpaged();

        Page<Income> mockPage = mock(Page.class);
        PagedIncomesDTO mockPagedIncomesDTO = new PagedIncomesDTO(mockPage.getNumber(), mockPage.getTotalPages(), mockPage.getContent().stream().map(incomeMapper::toDTO).toList());
        mockPagedIncomesDTO.setIncomes(Collections.emptyList()); // Assume an empty list for simplicity

        when(incomeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);
        when(incomeMapper.toPagedDTO(mockPage)).thenReturn(mockPagedIncomesDTO);

        // Execute
        PagedIncomesDTO result = incomeService.findIncomesWithFilters(keyword, dateFrom, dateTo, amountFrom, amountTo, pageable);

        // Assert
        assertEquals(mockPagedIncomesDTO, result);
        verify(incomeRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(incomeMapper, times(1)).toPagedDTO(mockPage);
    }
}
