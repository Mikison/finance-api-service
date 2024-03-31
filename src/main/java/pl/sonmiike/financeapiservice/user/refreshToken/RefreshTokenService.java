package pl.sonmiike.financeapiservice.user.refreshToken;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public RefreshToken createRefreshToken(String username) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found + " + username));

        RefreshToken refreshToken = user.getRefreshToken();
        // TODO If login and refresh is expired create new one
        if (refreshToken == null) {
            long refreshTokenExpiration = 5 * 60 * 60 * 10000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenExpiration))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken existingRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));

        if (existingRefreshToken.getExpirationTime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(existingRefreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return existingRefreshToken;
    }
}
