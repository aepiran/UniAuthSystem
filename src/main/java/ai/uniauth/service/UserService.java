package ai.uniauth.service;


import ai.uniauth.model.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

public interface UserService {

    // CRUD Operations
    UserDTO createUser(UserCreateDTO userCreateDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    UserDTO getUserByEmail(String email);
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> getUsersBySystem(String systemCode, Pageable pageable);
    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    void deleteUser(Long id);

    // Assignment Operations
    void assignUserToSystems(Long userId, Set<String> systemCodes);
    void removeUserFromSystems(Long userId, Set<String> systemCodes);
    void assignRolesToUser(Long userId, Set<Long> roleIds);
    void removeRolesFromUser(Long userId, Set<Long> roleIds);

    // Query Operations
    List<UserDTO> searchUsers(String keyword);
    List<UserDTO> getUsersByRole(String roleCode);
    boolean checkUserExists(String username, String email);
    boolean isUserActive(String username);

    // Permission Operations
    boolean hasPermission(String username, String permissionCode);
    Set<String> getUserPermissions(String username);
    Set<String> getUserPermissionsBySystem(String username, String systemCode);

    // Authentication related
    void updateLastLogin(String username);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void resetPassword(Long userId, String newPassword);

    // Status Management
    void activateUser(Long userId);
    void deactivateUser(Long userId);
    void lockUser(Long userId);
    void unlockUser(Long userId);
    Page<UserDTO> searchUsersWithCriteria(UserSearchCriteriaDTO criteria, Pageable pageable);

    UserDTO verifyUser(UserVerificationDTO verificationDTO);
}