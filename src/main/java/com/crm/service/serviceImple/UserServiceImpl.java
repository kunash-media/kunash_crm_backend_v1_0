package com.crm.service.serviceImple;

import com.crm.config.BcryptEncoderConfig;
import com.crm.dto.request.UserPatchDTO;
import com.crm.dto.request.UserRegistrationDTO;
import com.crm.dto.response.UserResponseDTO;
import com.crm.dto.stats.UserSummaryDto;
import com.crm.entity.UserEntity;
import com.crm.repository.UserRepository;
import com.crm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    private final UserRepository userRepository;
    private final BcryptEncoderConfig passwordEncoder;


    public UserServiceImpl(UserRepository userRepository,
                           BcryptEncoderConfig passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─────────────────────────────────────────────────────────────
    // REGISTER
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO dto) {
        log.info("[UserService] registerUser() - email={}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("[UserService] registerUser() - email already exists: {}", dto.getEmail());
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            log.warn("[UserService] registerUser() - phone already exists: {}", dto.getPhone());
            throw new RuntimeException("Phone number already registered");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setUserStatus(dto.getUserStatus());
        UserEntity savedUser = userRepository.save(user);
        log.info("[UserService] registerUser() - user saved, userId={}", savedUser.getUserId());

        return buildResponse(savedUser);
    }

    public List<UserSummaryDto> getUserSummary() {
        return userRepository.findAll().stream().map(u -> {
            UserSummaryDto dto = new UserSummaryDto();
            dto.setUserId(u.getUserId());
            dto.setFirstName(u.getFirstName());
            dto.setLastName(u.getLastName());
            dto.setEmail(u.getEmail());
            dto.setUserStatus(u.getUserStatus() != null ? u.getUserStatus().toString() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────────────────────
    @Override
    public UserResponseDTO getUserById(Long userId) {
        log.info("[UserService] getUserById() - userId={}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[UserService] getUserById() - userId={} not found", userId);
                    return new RuntimeException("User not found: " + userId);
                });

        log.info("[UserService] getUserById() - found userId={}", userId);
        return buildResponse(user);
    }

    // ─────────────────────────────────────────────────────────────
    // GET ALL WITH PAGINATION
    // ─────────────────────────────────────────────────────────────
    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.info("[UserService] getAllUsers() - page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<UserEntity> users = userRepository.findAll(pageable);
        log.info("[UserService] getAllUsers() - totalElements={}", users.getTotalElements());

        return users.map(this::buildResponse);
    }

    // ─────────────────────────────────────────────────────────────
    // PATCH USER
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserResponseDTO patchUser(Long userId, UserPatchDTO dto) {
        log.info("[UserService] patchUser() - userId={}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[UserService] patchUser() - userId={} not found", userId);
                    return new RuntimeException("User not found: " + userId);
                });

        StringBuilder changes = new StringBuilder();

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
            changes.append("firstName ");
        }
        if (dto.getMiddleName() != null) {
            user.setMiddleName(dto.getMiddleName());
            changes.append("middleName ");
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
            changes.append("lastName ");
        }
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                log.warn("[UserService] patchUser() - email conflict: {}", dto.getEmail());
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(dto.getEmail());
            changes.append("email ");
        }
        if (dto.getPhone() != null) {
            if (!dto.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(dto.getPhone())) {
                log.warn("[UserService] patchUser() - phone conflict: {}", dto.getPhone());
                throw new RuntimeException("Phone number already in use");
            }
            user.setPhone(dto.getPhone());
            changes.append("phone ");
        }

        if(dto.getUserStatus() != null) {
            user.setUserStatus(dto.getUserStatus());
            changes.append("userStatus");
        }


        UserEntity updated = userRepository.save(user);
        log.info("[UserService] patchUser() - userId={} updated, changed fields: [{}]",
                userId, changes.toString().trim());

        return buildResponse(updated);
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE USER
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("[UserService] deleteUser() - userId={}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[UserService] deleteUser() - userId={} not found", userId);
                    return new RuntimeException("User not found: " + userId);
                });

        userRepository.delete(user);
        log.info("[UserService] deleteUser() - userId={} deleted", userId);
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPER
    // ─────────────────────────────────────────────────────────────
    private UserResponseDTO buildResponse(UserEntity user) {

        UserResponseDTO response = new UserResponseDTO();
        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setMiddleName(user.getMiddleName());
        response.setLastName(user.getLastName());

        String fullName = (user.getMiddleName() != null && !user.getMiddleName().isBlank())
                ? user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName()
                : user.getFirstName() + " " + user.getLastName();
        response.setFullName(fullName);

        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        response.setUserStatus(user.getUserStatus());
        return response;
    }


    @Override
    @Transactional
    public UserResponseDTO registerGoogleUser(String name, String email) {

        log.info("[UserService] registerGoogleUser() - email={}", email);

        if (userRepository.existsByEmail(email)) {
            log.info("[UserService] Google user already exists: {}", email);

            UserEntity existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return buildResponse(existingUser);
        }

        // ── Split name ──
        String[] parts = name != null ? name.split(" ") : new String[]{"User"};

        UserEntity user = new UserEntity();
        user.setFirstName(parts[0]);
        user.setLastName(parts.length > 1 ? parts[1] : "User");
        user.setEmail(email);


        user.setPhone("9999999999_" + System.currentTimeMillis()); // avoid duplicate
        user.setPasswordHash(passwordEncoder.encode("GOOGLE_AUTH"));

        UserEntity savedUser = userRepository.save(user);

        log.info("[UserService] Google user saved, userId={}", savedUser.getUserId());

        return buildResponse(savedUser);
    }

}
