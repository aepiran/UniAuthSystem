package ai.uniauth.rep;

import ai.uniauth.models.PasswordReset;
import ai.uniauth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetRep extends JpaRepository<PasswordReset, String> {

    // Basic Finders
    Optional<PasswordReset> findByToken(String token);
    List<PasswordReset> findByUserId(UUID userId);
    List<PasswordReset> findByUser(User user);

    // Status-based Finders
    List<PasswordReset> findByIsUsedTrue();
    List<PasswordReset> findByIsUsedFalse();
    List<PasswordReset> findByUserIdAndIsUsedFalse(UUID userId);

    // Expiration-based Finders
    @Query("SELECT pr FROM PasswordReset pr WHERE pr.expiresAt < CURRENT_TIMESTAMP AND pr.isUsed = false")
    List<PasswordReset> findExpiredTokens();

    @Query("SELECT pr FROM PasswordReset pr WHERE pr.expiresAt BETWEEN :start AND :end AND pr.isUsed = false")
    List<PasswordReset> findExpiringTokens(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Date-based Finders
    List<PasswordReset> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<PasswordReset> findByUsedAtBetween(LocalDateTime start, LocalDateTime end);
    List<PasswordReset> findByCreatedAtAfter(LocalDateTime date);

    // IP-based Finders
    List<PasswordReset> findByUsedIp(String usedIp);

    // Validation Queries
    @Query("SELECT COUNT(pr) > 0 FROM PasswordReset pr WHERE pr.token = :token AND " +
            "pr.isUsed = false AND pr.expiresAt > CURRENT_TIMESTAMP")
    boolean isValidToken(@Param("token") String token);

    // Recent tokens for user
    @Query("SELECT pr FROM PasswordReset pr WHERE pr.user.id = :userId AND " +
            "pr.createdAt >= :since AND pr.isUsed = false")
    List<PasswordReset> findRecentTokensForUser(@Param("userId") UUID userId,
                                                @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(pr) FROM PasswordReset pr WHERE pr.user.id = :userId AND " +
            "pr.createdAt >= :since AND pr.isUsed = false")
    long countRecentTokensForUser(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    // Update Queries
    @Modifying
    @Query("UPDATE PasswordReset pr SET pr.isUsed = true, pr.usedAt = CURRENT_TIMESTAMP, " +
            "pr.usedIp = :ipAddress WHERE pr.token = :token")
    int markAsUsed(@Param("token") String token, @Param("ipAddress") String ipAddress);

    // Cleanup
    @Modifying
    @Query("DELETE FROM PasswordReset pr WHERE pr.expiresAt < :cutoffDate")
    int deleteExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM PasswordReset pr WHERE pr.user.id = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    // Statistics
    @Query("SELECT DATE(pr.createdAt), COUNT(pr), SUM(CASE WHEN pr.isUsed = true THEN 1 ELSE 0 END) " +
            "FROM PasswordReset pr " +
            "WHERE pr.createdAt >= :startDate " +
            "GROUP BY DATE(pr.createdAt) " +
            "ORDER BY DATE(pr.createdAt)")
    List<Object[]> getDailyStats(@Param("startDate") LocalDateTime startDate);

    // Find unused tokens
    @Query("SELECT pr FROM PasswordReset pr WHERE pr.isUsed = false AND pr.expiresAt > CURRENT_TIMESTAMP")
    List<PasswordReset> findValidTokens();

    // Bulk invalidate tokens for user
    @Modifying
    @Query("UPDATE PasswordReset pr SET pr.isUsed = true, pr.usedAt = CURRENT_TIMESTAMP, " +
            "pr.usedIp = 'Bulk invalidate' WHERE pr.user.id = :userId AND pr.isUsed = false")
    int invalidateUserTokens(@Param("userId") UUID userId);
}