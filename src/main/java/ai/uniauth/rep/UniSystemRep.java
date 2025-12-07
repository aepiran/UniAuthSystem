package ai.uniauth.rep;

import ai.uniauth.models.UniSystem;
import ai.uniauth.models.enums.AuthType;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UniSystemRep extends JpaRepository<UniSystem, UUID>, JpaSpecificationExecutor<UniSystem> {

    // Basic Finders
    Optional<UniSystem> findByCode(String code);
    Optional<UniSystem> findByName(String name);
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // Status-based Finders
    List<UniSystem> findByIsActiveTrue();
    List<UniSystem> findByIsActiveFalse();
    List<UniSystem> findByIsInternalTrue();
    List<UniSystem> findByIsInternalFalse();

    // AuthType-based Finders
    List<UniSystem> findByAuthType(AuthType authType);

    // Date-based Finders
    List<UniSystem> findByRegisteredAtBetween(LocalDateTime start, LocalDateTime end);
    List<UniSystem> findByLastSyncAtBefore(LocalDateTime date);
    List<UniSystem> findByLastHealthCheckBefore(LocalDateTime date);

    // Search Queries
    @Query("SELECT s FROM UniSystem s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<UniSystem> search(@Param("keyword") String keyword, Pageable pageable);

    // Update Queries
    @Modifying
    @Query("UPDATE UniSystem s SET s.isActive = :isActive WHERE s.id = :systemId")
    int updateActiveStatus(@Param("systemId") UUID systemId, @Param("isActive") Boolean isActive);

    @Modifying
    @Query("UPDATE UniSystem s SET s.lastSyncAt = :lastSyncAt WHERE s.id = :systemId")
    int updateLastSyncAt(@Param("systemId") UUID systemId, @Param("lastSyncAt") LocalDateTime lastSyncAt);

    @Modifying
    @Query("UPDATE UniSystem s SET s.lastHealthCheck = CURRENT_TIMESTAMP WHERE s.id = :systemId")
    int updateLastHealthCheck(@Param("systemId") UUID systemId);

    @Modifying
    @Query("UPDATE UniSystem s SET s.lastHealthCheck = CURRENT_TIMESTAMP, " +
            "s.config = :config WHERE s.id = :systemId")
    int updateHealthCheckAndConfig(@Param("systemId") UUID systemId,
                                   @Param("config") String config);

    // Statistics
    @Query("SELECT COUNT(s) FROM UniSystem s WHERE s.isActive = true")
    long countActiveSystems();

    @Query("SELECT COUNT(s) FROM UniSystem s WHERE s.lastHealthCheck > :cutoffTime")
    long countHealthySystems(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT s.authType, COUNT(s) FROM UniSystem s GROUP BY s.authType")
    List<Object[]> countByAuthType();

    // User Count per UniSystem
    @Query("SELECT s.id, COUNT(DISTINCT u.id) FROM UniSystem s " +
            "LEFT JOIN s.roles r " +
            "LEFT JOIN r.users u " +
            "WHERE s.id IN :systemIds " +
            "GROUP BY s.id")
    List<Object[]> countUsersBySystemIds(@Param("systemIds") List<UUID> systemIds);

    // Join Fetch Queries
    @Query("SELECT s FROM UniSystem s LEFT JOIN FETCH s.roles WHERE s.id = :systemId")
    Optional<UniSystem> findByIdWithRoles(@Param("systemId") UUID systemId);

    @Query("SELECT s FROM UniSystem s LEFT JOIN FETCH s.permissions WHERE s.id = :systemId")
    Optional<UniSystem> findByIdWithPermissions(@Param("systemId") UUID systemId);

    @Query("SELECT s FROM UniSystem s LEFT JOIN FETCH s.apiKeys WHERE s.id = :systemId")
    Optional<UniSystem> findByIdWithApiKeys(@Param("systemId") UUID systemId);

    // Find systems with expired API keys
    @Query("SELECT DISTINCT s FROM UniSystem s JOIN s.apiKeys ak WHERE ak.expiresAt < CURRENT_TIMESTAMP")
    List<UniSystem> findSystemsWithExpiredApiKeys();

    // Find systems needing sync
    @Query("SELECT s FROM UniSystem s WHERE s.lastSyncAt IS NULL OR s.lastSyncAt < :cutoffTime")
    List<UniSystem> findSystemsNeedingSync(@Param("cutoffTime") LocalDateTime cutoffTime);
}