package ai.uniauth.service;


import ai.uniauth.model.entity.*;
import ai.uniauth.rep.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockDataService {

    private final PasswordEncoder passwordEncoder;
    private final SystemRepository systemRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public Map<String, Object> generateTestData() {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("Starting test data generation...");

            // Kiểm tra nếu đã có dữ liệu
            if (systemRepository.count() > 0) {
                result.put("success", false);
                result.put("message", "Database already contains data. Please clean up first.");
                return result;
            }

            // 1. Tạo test system
            UniSystem testSystem = createTestSystem();
            result.put("systemCode", testSystem.getSystemCode());
            result.put("systemName", testSystem.getSystemName());
            result.put("apiKey", testSystem.getApiKey());

            // 2. Tạo test permissions
            List<Permission> permissions = createTestPermissions(testSystem);
            result.put("permissionsCount", permissions.size());

            // 3. Tạo test roles
            List<Role> roles = createTestRoles(testSystem, permissions);
            result.put("rolesCount", roles.size());

            // 4. Tạo test users
            List<User> users = createTestUsers(testSystem, roles);
            result.put("usersCount", users.size());

            // 5. Tạo demo credentials
            Map<String, String> demoCredentials = createDemoCredentials();
            result.put("demoCredentials", demoCredentials);

            result.put("success", true);
            result.put("message", "Test data generated successfully");
            result.put("timestamp", LocalDateTime.now());

            log.info("Test data generation completed: {} users, {} roles, {} permissions",
                    users.size(), roles.size(), permissions.size());

        } catch (Exception e) {
            log.error("Failed to generate test data", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("timestamp", LocalDateTime.now());
        }

        return result;
    }

    @Transactional
    public Map<String, Object> generateSystemWithData(String systemCode, String systemName) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("Generating system with data: {} - {}", systemCode, systemName);

            // 1. Tạo system
            UniSystem system = createSystem(systemCode, systemName,
                    "https://" + systemCode.toLowerCase() + ".company.com",
                    systemCode.toLowerCase() + "@company.com");
            system.setApiKey("UA-" + systemCode + "-" + UUID.randomUUID().toString().substring(0, 8));
            system.setSecretKey(passwordEncoder.encode(system.getApiKey()));
            system = systemRepository.save(system);

            result.put("system", system.getSystemCode());
            result.put("apiKey", system.getApiKey());

            // 2. Tạo permissions
            List<Permission> permissions = generateStandardPermissions(system);
            result.put("permissions", permissions.size());

            // 3. Tạo roles với permission mapping
            Map<String, List<String>> rolePermissionMapping = createRolePermissionMapping();
            List<Role> roles = generateRolesWithPermissions(system, permissions, rolePermissionMapping);
            result.put("roles", roles.size());

            // 4. Tạo users
            List<User> users = generateUsersForSystem(system, roles);
            result.put("users", users.size());

            result.put("success", true);
            result.put("message", "System data generated successfully");

        } catch (Exception e) {
            log.error("Failed to generate system data", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Transactional
    public Map<String, Object> generateLargeDataSet(int userCount, int roleCount, int permissionCount) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("Generating large dataset: users={}, roles={}, permissions={}",
                    userCount, roleCount, permissionCount);

            // Tạo system cho dataset lớn
            UniSystem system = createSystem("LARGE_" + UUID.randomUUID().toString().substring(0, 8),
                    "Large Test System",
                    "https://large-test.company.com",
                    "large.test@company.com");
            system.setApiKey("UA-LARGE-" + UUID.randomUUID().toString().substring(0, 12));
            system.setSecretKey(passwordEncoder.encode(system.getApiKey()));
            system = systemRepository.save(system);

            result.put("system", system.getSystemCode());

            // Tạo permissions
            List<Permission> permissions = new ArrayList<>();
            for (int i = 1; i <= permissionCount; i++) {
                Permission permission = new Permission();
                permission.setPermissionCode("PERM_" + String.format("%06d", i));
                permission.setPermissionName("Permission " + i);
                permission.setDescription("Test permission number " + i);
                permission.setResourceType("RESOURCE_" + ((i % 20) + 1));
                permission.setAction(getRandomAction());
                permission.setSystem(system);
                permission.setCreatedAt(LocalDateTime.now());
                permissions.add(permission);
            }
            permissions = permissionRepository.saveAll(permissions);
            result.put("permissions", permissions.size());

            // Tạo roles với permission ngẫu nhiên
            List<Role> roles = new ArrayList<>();
            Random random = new Random();

            for (int i = 1; i <= roleCount; i++) {
                Role role = new Role();
                role.setRoleCode("ROLE_" + String.format("%04d", i));
                role.setRoleName("Test Role " + i);
                role.setDescription("Generated role for testing");
                role.setSystem(system);
                role.setIsSystemRole(i == 1); // Role đầu tiên là system role
                role.setCreatedAt(LocalDateTime.now());

                // Gán ngẫu nhiên 5-15 permissions cho mỗi role
                Set<Permission> rolePermissions = new HashSet<>();
                int permCount = 5 + random.nextInt(11); // 5-15 permissions
                Collections.shuffle(permissions);
                for (int j = 0; j < Math.min(permCount, permissions.size()); j++) {
                    rolePermissions.add(permissions.get(j));
                }
                role.setPermissions(rolePermissions);

                roles.add(role);
            }
            roles = roleRepository.saveAll(roles);
            result.put("roles", roles.size());

            // Tạo users với role ngẫu nhiên
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= userCount; i++) {
                User user = new User();
                user.setUsername("testuser_" + String.format("%06d", i));
                user.setEmail("user" + i + "@test.company.com");
                user.setPasswordHash(passwordEncoder.encode("Test@123"));
                user.setFullName("Test User " + i);
                user.setPhone("+84123" + String.format("%07d", i));
                user.setAvatarUrl("https://ui-avatars.com/api/?name=User+" + i + "&background=random");
                user.setIsActive(i % 100 != 0); // Mỗi user thứ 100 inactive
                user.setIsLocked(i % 500 == 0); // Mỗi user thứ 500 locked
                user.setLastLogin(LocalDateTime.now().minusDays(random.nextInt(90)));
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                // Gán system
                user.setSystems(Set.of(system));

                // Gán 1-3 roles ngẫu nhiên
                Set<Role> userRoles = new HashSet<>();
                int userRoleCount = 1 + random.nextInt(3);
                Collections.shuffle(roles);
                for (int j = 0; j < Math.min(userRoleCount, roles.size()); j++) {
                    userRoles.add(roles.get(j));
                }
                user.setRoles(userRoles);

                users.add(user);
            }

            // Lưu users theo batch
            int batchSize = 100;
            for (int i = 0; i < users.size(); i += batchSize) {
                int end = Math.min(users.size(), i + batchSize);
                List<User> batch = users.subList(i, end);
                userRepository.saveAll(batch);
                log.debug("Saved batch {}-{} of users", i, end);
            }

            result.put("users", users.size());
            result.put("success", true);
            result.put("message", "Large dataset generated successfully");
            result.put("timestamp", LocalDateTime.now());

            log.info("Large dataset generation completed: {} users created", users.size());

        } catch (Exception e) {
            log.error("Failed to generate large dataset", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("timestamp", LocalDateTime.now());
        }

        return result;
    }

    @Transactional
    public Map<String, Object> cleanupTestData() {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("Starting test data cleanup...");

            // Tìm các test systems
            List<UniSystem> testSystems = systemRepository.findAll().stream()
                    .filter(s -> s.getSystemCode().startsWith("TEST_") ||
                            s.getSystemCode().startsWith("LARGE_") ||
                            s.getSystemCode().contains("MOCK") ||
                            s.getSystemCode().equals("DEMO"))
                    .collect(Collectors.toList());

            int deletedCount = 0;

            for (UniSystem system : testSystems) {
                log.debug("Cleaning up system: {}", system.getSystemCode());

                // Tìm users chỉ thuộc system này
                List<User> usersInSystem = userRepository.findBySystemCodes(Set.of(system.getSystemCode()));

                // Xóa users không thuộc system nào khác
                for (User user : usersInSystem) {
                    if (user.getSystems().size() == 1) {
                        // User chỉ thuộc system này, xóa user
                        userRepository.delete(user);
                    } else {
                        // User thuộc nhiều systems, chỉ xóa khỏi system này
                        user.getSystems().removeIf(s -> s.getId().equals(system.getId()));
                        userRepository.save(user);
                    }
                }

                // Xóa system
                systemRepository.delete(system);
                deletedCount++;
            }

            result.put("deletedSystems", deletedCount);
            result.put("success", true);
            result.put("message", "Test data cleanup completed");
            result.put("timestamp", LocalDateTime.now());

            log.info("Cleaned up {} test systems", deletedCount);

        } catch (Exception e) {
            log.error("Failed to cleanup test data", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("timestamp", LocalDateTime.now());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMockDataStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long totalUsers = userRepository.count();
            long totalSystems = systemRepository.count();
            long totalRoles = roleRepository.count();
            long totalPermissions = permissionRepository.count();

            stats.put("totalUsers", totalUsers);
            stats.put("totalSystems", totalSystems);
            stats.put("totalRoles", totalRoles);
            stats.put("totalPermissions", totalPermissions);

            // Active users
            long activeUsers = userRepository.findAll().stream()
                    .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                    .count();
            stats.put("activeUsers", activeUsers);

            // Active systems
            long activeSystems = systemRepository.findByIsActive(true, Pageable.unpaged()).getTotalElements();
            stats.put("activeSystems", activeSystems);

            // Users per system
            Map<String, Long> usersPerSystem = new HashMap<>();
            for (UniSystem system : systemRepository.findAll()) {
                long userCount = userRepository.findBySystemCodes(Set.of(system.getSystemCode())).size();
                usersPerSystem.put(system.getSystemCode(), userCount);
            }
            stats.put("usersPerSystem", usersPerSystem);

            // Permissions per system
            Map<String, Long> permissionsPerSystem = new HashMap<>();
            for (UniSystem system : systemRepository.findAll()) {
                long permCount = permissionRepository.findBySystemCode(system.getSystemCode()).size();
                permissionsPerSystem.put(system.getSystemCode(), permCount);
            }
            stats.put("permissionsPerSystem", permissionsPerSystem);

            stats.put("success", true);
            stats.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            log.error("Failed to get statistics", e);
            stats.put("success", false);
            stats.put("error", e.getMessage());
        }

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getSampleCredentials() {
        Map<String, String> credentials = new HashMap<>();

        // Tìm các user demo
        List<User> demoUsers = userRepository.findAll().stream()
                .filter(u -> u.getUsername().contains("admin") ||
                        u.getUsername().contains("demo") ||
                        u.getUsername().equals("john_doe") ||
                        u.getUsername().equals("jane_smith"))
                .limit(10)
                .collect(Collectors.toList());

        for (User user : demoUsers) {
            credentials.put(user.getUsername(), "QAZxsw123!");
        }

        // Thêm vài credentials mặc định nếu không tìm thấy
        if (credentials.isEmpty()) {
            credentials.put("admin", "QAZxsw123!");
            credentials.put("superadmin", "QAZxsw123!");
            credentials.put("demo_user", "QAZxsw123!");
        }

        return credentials;
    }

    @Transactional
    public Map<String, Object> resetDemoData() {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("Resetting demo data...");

            // 1. Clean up old demo data
            cleanupTestData();

            // 2. Create new demo system
            Map<String, Object> demoResult = generateSystemWithData("DEMO", "Demo System");

            if (Boolean.TRUE.equals(demoResult.get("success"))) {
                result.put("success", true);
                result.put("message", "Demo data reset successfully");
                result.put("system", demoResult.get("system"));
                result.put("apiKey", demoResult.get("apiKey"));
                result.put("timestamp", LocalDateTime.now());
            } else {
                result.put("success", false);
                result.put("error", "Failed to create demo system");
            }

        } catch (Exception e) {
            log.error("Failed to reset demo data", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    // ========== PRIVATE HELPER METHODS ==========

    private UniSystem createTestSystem() {
        UniSystem system = createSystem("DEMO", "Demo System",
                "https://demo.company.com", "demo@company.com");
        system.setApiKey("UA-DEMO-" + UUID.randomUUID().toString().substring(0, 8));
        system.setSecretKey(passwordEncoder.encode(system.getApiKey()));
        return systemRepository.save(system);
    }

    private List<Permission> createTestPermissions(UniSystem system) {
        List<Permission> permissions = new ArrayList<>();

        // Standard permissions
        String[] resources = {"USER", "ROLE", "PERMISSION", "REPORT", "SETTING"};
        String[] actions = {"CREATE", "READ", "UPDATE", "DELETE", "EXPORT"};

        for (String resource : resources) {
            for (String action : actions) {
                Permission permission = new Permission();
                permission.setPermissionCode(String.format("%s_%s_%s", system.getSystemCode(), resource, action));
                permission.setPermissionName(String.format("%s %s", action, resource));
                permission.setDescription(String.format("Permission to %s %s", action.toLowerCase(), resource.toLowerCase()));
                permission.setResourceType(resource);
                permission.setAction(action);
                permission.setSystem(system);
                permission.setCreatedAt(LocalDateTime.now());
                permissions.add(permission);
            }
        }

        // Special permissions
        permissions.add(createPermission(system, "DASHBOARD_VIEW", "View Dashboard", "VIEW"));
        permissions.add(createPermission(system, "AUDIT_VIEW", "View Audit Logs", "READ"));
        permissions.add(createPermission(system, "SYSTEM_MANAGE", "Manage System", "MANAGE"));

        return permissionRepository.saveAll(permissions);
    }

    private List<Role> createTestRoles(UniSystem system, List<Permission> permissions) {
        List<Role> roles = new ArrayList<>();

        // Admin role (all permissions)
        Role adminRole = createRole(system, "ADMIN", "Administrator", "Full system access");
        adminRole.setPermissions(new HashSet<>(permissions));
        roles.add(adminRole);

        // Manager role (most permissions except DELETE)
        Role managerRole = createRole(system, "MANAGER", "Manager", "Management access");
        Set<Permission> managerPerms = permissions.stream()
                .filter(p -> !p.getAction().equals("DELETE"))
                .collect(Collectors.toSet());
        managerRole.setPermissions(managerPerms);
        roles.add(managerRole);

        // User role (READ and CREATE only)
        Role userRole = createRole(system, "USER", "User", "Standard user access");
        Set<Permission> userPerms = permissions.stream()
                .filter(p -> p.getAction().equals("READ") || p.getAction().equals("CREATE"))
                .collect(Collectors.toSet());
        userRole.setPermissions(userPerms);
        roles.add(userRole);

        // Viewer role (READ only)
        Role viewerRole = createRole(system, "VIEWER", "Viewer", "Read-only access");
        Set<Permission> viewerPerms = permissions.stream()
                .filter(p -> p.getAction().equals("READ"))
                .collect(Collectors.toSet());
        viewerRole.setPermissions(viewerPerms);
        roles.add(viewerRole);

        return roleRepository.saveAll(roles);
    }

    private List<User> createTestUsers(UniSystem system, List<Role> roles) {
        List<User> users = new ArrayList<>();

        Map<String, String> userData = Map.of(
                "admin", "System Administrator",
                "manager", "Department Manager",
                "john_doe", "John Doe",
                "jane_smith", "Jane Smith",
                "demo_user", "Demo User",
                "view_only", "View Only User"
        );

        Map<String, String> roleMapping = Map.of(
                "admin", "ADMIN",
                "manager", "MANAGER",
                "john_doe", "USER",
                "jane_smith", "USER",
                "demo_user", "USER",
                "view_only", "VIEWER"
        );

        for (Map.Entry<String, String> entry : userData.entrySet()) {
            User user = new User();
            user.setUsername(entry.getKey());
            user.setEmail(entry.getKey().replace(".", "") + "@demo.company.com");
            user.setPasswordHash(passwordEncoder.encode("QAZxsw123!"));
            user.setFullName(entry.getValue());
            user.setPhone("+84123456789");
            user.setAvatarUrl("https://ui-avatars.com/api/?name=" + entry.getValue().replace(" ", "+"));
            user.setIsActive(true);
            user.setIsLocked(false);
            user.setLastLogin(LocalDateTime.now().minusDays(new Random().nextInt(30)));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Assign system
            user.setSystems(Set.of(system));

            // Assign role
            String roleCode = system.getSystemCode() + "_" + roleMapping.get(entry.getKey());
            Role userRole = roles.stream()
                    .filter(r -> r.getRoleCode().equals(roleCode))
                    .findFirst()
                    .orElse(roles.get(0)); // Fallback to admin role

            user.setRoles(Set.of(userRole));

            users.add(user);
        }

        return userRepository.saveAll(users);
    }

    private Map<String, String> createDemoCredentials() {
        return Map.of(
                "admin", "QAZxsw123!",
                "manager", "QAZxsw123!",
                "john_doe", "QAZxsw123!",
                "jane_smith", "QAZxsw123!",
                "demo_user", "QAZxsw123!"
        );
    }

    private UniSystem createSystem(String code, String name, String baseUrl, String email) {
        UniSystem system = new UniSystem();
        system.setSystemCode(code);
        system.setSystemName(name);
        system.setDescription("Demo system for testing purposes");
        system.setBaseUrl(baseUrl);
        system.setContactEmail(email);
        system.setIsActive(true);
        system.setCreatedAt(LocalDateTime.now());
        system.setUpdatedAt(LocalDateTime.now());
        return system;
    }

    private List<Permission> generateStandardPermissions(UniSystem system) {
        List<Permission> permissions = new ArrayList<>();

        // Module-based permissions
        String[][] modules = {
                {"USER", "User Management"},
                {"ROLE", "Role Management"},
                {"PERMISSION", "Permission Management"},
                {"SYSTEM", "System Management"},
                {"REPORT", "Report Management"},
                {"DASHBOARD", "Dashboard"},
                {"SETTING", "Settings"},
                {"AUDIT", "Audit Logs"}
        };

        String[] actions = {"CREATE", "READ", "UPDATE", "DELETE", "EXPORT", "IMPORT"};

        for (String[] module : modules) {
            for (String action : actions) {
                Permission permission = new Permission();
                permission.setPermissionCode(String.format("%s_%s_%s", system.getSystemCode(), module[0], action));
                permission.setPermissionName(String.format("%s %s", action, module[1]));
                permission.setDescription(String.format("Allows %s operation on %s", action.toLowerCase(), module[1].toLowerCase()));
                permission.setResourceType(module[0]);
                permission.setAction(action);
                permission.setSystem(system);
                permission.setCreatedAt(LocalDateTime.now());
                permissions.add(permission);
            }
        }

        return permissionRepository.saveAll(permissions);
    }

    private Map<String, List<String>> createRolePermissionMapping() {
        Map<String, List<String>> mapping = new HashMap<>();

        mapping.put("ADMIN", Arrays.asList("CREATE", "READ", "UPDATE", "DELETE", "EXPORT", "IMPORT"));
        mapping.put("MANAGER", Arrays.asList("CREATE", "READ", "UPDATE", "EXPORT"));
        mapping.put("USER", Arrays.asList("CREATE", "READ", "UPDATE"));
        mapping.put("VIEWER", Arrays.asList("READ"));
        mapping.put("AUDITOR", Arrays.asList("READ", "EXPORT"));

        return mapping;
    }

    private List<Role> generateRolesWithPermissions(UniSystem system, List<Permission> permissions,
                                                    Map<String, List<String>> rolePermissionMapping) {
        List<Role> roles = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : rolePermissionMapping.entrySet()) {
            Role role = new Role();
            role.setRoleCode(system.getSystemCode() + "_" + entry.getKey());
            role.setRoleName(entry.getKey() + " Role");
            role.setDescription(entry.getKey() + " role for " + system.getSystemName());
            role.setSystem(system);
            role.setIsSystemRole(entry.getKey().equals("ADMIN"));
            role.setCreatedAt(LocalDateTime.now());

            // Filter permissions based on allowed actions
            Set<Permission> rolePermissions = permissions.stream()
                    .filter(p -> entry.getValue().contains(p.getAction()))
                    .collect(Collectors.toSet());
            role.setPermissions(rolePermissions);

            roles.add(role);
        }

        return roleRepository.saveAll(roles);
    }

    private List<User> generateUsersForSystem(UniSystem system, List<Role> roles) {
        List<User> users = new ArrayList<>();
        Random random = new Random();

        String[][] userProfiles = {
                {"sysadmin", "System Administrator", "ADMIN"},
                {"hradmin", "HR Administrator", "ADMIN"},
                {"finmanager", "Finance Manager", "MANAGER"},
                {"salesrep", "Sales Representative", "USER"},
                {"support", "Support Agent", "USER"},
                {"auditor", "System Auditor", "AUDITOR"},
                {"viewer", "Report Viewer", "VIEWER"}
        };

        for (String[] profile : userProfiles) {
            User user = new User();
            user.setUsername(profile[0]);
            user.setEmail(profile[0] + "@" + system.getSystemCode().toLowerCase() + ".company.com");
            user.setPasswordHash(passwordEncoder.encode("QAZxsw123!"));
            user.setFullName(profile[1]);
            user.setPhone("+8498" + String.format("%07d", random.nextInt(10000000)));
            user.setAvatarUrl("https://ui-avatars.com/api/?name=" + profile[1].replace(" ", "+"));
            user.setIsActive(true);
            user.setIsLocked(false);
            user.setLastLogin(LocalDateTime.now().minusDays(random.nextInt(60)));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Assign system
            user.setSystems(Set.of(system));

            // Find and assign role
            String targetRoleCode = system.getSystemCode() + "_" + profile[2];
            Role targetRole = roles.stream()
                    .filter(r -> r.getRoleCode().equals(targetRoleCode))
                    .findFirst()
                    .orElse(roles.get(0));

            user.setRoles(Set.of(targetRole));
            users.add(user);
        }

        return userRepository.saveAll(users);
    }

    private Permission createPermission(UniSystem system, String code, String name, String action) {
        Permission permission = new Permission();
        permission.setPermissionCode(String.format("%s_%s", system.getSystemCode(), code));
        permission.setPermissionName(name);
        permission.setDescription(name + " permission");
        permission.setResourceType(code.split("_")[0]);
        permission.setAction(action);
        permission.setSystem(system);
        permission.setCreatedAt(LocalDateTime.now());
        return permission;
    }

    private Role createRole(UniSystem system, String roleCode, String roleName, String description) {
        Role role = new Role();
        role.setRoleCode(String.format("%s_%s", system.getSystemCode(), roleCode));
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setSystem(system);
        role.setIsSystemRole(roleCode.equals("ADMIN"));
        role.setCreatedAt(LocalDateTime.now());
        return role;
    }

    private String getRandomAction() {
        String[] actions = {"CREATE", "READ", "UPDATE", "DELETE", "VIEW", "EXPORT", "IMPORT", "APPROVE", "REJECT"};
        return actions[new Random().nextInt(actions.length)];
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSystemSummary(String systemCode) {
        Map<String, Object> summary = new HashMap<>();

        try {
            UniSystem system = systemRepository.findBySystemCode(systemCode)
                    .orElseThrow(() -> new RuntimeException("System not found: " + systemCode));

            summary.put("system", system.getSystemCode());
            summary.put("systemName", system.getSystemName());
            summary.put("isActive", system.getIsActive());

            // User count
            long userCount = userRepository.findBySystemCodes(Set.of(systemCode)).size();
            summary.put("userCount", userCount);

            // Role count
            long roleCount = roleRepository.findBySystemCode(systemCode).size();
            summary.put("roleCount", roleCount);

            // Permission count
            long permissionCount = permissionRepository.findBySystemCode(systemCode).size();
            summary.put("permissionCount", permissionCount);

            // Active users
            long activeUsers = userRepository.findBySystemCodes(Set.of(systemCode)).stream()
                    .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                    .count();
            summary.put("activeUsers", activeUsers);

            summary.put("success", true);

        } catch (Exception e) {
            log.error("Failed to get system summary", e);
            summary.put("success", false);
            summary.put("error", e.getMessage());
        }

        return summary;
    }
}