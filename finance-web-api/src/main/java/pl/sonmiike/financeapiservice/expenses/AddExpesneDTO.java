package pl.sonmiike.financeapiservice.expenses;

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
public class AddExpesneDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 100, message = "Description can have max 100 characters")
    private String description;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotNull
    @DecimalMin(value = "0.0", message = "Amount must be greater than 0", inclusive = false)
    private BigDecimal amount;
}
