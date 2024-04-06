package pl.sonmiike.financeapiservice.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.sonmiike.financeapiservice.validators.email.ValidEmail;
import pl.sonmiike.financeapiservice.validators.password.ValidPassword;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
