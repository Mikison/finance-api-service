package pl.sonmiike.financeapiservice.security.auth;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;
import pl.sonmiike.financeapiservice.user.UserRole;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshToken;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenService;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

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
    public void whenRegisteringNewUser_thenSucceeds() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "testuser", "test@example.com", "password123");
        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();
        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(1L, uuid , Instant.now().plusSeconds(86400), null);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("accessToken");
        when(refreshTokenService.createRefreshToken(any(String.class))).thenReturn(refreshToken);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals(uuid, response.getRefreshToken());
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtService).generateToken(any(UserEntity.class));
        verify(refreshTokenService).createRefreshToken(any(String.class));
    }

    @Test
    public void whenGetUserId_thenSucceeds() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userEntity);

        Long userId = authService.getUserId(authentication);

        assertNotNull(userId);
        assertEquals(Long.valueOf(1L), userId);
        assertEquals(userEntity, authentication.getPrincipal());
    }

}
