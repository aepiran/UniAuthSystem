package ai.uniauth.rep;

import ai.uniauth.models.User;
import ai.uniauth.models.UserSession;
import ai.uniauth.models.enums.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface UserSessionRep extends JpaRepository<UserSession, UUID> {

    // Basic Finders
    Optional<UserSession> findByRefreshToken(String refreshToken);
    Optional<UserSession> findByAccessToken(String accessToken);
    Optional<UserSession> findByTokenHash(String tokenHash);

    // User-based Finders
    List<UserSession> findByUserId(UUID userId);
    List<UserSession> findByUser(User user);

    // Status-based Finders
    List<UserSession> findByIsActiveTrue();
    List<UserSession> findByIsActiveFalse();
    List<UserSession> findByUserIdAndIsActiveTrue(UUID userId);
    List<UserSession> findByUserIdAndIsActiveFalse(UUID userId);

    // Device-based Finders
    List<UserSession> findByDeviceType(DeviceType deviceType);
    List<UserSession> findByUserIdAndDeviceType(UUID userId, DeviceType deviceType);

    // Date-based Finders
    List<UserSession> findByLoginAtBetween(LocalDateTime start, LocalDateTime end);
    List<UserSession> findByLastActivityAtBefore(LocalDateTime date);
    List<UserSession> findByAccessTokenExpiresBefore(LocalDateTime date);
    List<UserSession> findByRefreshTokenExpiresBefore(LocalDateTime date);

    // Expired/Invalid Sessions
    @Query("SELECT s FROM UserSession s WHERE s.accessTokenExpires < CURRENT_TIMESTAMP AND s.isActive = true")
    List<UserSession> findExpiredActiveSessions();

    @Query("SELECT s FROM UserSession s WHERE s.refreshTokenExpires < CURRENT_TIMESTAMP AND s.isActive = true")
    List<UserSession> findExpiredRefreshTokens();

    @Query("SELECT s FROM UserSession s WHERE s.lastActivityAt < :cutoffTime AND s.isActive = true")
    List<UserSession> findIdleSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    // IP-based Finders
    List<UserSession> findByIpAddress(String ipAddress);
    List<UserSession> findByUserIdAndIpAddress(UUID userId, String ipAddress);

    @Query("SELECT DISTINCT s.ipAddress FROM UserSession s WHERE s.user.id = :userId")
    List<String> findDistinctIpsByUserId(@Param("userId") UUID userId);

    // Country-based Finders
    List<UserSession> findByCountryCode(String countryCode);
    List<UserSession> findByUserIdAndCountryCode(UUID userId, String countryCode);

    // Search Queries
    @Query("SELECT s FROM UserSession s WHERE " +
            "LOWER(s.deviceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.os) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.browser) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "s.ipAddress LIKE CONCAT('%', :keyword, '%')")
    Page<UserSession> search(@Param("keyword") String keyword, Pageable pageable);

    // Update Queries
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.logoutAt = CURRENT_TIMESTAMP, " +
            "s.logoutReason = :reason WHERE s.id = :sessionId")
    int logoutSession(@Param("sessionId") String sessionId, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.logoutAt = CURRENT_TIMESTAMP, " +
            "s.logoutReason = :reason WHERE s.user.id = :userId")
    int logoutAllUserSessions(@Param("userId") UUID userId, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE UserSession s SET s.lastActivityAt = CURRENT_TIMESTAMP WHERE s.id = :sessionId")
    int updateLastActivity(@Param("sessionId") String sessionId);

    @Modifying
    @Query("UPDATE UserSession s SET s.accessToken = :accessToken, " +
            "s.accessTokenExpires = :expiresAt WHERE s.id = :sessionId")
    int updateAccessToken(@Param("sessionId") String sessionId,
                          @Param("accessToken") String accessToken,
                          @Param("expiresAt") LocalDateTime expiresAt);

    // Bulk Operations
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.logoutAt = CURRENT_TIMESTAMP " +
            "WHERE s.accessTokenExpires < CURRENT_TIMESTAMP AND s.isActive = true")
    int deactivateExpiredSessions();

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.logoutAt < :cutoffDate")
    int deleteOldSessions(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Statistics
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.user.id = :userId AND s.isActive = true")
    long countActiveSessionsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(DISTINCT s.user.id) FROM UserSession s WHERE s.isActive = true")
    long countActiveUsers();

    @Query("SELECT s.deviceType, COUNT(s) FROM UserSession s WHERE s.isActive = true GROUP BY s.deviceType")
    List<Object[]> countActiveSessionsByDeviceType();

    @Query("SELECT DATE(s.loginAt), COUNT(s) FROM UserSession s " +
            "WHERE s.loginAt >= :startDate GROUP BY DATE(s.loginAt) ORDER BY DATE(s.loginAt)")
    List<Object[]> countSessionsByDate(@Param("startDate") LocalDateTime startDate);

    // Latest Sessions
    @Query("SELECT s FROM UserSession s WHERE s.user.id = :userId ORDER BY s.loginAt DESC")
    Page<UserSession> findLatestSessionsByUserId(@Param("userId") UUID userId, Pageable pageable);

    // Find sessions by criteria
    @Query("SELECT s FROM UserSession s WHERE " +
            "(:userId IS NULL OR s.user.id = :userId) AND " +
            "(:deviceType IS NULL OR s.deviceType = :deviceType) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive) AND " +
            "s.loginAt BETWEEN :startDate AND :endDate")
    List<UserSession> findByCriteria(@Param("userId") UUID userId,
                                     @Param("deviceType") DeviceType deviceType,
                                     @Param("isActive") Boolean isActive,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}