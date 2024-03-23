package pl.sonmiike.financeapiservice.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.sonmiike.financeapiservice.validators.email.ValidEmail;
import pl.sonmiike.financeapiservice.validators.password.ValidPassword;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    @ValidEmail
    @NotNull
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotNull
    @ValidPassword
    private String password;
}
