package ai.uniauth;

import ai.uniauth.model.entity.*;
import ai.uniauth.rep.*;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("dev | test")
@Component
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final SystemRepository systemRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    @Transactional
    public CommandLineRunner initializeMockData() {
        return args -> {
            if (systemRepository.count() > 0) {
                log.info("Database already has data. Skipping mock data initialization.");
                return;
            }

            log.info("Initializing mock data for UniAuth System...");

            // Tạo dữ liệu theo từng bước, tránh xung đột
            try {
                // 1. Tạo các hệ thống (Systems)
                List<UniSystem> systems = createAndSaveSystems();

                // 2. Tạo các permission cho từng hệ thống
                Map<String, List<Permission>> systemPermissions = createAndSavePermissions(systems);

                // 3. Tạo các role và gán permission
                Map<String, List<Role>> systemRoles = createAndSaveRoles(systems, systemPermissions);

                // 4. Tạo users
                List<User> users = createUsers();

                // 5. Lưu users trước (không có relationships)
                List<User> savedUsers = saveUsers(users);

                // 6. Gán systems và roles cho users (sau khi đã lưu)
                assignSystemsAndRolesToUsers(savedUsers, systems, systemRoles);

                log.info("Mock data initialization completed successfully!");
                log.info("Created: {} systems, {} permissions, {} roles, {} users",
                        systems.size(),
                        systemPermissions.values().stream().mapToInt(List::size).sum(),
                        systemRoles.values().stream().mapToInt(List::size).sum(),
                        savedUsers.size());

            } catch (Exception e) {
                log.error("Error initializing mock data", e);
                throw new RuntimeException("Failed to initialize mock data", e);
            }
        };
    }

    private List<UniSystem> createAndSaveSystems() {
        List<UniSystem> systems = Arrays.asList(
                createSystem("HRM", "Human Resource Management", "https://hrm.company.com", "hrm@company.com"),
                createSystem("CRM", "Customer Relationship Management", "https://crm.company.com", "crm@company.com"),
                createSystem("ERP", "Enterprise Resource Planning", "https://erp.company.com", "erp@company.com"),
                createSystem("SCM", "Supply Chain Management", "https://scm.company.com", "scm@company.com"),
                createSystem("FIN", "Financial Management", "https://finance.company.com", "finance@company.com"),
                createSystem("PROJ", "Project Management", "https://project.company.com", "project@company.com"),
                createSystem("INV", "Inventory Management", "https://inventory.company.com", "inventory@company.com"),
                createSystem("BIZ", "Business Intelligence", "https://bi.company.com", "bi@company.com"),
                createSystem("ADMIN", "System Administration", "https://admin.company.com", "admin@company.com"),
                createSystem("PORTAL", "Employee Portal", "https://portal.company.com", "portal@company.com")
        );

        // Generate API keys
        systems.forEach(system -> {
            system.setApiKey(generateApiKey(system.getSystemCode()));
            system.setSecretKey(passwordEncoder.encode(system.getApiKey()));
        });

        // Lưu từng system một để tránh xung đột
        List<UniSystem> savedSystems = new ArrayList<>();
        for (UniSystem system : systems) {
            savedSystems.add(systemRepository.save(system));
        }

        return savedSystems;
    }

    private UniSystem createSystem(String code, String name, String baseUrl, String email) {
        UniSystem system = new UniSystem();
        system.setSystemCode(code);
        system.setSystemName(name);
        system.setDescription(name + " System for managing company resources");
        system.setBaseUrl(baseUrl);
        system.setContactEmail(email);
        system.setIsActive(true);
        system.setCreatedAt(LocalDateTime.now());
        system.setUpdatedAt(LocalDateTime.now());
        return system;
    }

    private Map<String, List<Permission>> createAndSavePermissions(List<UniSystem> systems) {
        Map<String, List<Permission>> systemPermissions = new HashMap<>();

        // Tạo và lưu permissions theo từng system
        for (UniSystem system : systems) {
            List<Permission> permissions = new ArrayList<>();

            // Tạo basic permissions
            permissions.addAll(createBasicPermissions(system));

            // Tạo system-specific permissions
            permissions.addAll(createSystemSpecificPermissions(system));

            // Lưu permissions
            List<Permission> savedPermissions = new ArrayList<>();
            for (Permission permission : permissions) {
                savedPermissions.add(permissionRepository.save(permission));
            }

            systemPermissions.put(system.getSystemCode(), savedPermissions);

            // Cập nhật system với permissions (nếu cần)
            system.setPermissions(new HashSet<>(savedPermissions));
            systemRepository.save(system);
        }

        return systemPermissions;
    }

    private List<Permission> createBasicPermissions(UniSystem system) {
        List<Permission> permissions = new ArrayList<>();
        String[][] resources = {
                {"USER", "User Management"},
                {"ROLE", "Role Management"},
                {"PERMISSION", "Permission Management"},
                {"REPORT", "Report Management"},
                {"SETTING", "System Settings"},
                {"DASHBOARD", "Dashboard"},
                {"AUDIT", "Audit Log"}
        };

        String[] actions = {"CREATE", "READ", "UPDATE", "DELETE", "EXPORT", "IMPORT"};

        for (String[] resource : resources) {
            for (String action : actions) {
                // Skip certain combinations
                if (shouldSkipPermission(system.getSystemCode(), resource[0], action)) {
                    continue;
                }

                Permission permission = new Permission();
                permission.setPermissionCode(
                        String.format("%s_%s_%s", system.getSystemCode(), resource[0], action)
                );
                permission.setPermissionName(
                        String.format("%s %s", action, resource[1])
                );
                permission.setDescription(
                        String.format("Permission to %s %s in %s system",
                                action.toLowerCase(), resource[1].toLowerCase(), system.getSystemName())
                );
                permission.setResourceType(resource[0]);
                permission.setAction(action);
                permission.setSystem(system);
                permission.setCreatedAt(LocalDateTime.now());
                permissions.add(permission);
            }
        }

        return permissions;
    }

    private List<Permission> createSystemSpecificPermissions(UniSystem system) {
        List<Permission> permissions = new ArrayList<>();

        switch (system.getSystemCode()) {
            case "HRM":
                permissions.addAll(Arrays.asList(
                        createPermission(system, "EMPLOYEE", "Manage employees"),
                        createPermission(system, "ATTENDANCE", "Track attendance"),
                        createPermission(system, "PAYROLL", "Process payroll"),
                        createPermission(system, "LEAVE", "Manage leaves"),
                        createPermission(system, "RECRUITMENT", "Manage recruitment"),
                        createPermission(system, "TRAINING", "Manage training")
                ));
                break;

            case "CRM":
                permissions.addAll(Arrays.asList(
                        createPermission(system, "CUSTOMER", "Manage customers"),
                        createPermission(system, "LEAD", "Manage leads"),
                        createPermission(system, "SALES", "Manage sales"),
                        createPermission(system, "SUPPORT", "Manage support"),
                        createPermission(system, "MARKETING", "Manage marketing"),
                        createPermission(system, "CONTRACT", "Manage contracts")
                ));
                break;

            case "FIN":
                permissions.addAll(Arrays.asList(
                        createPermission(system, "INVOICE", "Manage invoices"),
                        createPermission(system, "PAYMENT", "Process payments"),
                        createPermission(system, "BUDGET", "Manage budgets"),
                        createPermission(system, "TAX", "Manage taxes"),
                        createPermission(system, "ACCOUNTING", "Manage accounting")
                ));
                break;

            case "ADMIN":
                permissions.addAll(Arrays.asList(
                        createPermission(system, "SYSTEM", "Manage systems"),
                        createPermission(system, "BACKUP", "Manage backups"),
                        createPermission(system, "MONITORING", "Monitor systems"),
                        createPermission(system, "SECURITY", "Manage security")
                ));
                break;
        }

        return permissions;
    }

    private Permission createPermission(UniSystem system, String resource, String description) {
        Permission permission = new Permission();
        permission.setPermissionCode(String.format("%s_%s_MANAGE", system.getSystemCode(), resource));
        permission.setPermissionName("Manage " + resource);
        permission.setDescription(description);
        permission.setResourceType(resource);
        permission.setAction("MANAGE");
        permission.setSystem(system);
        permission.setCreatedAt(LocalDateTime.now());
        return permission;
    }

    private boolean shouldSkipPermission(String systemCode, String resource, String action) {
        // Skip DELETE for ADMIN on USER
        if (systemCode.equals("ADMIN") && resource.equals("USER") && action.equals("DELETE")) {
            return true;
        }

        // Skip CREATE for FIN on REPORT
        if (systemCode.equals("FIN") && resource.equals("REPORT") && action.equals("CREATE")) {
            return true;
        }

        return false;
    }

    private Map<String, List<Role>> createAndSaveRoles(List<UniSystem> systems,
                                                       Map<String, List<Permission>> systemPermissions) {
        Map<String, List<Role>> systemRoles = new HashMap<>();

        for (UniSystem system : systems) {
            List<Role> roles = new ArrayList<>();
            List<Permission> permissions = systemPermissions.get(system.getSystemCode());

            // Tạo các role cơ bản
            roles.add(createRole(system, "ADMIN", "System Administrator", "Full system access"));
            roles.add(createRole(system, "MANAGER", "Department Manager", "Manager level access"));
            roles.add(createRole(system, "USER", "Regular User", "Basic access"));
            roles.add(createRole(system, "VIEWER", "View Only", "Read-only access"));
            roles.add(createRole(system, "AUDITOR", "Auditor", "Audit access"));

            // Tạo role system-specific
            roles.addAll(createSystemSpecificRoles(system));

            // Lưu roles và gán permissions
            List<Role> savedRoles = new ArrayList<>();
            for (Role role : roles) {
                // Gán permissions dựa trên loại role
                assignPermissionsToRole(role, permissions);

                Role savedRole = roleRepository.save(role);
                savedRoles.add(savedRole);
            }

            systemRoles.put(system.getSystemCode(), savedRoles);

            // Cập nhật system với roles
            system.setRoles(new HashSet<>(savedRoles));
            systemRepository.save(system);
        }

        return systemRoles;
    }

    private Role createRole(UniSystem system, String roleCode, String roleName, String description) {
        Role role = new Role();
        role.setRoleCode(String.format("%s_%s", system.getSystemCode(), roleCode));
        role.setRoleName(String.format("%s %s", system.getSystemName(), roleName));
        role.setDescription(description);
        role.setSystem(system);
        role.setIsSystemRole(roleCode.equals("ADMIN"));
        role.setCreatedAt(LocalDateTime.now());
        return role;
    }

    private List<Role> createSystemSpecificRoles(UniSystem system) {
        List<Role> roles = new ArrayList<>();

        switch (system.getSystemCode()) {
            case "HRM":
                roles.add(createSimpleRole(system, "HR_MANAGER", "HR Manager"));
                roles.add(createSimpleRole(system, "RECRUITER", "Recruiter"));
                roles.add(createSimpleRole(system, "PAYROLL_OFFICER", "Payroll Officer"));
                break;

            case "CRM":
                roles.add(createSimpleRole(system, "SALES_REP", "Sales Representative"));
                roles.add(createSimpleRole(system, "SUPPORT_AGENT", "Support Agent"));
                roles.add(createSimpleRole(system, "MARKETING_SPECIALIST", "Marketing Specialist"));
                break;

            case "FIN":
                roles.add(createSimpleRole(system, "ACCOUNTANT", "Accountant"));
                roles.add(createSimpleRole(system, "FINANCIAL_ANALYST", "Financial Analyst"));
                break;
        }

        return roles;
    }

    private Role createSimpleRole(UniSystem system, String roleCode, String roleName) {
        Role role = new Role();
        role.setRoleCode(String.format("%s_%s", system.getSystemCode(), roleCode));
        role.setRoleName(roleName);
        role.setDescription(roleName + " for " + system.getSystemName());
        role.setSystem(system);
        role.setIsSystemRole(false);
        role.setCreatedAt(LocalDateTime.now());
        return role;
    }

    private void assignPermissionsToRole(Role role, List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }

        Set<Permission> rolePermissions = new HashSet<>();
        String roleCode = role.getRoleCode();

        // Dựa trên loại role để gán permissions
        if (roleCode.endsWith("_ADMIN")) {
            // Admin gets all permissions
            rolePermissions.addAll(permissions);
        } else if (roleCode.endsWith("_MANAGER")) {
            // Manager gets most permissions except DELETE on certain resources
            for (Permission permission : permissions) {
                if (!permission.getAction().equals("DELETE") ||
                        !permission.getResourceType().equals("SYSTEM")) {
                    rolePermissions.add(permission);
                }
            }
        } else if (roleCode.endsWith("_USER")) {
            // User gets READ and CREATE
            for (Permission permission : permissions) {
                if (permission.getAction().equals("READ") ||
                        permission.getAction().equals("CREATE")) {
                    rolePermissions.add(permission);
                }
            }
        } else if (roleCode.endsWith("_VIEWER")) {
            // Viewer gets only READ
            for (Permission permission : permissions) {
                if (permission.getAction().equals("READ")) {
                    rolePermissions.add(permission);
                }
            }
        } else if (roleCode.endsWith("_AUDITOR")) {
            // Auditor gets AUDIT and REPORT
            for (Permission permission : permissions) {
                if (permission.getResourceType().equals("AUDIT") ||
                        permission.getResourceType().equals("REPORT")) {
                    rolePermissions.add(permission);
                }
            }
        }

        role.setPermissions(rolePermissions);
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        // Admin users
        users.add(createUser("admin", "admin@company.com", "System", "Administrator", "0987654321"));
        users.add(createUser("superadmin", "superadmin@company.com", "Super", "Admin", "0987654322"));

        // Department heads
        users.add(createUser("hradmin", "hr.admin@company.com", "HR", "Manager", "0987654323"));
        users.add(createUser("crmadmin", "crm.admin@company.com", "CRM", "Manager", "0987654324"));
        users.add(createUser("finadmin", "finance.admin@company.com", "Finance", "Manager", "0987654325"));

        // Regular employees
        users.add(createUser("john.doe", "john.doe@company.com", "John", "Doe", "0987654326"));
        users.add(createUser("jane.smith", "jane.smith@company.com", "Jane", "Smith", "0987654327"));
        users.add(createUser("bob.johnson", "bob.johnson@company.com", "Bob", "Johnson", "0987654328"));
        users.add(createUser("alice.williams", "alice.williams@company.com", "Alice", "Williams", "0987654329"));
        users.add(createUser("charlie.brown", "charlie.brown@company.com", "Charlie", "Brown", "0987654330"));

        // System-specific users
        users.add(createUser("hrm.user", "hrm.user@company.com", "HRM", "User", "0987654331"));
        users.add(createUser("crm.user", "crm.user@company.com", "CRM", "User", "0987654332"));
        users.add(createUser("fin.user", "fin.user@company.com", "FIN", "User", "0987654333"));

        // Special users
        users.add(createUser("inactive.user", "inactive@company.com", "Inactive", "User", "0987654334"));
        users.get(users.size() - 1).setIsActive(false);

        users.add(createUser("locked.user", "locked@company.com", "Locked", "User", "0987654335"));
        users.get(users.size() - 1).setIsLocked(true);

        return users;
    }

    private User createUser(String username, String email, String firstName,
                            String lastName, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("Password123!"));
        user.setFullName(firstName + " " + lastName);
        user.setPhone(phone);
        user.setAvatarUrl("https://ui-avatars.com/api/?name=" + firstName + "+" + lastName);
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Set last login
        Random random = new Random();
        if (username.contains("admin")) {
            user.setLastLogin(LocalDateTime.now().minusDays(1));
        } else {
            user.setLastLogin(LocalDateTime.now().minusDays(random.nextInt(30)));
        }

        return user;
    }

    private List<User> saveUsers(List<User> users) {
        // Lưu users không có relationships trước
        List<User> savedUsers = new ArrayList<>();
        for (User user : users) {
            // Tạo các collections rỗng
            user.setSystems(new HashSet<>());
            user.setRoles(new HashSet<>());

            User savedUser = userRepository.save(user);
            savedUsers.add(savedUser);
        }
        return savedUsers;
    }

    private void assignSystemsAndRolesToUsers(List<User> users, List<UniSystem> systems,
                                              Map<String, List<Role>> systemRoles) {
        // Map users by username
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUsername(), user);
        }

        // Map systems by code
        Map<String, UniSystem> systemMap = new HashMap<>();
        for (UniSystem system : systems) {
            systemMap.put(system.getSystemCode(), system);
        }

        // Map roles by system and code
        Map<String, Map<String, Role>> roleMap = new HashMap<>();
        for (Map.Entry<String, List<Role>> entry : systemRoles.entrySet()) {
            Map<String, Role> systemRoleMap = new HashMap<>();
            for (Role role : entry.getValue()) {
                systemRoleMap.put(role.getRoleCode(), role);
            }
            roleMap.put(entry.getKey(), systemRoleMap);
        }

        // Gán cho từng user
        for (User user : users) {
            Set<UniSystem> assignedSystems = new HashSet<>();
            Set<Role> assignedRoles = new HashSet<>();

            switch (user.getUsername()) {
                case "superadmin":
                case "admin":
                    // Access to all systems with ADMIN roles
                    assignedSystems.addAll(systems);
                    for (UniSystem system : systems) {
                        Role adminRole = roleMap.get(system.getSystemCode()).get(system.getSystemCode() + "_ADMIN");
                        if (adminRole != null) {
                            assignedRoles.add(adminRole);
                        }
                    }
                    break;

                case "hradmin":
                    assignedSystems.add(systemMap.get("HRM"));
                    assignedRoles.add(roleMap.get("HRM").get("HRM_ADMIN"));
                    break;

                case "crmadmin":
                    assignedSystems.add(systemMap.get("CRM"));
                    assignedRoles.add(roleMap.get("CRM").get("CRM_ADMIN"));
                    break;

                case "finadmin":
                    assignedSystems.add(systemMap.get("FIN"));
                    assignedRoles.add(roleMap.get("FIN").get("FIN_ADMIN"));
                    break;

                case "hrm.user":
                    assignedSystems.add(systemMap.get("HRM"));
                    assignedRoles.add(roleMap.get("HRM").get("HRM_USER"));
                    break;

                case "crm.user":
                    assignedSystems.add(systemMap.get("CRM"));
                    assignedRoles.add(roleMap.get("CRM").get("CRM_USER"));
                    break;

                case "fin.user":
                    assignedSystems.add(systemMap.get("FIN"));
                    assignedRoles.add(roleMap.get("FIN").get("FIN_USER"));
                    break;

                default:
                    // Regular users get random assignments
                    assignRandomSystemsAndRoles(user, assignedSystems, assignedRoles,
                            systems, systemMap, roleMap);
                    break;
            }

            // Cập nhật user
            user.setSystems(assignedSystems);
            user.setRoles(assignedRoles);
            userRepository.save(user);
        }
    }

    private void assignRandomSystemsAndRoles(User user, Set<UniSystem> assignedSystems,
                                             Set<Role> assignedRoles, List<UniSystem> systems,
                                             Map<String, UniSystem> systemMap,
                                             Map<String, Map<String, Role>> roleMap) {
        Random random = new Random();

        // Assign 1-3 random systems
        List<UniSystem> shuffledSystems = new ArrayList<>(systems);
        Collections.shuffle(shuffledSystems);
        int numSystems = 1 + random.nextInt(3);

        for (int i = 0; i < Math.min(numSystems, shuffledSystems.size()); i++) {
            UniSystem system = shuffledSystems.get(i);
            assignedSystems.add(system);

            // Assign a non-ADMIN role for this system
            Map<String, Role> systemRoles = roleMap.get(system.getSystemCode());
            if (systemRoles != null) {
                List<Role> nonAdminRoles = systemRoles.values().stream()
                        .filter(r -> !r.getRoleCode().endsWith("_ADMIN"))
                        .collect(Collectors.toList());

                if (!nonAdminRoles.isEmpty()) {
                    Role randomRole = nonAdminRoles.get(random.nextInt(nonAdminRoles.size()));
                    assignedRoles.add(randomRole);
                }
            }
        }
    }

    private String generateApiKey(String systemCode) {
        return "UA-" + systemCode + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}