package pl.sonmiike.financeapiservice.user.refreshToken;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.sonmiike.financeapiservice.user.UserEntity;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Enter refresh token")
    private String refreshToken;

    @Column(nullable = false)
    private Instant expirationTime;
    @OneToOne
    private UserEntity user;
}