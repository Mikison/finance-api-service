package pl.sonmiike.financeapiservice.income;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeDTO {

    @NotNull
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate incomeDate;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 100, message = "Description can have max 100 characters")
    private String description;

    @DecimalMin(value = "0.00", inclusive = false)
    @NotNull
    private BigDecimal amount;

    private Long userId;
}
