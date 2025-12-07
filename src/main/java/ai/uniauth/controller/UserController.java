package ai.uniauth.controller;

import ai.uniauth.model.dto.*;
import ai.uniauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users in UniAuth System")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    @PreAuthorize("hasPermission('USER_CREATE')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO createdUser = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasPermission('USER_READ') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    @PreAuthorize("hasPermission('USER_READ') or @securityService.isCurrentUser(#username)")
    public ResponseEntity<UserDTO> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    @PreAuthorize("hasPermission('USER_READ') or @securityService.isCurrentUserByEmail(#email)")
    public ResponseEntity<UserDTO> getUserByEmail(
            @Parameter(description = "Email") @PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    @PreAuthorize("hasPermission('USER_READ')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/system/{systemCode}")
    @Operation(summary = "Get users by system")
    @PreAuthorize("hasPermission('USER_READ')")
    public ResponseEntity<Page<UserDTO>> getUsersBySystem(
            @Parameter(description = "System code") @PathVariable String systemCode,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserDTO> users = userService.getUsersBySystem(systemCode, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasPermission('USER_UPDATE') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (soft delete)")
    @PreAuthorize("hasPermission('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by keyword")
    @PreAuthorize("hasPermission('USER_READ')")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/search/filter")
    @Operation(summary = "Search users with criteria")
    @PreAuthorize("hasPermission('USER_READ')")
    public ResponseEntity<Page<UserDTO>> searchUsersWithCriteria(
            @RequestBody UserSearchCriteriaDTO criteria,
            @PageableDefault(size = 20) Pageable pageable) {
//        Page<UserDTO> users = userService.searchUsersWithCriteria(criteria, pageable);
        return ResponseEntity.ok(Page.empty());
    }

    @PostMapping("/{userId}/systems/assign")
    @Operation(summary = "Assign user to systems")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> assignUserToSystems(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody Set<String> systemCodes) {
        userService.assignUserToSystems(userId, systemCodes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/roles/assign")
    @Operation(summary = "Assign roles to user")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> assignRolesToUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody Set<Long> roleIds) {
        userService.assignRolesToUser(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/remove")
    @Operation(summary = "Remove roles from user")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> removeRolesFromUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody Set<Long> roleIds) {
        userService.removeRolesFromUser(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/permissions")
    @Operation(summary = "Get user permissions")
    @PreAuthorize("hasPermission('USER_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Set<String>> getUserPermissions(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        Set<String> permissions = userService.getUserPermissions(userId.toString());
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{userId}/permissions/check")
    @Operation(summary = "Check if user has permission")
    @PreAuthorize("hasPermission('USER_READ') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Boolean> checkUserPermission(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam String permissionCode) {
        boolean hasPermission = userService.hasPermission(userId.toString(), permissionCode);
        return ResponseEntity.ok(hasPermission);
    }

    @PatchMapping("/{userId}/activate")
    @Operation(summary = "Activate user")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> activateUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/lock")
    @Operation(summary = "Lock user")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> lockUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        userService.lockUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/unlock")
    @Operation(summary = "Unlock user")
    @PreAuthorize("hasPermission('USER_UPDATE')")
    public ResponseEntity<Void> unlockUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        userService.unlockUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/role/{roleCode}")
    @Operation(summary = "Get users by role")
    @PreAuthorize("hasPermission('USER_READ')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @Parameter(description = "Role code") @PathVariable String roleCode) {
        List<UserDTO> users = userService.getUsersByRole(roleCode);
        return ResponseEntity.ok(users);
    }
}