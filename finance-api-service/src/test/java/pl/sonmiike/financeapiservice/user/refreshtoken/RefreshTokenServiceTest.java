package pl.sonmiike.financeapiservice.user.refreshtoken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshToken;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenRepository;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private AutoCloseable openMocks;

    @BeforeEach
    public void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void whenUserExistsAndNoRefreshToken_ThenCreateNewRefreshToken() {
        String username = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(username);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(userEntity));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getRefreshToken());
        assertTrue(refreshToken.getExpirationTime().isAfter(Instant.now()));
        verify(refreshTokenRepository, times(1)).save(refreshToken);
    }

    @Test
    public void whenUserExistsAndHasRefreshToken_ThenNoNewRefreshTokenCreated() {
        String username = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(username);
        RefreshToken existingRefreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expirationTime(Instant.now().plusMillis(5 * 60 * 60 * 10000))
                .user(userEntity)
                .build();
        userEntity.setRefreshToken(existingRefreshToken);

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(userEntity));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        assertSame(existingRefreshToken, refreshToken);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void whenUserNotFound_ThenThrowException() {
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class, () -> refreshTokenService.createRefreshToken(username));
    }

    @Test
    void whenRefreshTokenIsValid_ThenReturnIt() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(token)
                .expirationTime(Instant.now().plusMillis(1000)) // Future expiration time
                .build();

        when(refreshTokenRepository.findByRefreshToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(token);

        assertNotNull(verifiedToken);
        assertEquals(token, verifiedToken.getRefreshToken());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void whenRefreshTokenIsExpired_ThenThrowRuntimeException() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(token)
                .expirationTime(Instant.now().minusMillis(1000)) // Past expiration time
                .build();

        when(refreshTokenRepository.findByRefreshToken(token)).thenReturn(Optional.of(refreshToken));

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyRefreshToken(token));
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }


}
