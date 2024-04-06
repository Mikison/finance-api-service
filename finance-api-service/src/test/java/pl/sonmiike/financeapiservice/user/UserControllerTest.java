package pl.sonmiike.financeapiservice.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.sonmiike.financeapiservice.security.auth.JwtService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();



    @BeforeEach
    public void setup() {

    }

    @Test
    public void getAllUsersTest() throws Exception {
        // Given
        int page = 0;
        int size = 10;
        PagedUsersDTO pagedUsersDTO = mock(PagedUsersDTO.class); // Mock the DTO
        when(userService.getAllUsers(page, size)).thenReturn(pagedUsersDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users?page=" + page + "&size=" + size))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pagedUsersDTO)));

        verify(userService).getAllUsers(page, size);
    }

    @Test
    public void getUserByIdTest() throws Exception {
        // Given
        Long id = 1L;
        UserEntity userEntity = mock(UserEntity.class);
        UserDTO userDTO = mock(UserDTO.class);
        when(userService.getUserById(id)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService).getUserById(id);
        verify(userMapper).toDTO(userEntity);
    }

    @Test
    public void getUserByEmailTest() throws Exception {
        // Given
        String email = "test@example.com";
        UserDTO userDTO = mock(UserDTO.class);
        when(userService.getUserByEmail(email)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/users/email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService).getUserByEmail(email);
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        // Given
        Long id = 1L;

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/users/" + id))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserById(id);
    }
}
