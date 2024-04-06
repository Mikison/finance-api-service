package pl.sonmiike.financeapiservice.user;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(UserEntity user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getAuthorities())
                .build();
    }

    public UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .userId(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .role(UserRole.valueOf(userDTO.getRoles().toString()))
                .build();
    }

    public PagedUsersDTO toPagedDTO(Page<UserEntity> user) {
        return PagedUsersDTO.builder()
                .currentPage(user.getNumber())
                .totalPages(user.getTotalPages())
                .users(user.map(this::toDTO).getContent())
                .build();
    }
}
