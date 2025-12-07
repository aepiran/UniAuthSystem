package ai.uniauth.rep;

import ai.uniauth.model.entity.UniSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SystemRepository extends JpaRepository<UniSystem, Long> {

    Optional<UniSystem> findBySystemCode(String systemCode);

    Optional<UniSystem> findByApiKey(String apiKey);

    boolean existsBySystemCode(String systemCode);

    boolean existsByApiKey(String apiKey);

    Page<UniSystem> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT s FROM UniSystem s WHERE s.systemCode IN :systemCodes")
    Set<UniSystem> findBySystemCodes(@Param("systemCodes") Set<String> systemCodes);

    @Query("SELECT s FROM UniSystem s WHERE s.systemName LIKE %:keyword% OR s.systemCode LIKE %:keyword%")
    List<UniSystem> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT s FROM UniSystem s WHERE s.updatedAt >= :since")
    List<UniSystem> findRecentlyUpdated(@Param("since") LocalDateTime since);

    @Query("SELECT s FROM UniSystem s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<UniSystem> findByCreatedDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(u) FROM User u JOIN u.systems s WHERE s.id = :systemId")
    Long countUsersBySystemId(@Param("systemId") Long systemId);

    @Query("SELECT s FROM UniSystem s WHERE s.contactEmail = :email")
    List<UniSystem> findByContactEmail(@Param("email") String email);

    @Query("SELECT s FROM UniSystem s WHERE s.baseUrl LIKE %:domain%")
    List<UniSystem> findByDomain(@Param("domain") String domain);
}