package pl.sonmiike.financeapiservice.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.sonmiike.financeapiservice.security.testconfig.TestSecurityConfig;
import pl.sonmiike.financeapiservice.user.UserEntity;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshToken;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtService jwtService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password1234";

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void register_ShouldReturnOk_WhenValidRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", TEST_EMAIL, "tester", TEST_PASSWORD);
        AuthResponse expectedResponse = new AuthResponse("accessToken", "refreshToken");

        given(authService.register(registerRequest)).willReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accessToken\":\"accessToken\",\"refreshToken\":\"refreshToken\"}"));
    }

    @Test
    public void login_ShouldReturnOk_WhenValidRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthResponse expectedResponse = new AuthResponse("accessToken", "refreshToken");

        given(authService.login(loginRequest)).willReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + TEST_EMAIL + "\",\"password\":\"" + TEST_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accessToken\":\"accessToken\",\"refreshToken\":\"refreshToken\"}"));
    }

    @Test
    public void refreshToken_ShouldReturnOk_WhenValidRequest() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("validRefreshToken");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setRefreshToken("validRefreshToken");
        UserEntity mockUser = new UserEntity();
        mockRefreshToken.setUser(mockUser);

        given(refreshTokenService.verifyRefreshToken("validRefreshToken")).willReturn(mockRefreshToken);
        given(jwtService.generateToken(any(UserEntity.class))).willReturn("newAccessToken");

        AuthResponse expectedResponse = new AuthResponse("newAccessToken", "validRefreshToken");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }






}