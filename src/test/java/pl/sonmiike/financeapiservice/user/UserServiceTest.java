package pl.sonmiike.financeapiservice.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.sonmiike.financeapiservice.category.UserCategoryRepository;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.expenses.ExpenseRepository;
import pl.sonmiike.financeapiservice.income.IncomeRepository;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserCategoryRepository userCategoryRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

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
    void testGetUserById_Success() {
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder().userId(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));


        UserEntity userById = userService.getUserById(userId);
        assertNotNull(userById);
        assertEquals(userById, userEntity);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_Failure() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testgetUserByEmail_Success() {
        String email = "test@test.com";
        UserEntity userEntity = UserEntity.builder().email(email).build();
        UserDTO dto = UserDTO.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(dto);

        UserDTO userByEmail = userService.getUserByEmail(email);

        assertEquals(userByEmail, dto);
        verify(userRepository, times(1)).findByEmail(email);

    }

    @Test
    void testgetUserByEmail_Failure() {
        String email = "test@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);

    }
}
