package pl.sonmiike.financeapiservice.security.auth;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sonmiike.financeapiservice.exceptions.custom.EmailAlreadyTakenException;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRepository;
import pl.sonmiike.financeapiservice.user.UserRole;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshToken;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    void whenRegisteringNewUser_thenSucceeds() {
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
    void whenRegisteringExistingUser_thenThrowsException() {
        RegisterRequest request = new RegisterRequest("Test User", "test@user.com", "test@example.com", "password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        // Assert
        assertThrows(EmailAlreadyTakenException.class, () -> authService.register(request));
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(jwtService, never()).generateToken(any(UserEntity.class));
        verify(refreshTokenService, never()).createRefreshToken(any(String.class));
    }

    @Test
    void whenLoginSuccessful_thenReturnsAuthResponse() {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        UserEntity user = new UserEntity(); // Assuming you have a constructor or builder to set properties
        user.setUsername("testUser");
        RefreshToken refreshToken = new RefreshToken(); // Assuming you have a constructor or method to set properties
        refreshToken.setRefreshToken("refreshToken123");
        Authentication authentication = mock(Authentication.class);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("accessToken123");
        when(refreshTokenService.createRefreshToken(user.getUsername())).thenReturn(refreshToken);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken123", response.getAccessToken());
        assertEquals("refreshToken123", response.getRefreshToken());

        // Verify the interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).createRefreshToken(user.getUsername());
    }

    @Test
    void whenUserNotFound_thenThrowsException() {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password123");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest));

        // Verify userRepository was called
        verify(userRepository).findByEmail(loginRequest.getEmail());
        // Ensure no other interactions
        verifyNoInteractions(jwtService);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void whenGetUserId_thenSucceeds() {
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
