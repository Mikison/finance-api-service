package pl.sonmiike.financeapiservice.security.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.UserRole;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    // Secret key used in your JwtService
    private final String SECRET_KEY = "OXgzaGZ3OTMzdWRpendib281cHF1bTRsODl1YWx5ejloc2E5Zm16bW5hNzBrcmt5c2p0c3Q5dXhrMDV6YWUzOGFldDNlNHZlajllZWduenlzdTd1Y3RyN2d6dWF1MjBiNm5ib2tjeW9hb3l4aTg3NGMybmV5a3F6NG1zN2E2c20=";
    private final UserEntity USER_DETAILS = UserEntity.builder()
            .userId(1L)
            .email("test@test.com")
            .username("testUser")
            .role(UserRole.USER)
            .build();


    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        jwtService.setSecretKey(SECRET_KEY) ;
        jwtService.init();
    }

    @Test
    public void whenGenerateToken_thenSuccess() {
        // Given
        // When
        String token = jwtService.generateToken(USER_DETAILS);


        // Then
        assertTrue(token != null && !token.isEmpty());
    }

    @Test
    public void whenTokenIsValid_thenSuccess() {
        // Given

        String token = jwtService.generateToken(USER_DETAILS);

        // When
        boolean isValid = jwtService.isTokenValid(token, USER_DETAILS);

        // Then
        assertTrue(isValid);
    }
//
//    @Test
//    public void whenTokenIsInvalid_thenFailure() {
//        // Given
//        UserEntity userDetails = new UserEntity("testUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//        UserDetails anotherUserDetails = new org.springframework.security.core.userdetails.User("anotherUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//        String token = jwtService.generateToken(userDetails);
//
//        // When
//        boolean isValid = jwtService.isTokenValid(token, anotherUserDetails);
//
//        // Then
//        assertFalse(isValid);
//    }
//
}