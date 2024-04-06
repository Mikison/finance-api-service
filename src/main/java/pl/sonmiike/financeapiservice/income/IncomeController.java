package pl.sonmiike.financeapiservice.income;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.sonmiike.financeapiservice.exceptions.custom.IdNotMatchingException;
import pl.sonmiike.financeapiservice.security.auth.AuthService;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me/income")
public class IncomeController {

    private final IncomeService incomeService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<PagedIncomesDTO> getUserIncome(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(incomeService.getUserIncome(userId, page, size));
    }

    @GetMapping("/{incomeId}") // TODO Make it for either admin or user scope only
    public ResponseEntity<IncomeDTO> getIncomeById(@PathVariable Long incomeId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        IncomeDTO incomeDTO = incomeService.getIncomeById(incomeId, userId);
        return ResponseEntity.ok(incomeDTO);
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedIncomesDTO> getIncomes(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(value = "fromAmount", required = false) BigDecimal fromAmount,
            @RequestParam(value = "toAmount", required = false) BigDecimal toAmount,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC)Pageable pageable) {
        return ResponseEntity.ok(incomeService.findIncomesWithFilters(keyword, dateFrom, dateTo, fromAmount, toAmount, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createIncome(@RequestBody @Valid AddIncomeDTO incomeDTO, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        incomeService.createIncome(incomeDTO, userId);

    }

    @PutMapping("/{incomeId}")
    public ResponseEntity<IncomeDTO> updateIncome(@PathVariable Long incomeId, @RequestBody @Valid IncomeDTO incomeDTO, Authentication authentication) {
        if (!incomeId.equals(incomeDTO.getId())) {
            throw new IdNotMatchingException("Income id in path and body must be the same");
        }
        Long userId = authService.getUserId(authentication);
        return ResponseEntity.ok(incomeService.updateIncome(incomeDTO, userId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{incomeId}")
    public void deleteIncome(@PathVariable Long incomeId, Authentication authentication) {
        Long userId = authService.getUserId(authentication);
        incomeService.deleteIncome(incomeId, userId);
    }


}
