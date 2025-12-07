package ai.uniauth.rep;
import ai.uniauth.models.LoginAttempt;
import ai.uniauth.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoginAttemptRep extends JpaRepository<LoginAttempt, UUID>, JpaSpecificationExecutor<LoginAttempt> {

    // User-based Finders
    List<LoginAttempt> findByUserId(UUID userId);
    List<LoginAttempt> findByUser(User user);
    List<LoginAttempt> findByUsername(String username);

    // Status-based Finders
    List<LoginAttempt> findBySuccessTrue();
    List<LoginAttempt> findBySuccessFalse();
    List<LoginAttempt> findByUserIdAndSuccessTrue(UUID userId);
    List<LoginAttempt> findByUserIdAndSuccessFalse(UUID userId);

    // IP-based Finders
    List<LoginAttempt> findByIpAddress(String ipAddress);
    List<LoginAttempt> findByIpAddressAndSuccessFalse(String ipAddress);

    // Date-based Finders
    List<LoginAttempt> findByAttemptedAtBetween(LocalDateTime start, LocalDateTime end);
    List<LoginAttempt> findByAttemptedAtAfter(LocalDateTime date);
    List<LoginAttempt> findByAttemptedAtBefore(LocalDateTime date);

    // MFA-based Finders
    List<LoginAttempt> findByMfaUsedTrue();
    List<LoginAttempt> findByMfaUsedFalse();

    // Recent Failed Attempts
    @Query("SELECT la FROM LoginAttempt la WHERE la.ipAddress = :ipAddress " +
            "AND la.success = false AND la.attemptedAt >= :since")
    List<LoginAttempt> findRecentFailedAttemptsByIp(@Param("ipAddress") String ipAddress,
                                                    @Param("since") LocalDateTime since);

    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username " +
            "AND la.success = false AND la.attemptedAt >= :since")
    List<LoginAttempt> findRecentFailedAttemptsByUsername(@Param("username") String username,
                                                          @Param("since") LocalDateTime since);

    // Count Queries
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress " +
            "AND la.success = false AND la.attemptedAt >= :since")
    long countRecentFailedAttemptsByIp(@Param("ipAddress") String ipAddress,
                                       @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.username = :username " +
            "AND la.success = false AND la.attemptedAt >= :since")
    long countRecentFailedAttemptsByUsername(@Param("username") String username,
                                             @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.user.id = :userId " +
            "AND la.success = false AND la.attemptedAt >= :since")
    long countRecentFailedAttemptsByUserId(@Param("userId") UUID userId,
                                           @Param("since") LocalDateTime since);

    // Statistics
    @Query("SELECT DATE(la.attemptedAt), COUNT(la), SUM(CASE WHEN la.success = true THEN 1 ELSE 0 END) " +
            "FROM LoginAttempt la WHERE la.attemptedAt >= :startDate " +
            "GROUP BY DATE(la.attemptedAt) ORDER BY DATE(la.attemptedAt)")
    List<Object[]> getDailyLoginStats(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT la.ipAddress, COUNT(la), SUM(CASE WHEN la.success = true THEN 1 ELSE 0 END) " +
            "FROM LoginAttempt la WHERE la.attemptedAt >= :startDate " +
            "GROUP BY la.ipAddress HAVING COUNT(la) > :minAttempts " +
            "ORDER BY COUNT(la) DESC")
    List<Object[]> getTopIps(@Param("startDate") LocalDateTime startDate,
                             @Param("minAttempts") long minAttempts);

    @Query("SELECT la.country, COUNT(la), SUM(CASE WHEN la.success = true THEN 1 ELSE 0 END) " +
            "FROM LoginAttempt la WHERE la.attemptedAt >= :startDate AND la.country IS NOT NULL " +
            "GROUP BY la.country ORDER BY COUNT(la) DESC")
    List<Object[]> getStatsByCountry(@Param("startDate") LocalDateTime startDate);

    // Search Queries
    @Query("SELECT la FROM LoginAttempt la WHERE " +
            "LOWER(la.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "la.ipAddress LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(la.country) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(la.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<LoginAttempt> search(@Param("keyword") String keyword, Pageable pageable);

    // Cleanup
    @Modifying
    @Query("DELETE FROM LoginAttempt la WHERE la.attemptedAt < :cutoffDate")
    int deleteOldAttempts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Bulk Operations
    @Modifying
    @Query("DELETE FROM LoginAttempt la WHERE la.user.id = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    // Find suspicious IPs
    @Query("SELECT la.ipAddress, COUNT(la) as attempts " +
            "FROM LoginAttempt la " +
            "WHERE la.success = false AND la.attemptedAt >= :startDate " +
            "GROUP BY la.ipAddress " +
            "HAVING COUNT(la) > :threshold " +
            "ORDER BY attempts DESC")
    List<Object[]> findSuspiciousIps(@Param("startDate") LocalDateTime startDate,
                                     @Param("threshold") long threshold);
}