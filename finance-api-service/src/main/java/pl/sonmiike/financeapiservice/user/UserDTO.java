package pl.sonmiike.financeapiservice.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class UserDTO {

    @NotBlank
    private Long id;

    @Size(min = 3, max = 24)
    @NotBlank
    private String username;

    @Email
    private String email;

    @NotNull
    private Collection<?> roles;
}
