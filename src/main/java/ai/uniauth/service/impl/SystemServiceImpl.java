package ai.uniauth.service.impl;

import ai.uniauth.exception.BusinessException;
import ai.uniauth.exception.ResourceAlreadyExistsException;
import ai.uniauth.exception.ResourceNotFoundException;
import ai.uniauth.model.dto.*;
import ai.uniauth.model.entity.UniSystem;
import ai.uniauth.model.entity.User;
import ai.uniauth.model.mapper.SystemMapper;
import ai.uniauth.rep.PermissionRepository;
import ai.uniauth.rep.RoleRepository;
import ai.uniauth.rep.SystemRepository;
import ai.uniauth.rep.UserRepository;
import ai.uniauth.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SystemServiceImpl implements SystemService {

    private final SystemRepository systemRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final SystemMapper systemMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyGenerator apiKeyGenerator;

    @Override
    public SystemRegistrationResponseDTO registerSystem(SystemCreateDTO systemCreateDTO) {
        // Validate system code uniqueness
        if (systemRepository.existsBySystemCode(systemCreateDTO.getSystemCode())) {
            throw new ResourceAlreadyExistsException("System code already exists: " + systemCreateDTO.getSystemCode());
        }

        // Map to entity
        UniSystem system = systemMapper.toEntity(systemCreateDTO);

        // Generate API keys
        String apiKey = apiKeyGenerator.generateApiKey();
        String secretKey = apiKeyGenerator.generateSecretKey();

        system.setApiKey(apiKey);
        system.setSecretKey(passwordEncoder.encode(secretKey));

        // Save system
        UniSystem savedSystem = systemRepository.save(system);

        log.info("System registered: {} with API key: {}", systemCreateDTO.getSystemCode(), apiKey);

        // Return response
        return new SystemRegistrationResponseDTO(
                savedSystem.getSystemCode(),
                savedSystem.getSystemName(),
                apiKey,
                secretKey,
                "System registered successfully. Please save your API keys securely.",
                savedSystem.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UniSystemDTO getSystemById(Long id) {
        UniSystem system = systemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with ID: " + id));
        return systemMapper.toDTO(system);
    }

    @Override
    @Transactional(readOnly = true)
    public UniSystemDTO getSystemByCode(String systemCode) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with code: " + systemCode));
        return systemMapper.toDTO(system);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UniSystemDTO> getAllSystems(Pageable pageable) {
        return systemRepository.findAll(pageable)
                .map(systemMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UniSystemDTO> getActiveSystems(Pageable pageable) {
        return systemRepository.findByIsActive(true, pageable)
                .map(systemMapper::toDTO);
    }

    @Override
    public UniSystemDTO updateSystem(Long id, SystemUpdateDTO systemUpdateDTO) {
        UniSystem system = systemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with ID: " + id));

        // Check if trying to deactivate system with active users
        if (Boolean.FALSE.equals(systemUpdateDTO.getIsActive()) && system.getIsActive()) {
            Long userCount = systemRepository.countUsersBySystemId(id);
            if (userCount > 0) {
                throw new BusinessException("Cannot deactivate system with " + userCount + " active users");
            }
        }

        systemMapper.updateEntity(systemUpdateDTO, system);
        system.setUpdatedAt(LocalDateTime.now());

        UniSystem updatedSystem = systemRepository.save(system);
        log.info("System updated: {}", system.getSystemCode());

        return systemMapper.toDTO(updatedSystem);
    }

    @Override
    public void deleteSystem(Long id) {
        UniSystem system = systemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with ID: " + id));

        // Check if system has users
        Long userCount = systemRepository.countUsersBySystemId(id);
        if (userCount > 0) {
            throw new BusinessException("Cannot delete system with " + userCount + " users. Remove users first.");
        }

        // Check if system has roles or permissions
        if (!system.getRoles().isEmpty() || !system.getPermissions().isEmpty()) {
            throw new BusinessException("Cannot delete system with existing roles or permissions. Remove them first.");
        }

        systemRepository.delete(system);
        log.info("System deleted: {}", system.getSystemCode());
    }

    @Override
    public void activateSystem(Long id) {

    }

    @Override
    public void deactivateSystem(Long id) {

    }

    @Override
    public SystemApiKeyResponseDTO regenerateApiKeys(Long systemId) {
        UniSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with ID: " + systemId));

        return regenerateApiKeysForSystem(system);
    }

    @Override
    public SystemApiKeyResponseDTO regenerateApiKeysByCode(String systemCode) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> new ResourceNotFoundException("System not found: " + systemCode));

        return regenerateApiKeysForSystem(system);
    }

    private SystemApiKeyResponseDTO regenerateApiKeysForSystem(UniSystem system) {
        String newApiKey = apiKeyGenerator.generateApiKey();
        String newSecretKey = apiKeyGenerator.generateSecretKey();

        system.setApiKey(newApiKey);
        system.setSecretKey(passwordEncoder.encode(newSecretKey));
        system.setUpdatedAt(LocalDateTime.now());

        systemRepository.save(system);

        log.warn("API keys regenerated for system: {}", system.getSystemCode());

        return new SystemApiKeyResponseDTO(
                system.getSystemCode(),
                newApiKey,
                newSecretKey,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(6) // Keys expire in 6 months
        );
    }

    @Override
    public boolean validateApiKey(String systemCode, String apiKey) {
        Optional<UniSystem> systemOpt = systemRepository.findByApiKey(apiKey);

        if (systemOpt.isEmpty()) {
            return false;
        }

        UniSystem system = systemOpt.get();
        return system.getSystemCode().equals(systemCode)
                && Boolean.TRUE.equals(system.getIsActive())
                && passwordEncoder.matches(apiKey, system.getSecretKey());
    }

    @Override
    public void revokeApiKey(Long systemId) {

    }

    @Override
    @Transactional(readOnly = true)
    public SystemStatusDTO getSystemStatus(String systemCode) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> new ResourceNotFoundException("System not found: " + systemCode));

        Long activeUsers = system.getUsers().stream()
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .count();

        // In production, you would query actual metrics
        return new SystemStatusDTO(
                system.getSystemCode(),
                system.getSystemName(),
                system.getIsActive(),
                system.getUpdatedAt(),
                activeUsers.intValue(),
                0 // This would come from metrics service
        );
    }

    @Override
    public List<SystemStatusDTO> getAllSystemsStatus() {
        return List.of();
    }

    @Override
    public void syncSystemData(SystemSyncRequestDTO syncRequest) {

    }

    @Override
    public void addUsersToSystem(Long systemId, Set<Long> userIds) {
        UniSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with ID: " + systemId));

        Set<User> users = new HashSet<>(userRepository.findAllById(userIds));
        if (users.size() != userIds.size()) {
            throw new ResourceNotFoundException("Some users not found");
        }

        system.getUsers().addAll(users);
        system.setUpdatedAt(LocalDateTime.now());
        systemRepository.save(system);

        log.info("Added {} users to system: {}", users.size(), system.getSystemCode());
    }

    @Override
    public void removeUsersFromSystem(Long systemId, Set<Long> userIds) {
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getSystemUsers(Long systemId, Pageable pageable) {
        UniSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new ResourceNotFoundException("System not found with ID: " + systemId));

        // Using custom query
        return userRepository.findBySystemCode(system.getSystemCode(), pageable)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    // Map user to DTO
                    return dto;
                });
    }

    @Override
    public Page<RoleDTO> getSystemRoles(Long systemId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<PermissionDTO> getSystemPermissions(Long systemId, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public SystemStatsDTO getSystemStatistics(String systemCode) {
        UniSystem system = systemRepository.findBySystemCode(systemCode)
                .orElseThrow(() -> new ResourceNotFoundException("System not found: " + systemCode));

        Long totalUsers = (long) system.getUsers().size();
        Long activeUsers = system.getUsers().stream()
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .count();

        return new SystemStatsDTO(
                system.getSystemCode(),
                system.getSystemName(),
                totalUsers,
                activeUsers,
                (long) system.getRoles().size(),
                (long) system.getPermissions().size(),
                0L, // Would come from analytics
                0L, // Would come from analytics
                0.0, // Would come from analytics
                system.getUpdatedAt()
        );
    }

    @Override
    public List<SystemActivityDTO> getSystemActivity(String systemCode, int days) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean systemExists(String systemCode) {
        return systemRepository.existsBySystemCode(systemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSystemActive(String systemCode) {
        return systemRepository.findBySystemCode(systemCode)
                .map(UniSystem::getIsActive)
                .orElse(false);
    }

    @Override
    public boolean validateSystemAccess(String systemCode, String apiKey) {
        return false;
    }

    // Helper class for API key generation
    @Component
    static class ApiKeyGenerator {
        private static final String API_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final String SECRET_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

        public String generateApiKey() {
            return "UA-" + generateRandomString(API_KEY_CHARS, 32);
        }

        public String generateSecretKey() {
            return generateRandomString(SECRET_KEY_CHARS, 64);
        }

        private String generateRandomString(String characterSet, int length) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(characterSet.charAt(random.nextInt(characterSet.length())));
            }
            return sb.toString();
        }
    }
}