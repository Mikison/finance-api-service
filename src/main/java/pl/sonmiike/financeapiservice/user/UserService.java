package pl.sonmiike.financeapiservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sonmiike.financeapiservice.category.UserCategoryRepository;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFoundException;
import pl.sonmiike.financeapiservice.expenses.ExpenseRepository;
import pl.sonmiike.financeapiservice.income.IncomeRepository;
import pl.sonmiike.financeapiservice.user.refreshToken.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserMapper userMapper;



    public PagedUsersDTO getAllUsers(int page, int size) {
        Page<UserEntity> users = userRepository.findAll(PageRequest.of(page, size));
        return userMapper.toPagedDTO(users);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the database"));
    }


    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the database"));
    }


    @Transactional
    public void deleteUserById(Long userid) {
        if (!userRepository.existsById(userid)) {
            throw new ResourceNotFoundException("User not found in the database");
        }
        incomeRepository.deleteAllByUserUserId(userid);
        expenseRepository.deleteAllByUserUserId(userid);
        userCategoryRepository.deleteAllByUserUserId(userid);
        refreshTokenRepository.deleteAllByUserUserId(userid);
        userRepository.deleteById(userid);

    }

    // TODO Update user
}
