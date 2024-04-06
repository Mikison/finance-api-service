package pl.sonmiike.financeapiservice.user.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteAllByUserUserId(Long userid);
}
