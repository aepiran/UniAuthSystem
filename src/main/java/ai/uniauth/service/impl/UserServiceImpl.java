package ai.uniauth.service.impl;

import ai.uniauth.model.entity.*;
import ai.uniauth.model.dto.*;
import ai.uniauth.model.mapper.UserMapper;
import ai.uniauth.rep.*;
import ai.uniauth.service.UserService;
import ai.uniauth.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SystemRepository systemRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        // Validate
        if (userRepository.existsByUsername(userCreateDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + userCreateDTO.getUsername());
        }
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + userCreateDTO.getEmail());
        }

        // Map to entity
        User user = userMapper.toEntity(userCreateDTO);
        user.setPasswordHash(passwordEncoder.encode(userCreateDTO.getPassword()));

        // Assign systems
        Set<UniSystem> systems = systemRepository.findBySystemCodes(userCreateDTO.getSystemCodes());
        if (systems.size() != userCreateDTO.getSystemCodes().size()) {
            throw new ResourceNotFoundException("Some systems not found");
        }
        user.setSystems(systems);

        // Save
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersBySystem(String systemCode, Pageable pageable) {
        return userRepository.findBySystemCode(systemCode, pageable).map(userMapper::toDTO);
    }

    @Override
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        userMapper.updateEntity(userUpdateDTO, user);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", id);

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Soft delete: set inactive
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User deactivated with ID: {}", id);
    }

    @Override
    public void assignUserToSystems(Long userId, Set<String> systemCodes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Set<UniSystem> systems = systemRepository.findBySystemCodes(systemCodes);
        if (systems.size() != systemCodes.size()) {
            throw new ResourceNotFoundException("Some systems not found");
        }

        user.getSystems().addAll(systems);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Assigned user {} to systems: {}", userId, systemCodes);
    }

    @Override
    public void assignRolesToUser(Long userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new ResourceNotFoundException("Some roles not found");
        }

        // Check if roles belong to user's systems
        Set<String> userSystemCodes = user.getSystems().stream()
                .map(UniSystem::getSystemCode)
                .collect(Collectors.toSet());

        for (Role role : roles) {
            if (!userSystemCodes.contains(role.getSystem().getSystemCode())) {
                throw new BusinessException("User does not belong to system of role: " + role.getRoleCode());
            }
        }

        user.getRoles().addAll(roles);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Assigned roles {} to user {}", roleIds, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(String username, String permissionCode) {
        List<Permission> permissions = permissionRepository.findByUsername(username);
        return permissions.stream()
                .anyMatch(p -> p.getPermissionCode().equals(permissionCode));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(String username) {
        return permissionRepository.findByUsername(username).stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Override
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed for user ID: {}", userId);
    }

    // Other methods implementation...


    @Override
    public void removeUserFromSystems(Long userId, Set<String> systemCodes) {

    }

    @Override
    public void removeRolesFromUser(Long userId, Set<Long> roleIds) {

    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        return List.of();
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleCode) {
        return List.of();
    }

    @Override
    public boolean checkUserExists(String username, String email) {
        return false;
    }

    @Override
    public boolean isUserActive(String username) {
        return false;
    }

    @Override
    public Set<String> getUserPermissionsBySystem(String username, String systemCode) {
        return Set.of();
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {

    }

    @Override
    public void activateUser(Long userId) {

    }

    @Override
    public void deactivateUser(Long userId) {

    }

    @Override
    public void lockUser(Long userId) {

    }

    @Override
    public void unlockUser(Long userId) {

    }

    @Override
    public Page<UserDTO> searchUsersWithCriteria(UserSearchCriteriaDTO criteria, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public UserDTO verifyUser(UserVerificationDTO verificationDTO) {
        User user = userRepository.findByUsername(verificationDTO.getUsername())
                .orElseThrow(ResourceNotFoundException::new);

        if (passwordEncoder.matches(
                verificationDTO.getPassword(),
                user.getPasswordHash())
        ) {
            return userMapper.toDTO(user);
        }

        throw new BusinessException("Password is incorrect");
    }
}