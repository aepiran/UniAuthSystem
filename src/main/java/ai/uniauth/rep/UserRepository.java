package ai.uniauth.rep;

import ai.uniauth.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.systems s WHERE s.systemCode = :systemCode")
    Page<User> findBySystemCode(@Param("systemCode") String systemCode, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.systems s WHERE s.systemCode IN :systemCodes")
    List<User> findBySystemCodes(@Param("systemCodes") Set<String> systemCodes);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleCode = :roleCode")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.fullName LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.system.systemCode = :systemCode")
    List<User> findBySystemRoles(@Param("systemCode") String systemCode);
}