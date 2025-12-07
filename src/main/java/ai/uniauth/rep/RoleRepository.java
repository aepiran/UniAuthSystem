package ai.uniauth.rep;

import ai.uniauth.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);

    List<Role> findBySystemId(Long systemId);

    @Query("SELECT r FROM Role r WHERE r.system.systemCode = :systemCode")
    List<Role> findBySystemCode(@Param("systemCode") String systemCode);

    @Query("SELECT r FROM Role r WHERE r.system.systemCode = :systemCode AND r.isSystemRole = true")
    List<Role> findSystemRoles(@Param("systemCode") String systemCode);

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.username = :username")
    List<Role> findByUsername(@Param("username") String username);

    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.permissionCode = :permissionCode")
    List<Role> findByPermissionCode(@Param("permissionCode") String permissionCode);

    @Query("SELECT r FROM Role r WHERE r.roleCode IN :roleCodes")
    List<Role> findByRoleCodes(@Param("roleCodes") Set<String> roleCodes);
}