package ai.uniauth.rep;

import ai.uniauth.model.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>,
        JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    List<Permission> findBySystemId(Long systemId);

    @Query("SELECT p FROM Permission p WHERE p.system.systemCode = :systemCode")
    Page<Permission> findBySystemCode(@Param("systemCode") String systemCode, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.system.systemCode = :systemCode")
    List<Permission> findBySystemCode(@Param("systemCode") String systemCode);

    @Query("SELECT p FROM Permission p WHERE p.system.systemCode = :systemCode AND p.resourceType = :resourceType")
    Page<Permission> findBySystemAndResourceType(@Param("systemCode") String systemCode,
                                                 @Param("resourceType") String resourceType, Pageable pageable);

    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.username = :username")
    List<Permission> findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.username = :username AND p.system.systemCode = :systemCode")
    List<Permission> findByUsernameAndSystem(@Param("username") String username,
                                             @Param("systemCode") String systemCode);

    @Query("SELECT p FROM Permission p WHERE p.permissionCode IN :permissionCodes")
    List<Permission> findByPermissionCodes(@Param("permissionCodes") Set<String> permissionCodes);
}