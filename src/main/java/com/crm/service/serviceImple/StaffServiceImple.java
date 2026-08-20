package com.crm.service.serviceImple;

import com.crm.config.BcryptEncoderConfig;
import com.crm.dto.response.PagedResponse;
import com.crm.dto.request.StaffPatchRequest;
import com.crm.dto.request.StaffRegisterRequest;
import com.crm.dto.response.StaffDropdownDto;
import com.crm.dto.response.StaffRegisterdResponse;
import com.crm.dto.stats.StaffStatsDto;
import com.crm.entity.StaffEntity;
import com.crm.exception.DuplicateStaffException;
import com.crm.exception.InvalidCredentialsException;
import com.crm.exception.StaffNotFoundException;
import com.crm.repository.LeadRepository;
import com.crm.repository.StaffRepository;
import com.crm.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffServiceImple implements StaffService {

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImple.class);

    // auto-increasing page size guard rails
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final LeadRepository leadRepository;
    private final StaffRepository staffRepository;
    private final BcryptEncoderConfig bCryptPasswordEncoder;

    public StaffServiceImple(StaffRepository staffRepository, BcryptEncoderConfig bCryptPasswordEncoder, LeadRepository leadRepository) {
        this.staffRepository = staffRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.leadRepository = leadRepository;
    }

    @Override
    public StaffRegisterdResponse createStaff(StaffRegisterRequest request) {
        logger.info("Creating new staff record for email={}", request.getStaffEmail());


        if (staffRepository.existsByStaffEmail(request.getStaffEmail())) {
            logger.warn("Create failed — duplicate email={}", request.getStaffEmail());
            throw new DuplicateStaffException("A staff record already exists with email: " + request.getStaffEmail());
        }
        if (staffRepository.existsByStaffMobile(request.getStaffMobile())) {
            logger.warn("Create failed — duplicate mobile={}", request.getStaffMobile());
            throw new DuplicateStaffException("A staff record already exists with mobile number: " + request.getStaffMobile());
        }

        StaffEntity entity = new StaffEntity();

        entity.setStaffStrId(generateStaffStrId());
        entity.setStaffFirstName(request.getStaffFirstName());
        entity.setStaffMiddleName(request.getStaffMiddleName());
        entity.setStaffLastName(request.getStaffLastName());
        entity.setStaffMobile(request.getStaffMobile());
        entity.setStaffEmail(request.getStaffEmail());
        entity.setStaffWorkingEmail(request.getStaffWorkingEmail());
        entity.setStaffAddress(request.getStaffAddress());
        entity.setStaffSalary(request.getStaffSalary());
        entity.setJoiningDate(request.getJoiningDate());
        entity.setStaffRole(request.getStaffRole());
        entity.setStaffDepartment(request.getStaffDepartment());
        entity.setStaffPassword(bCryptPasswordEncoder.encode(request.getStaffPassword()));


        StaffEntity saved = staffRepository.save(entity);
        logger.info("Staff created successfully with staffPrimeId={}, staffStrId={}",
                saved.getStaffPrimeId(), saved.getStaffStrId());

        return toResponse(saved);
    }

    @Override
    public StaffRegisterdResponse getStaffById(Long staffPrimeId) {
        logger.debug("Fetching staff with staffPrimeId={}", staffPrimeId);

        StaffEntity entity = staffRepository.findById(staffPrimeId)
                .orElseThrow(() -> {
                    logger.warn("Staff not found for staffPrimeId={}", staffPrimeId);
                    return new StaffNotFoundException("Staff not found with id: " + staffPrimeId);
                });

        return toResponse(entity);
    }

    @Override
    public PagedResponse<StaffRegisterdResponse> getAllStaff(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = resolvePageSize(size);

        logger.info("Fetching staff list — page={}, size={}", safePage, safeSize);

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<StaffEntity> resultPage = staffRepository.findByStaffActiveTrue(pageable);

        List<StaffRegisterdResponse> content = resultPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        logger.info("Fetched {} staff records out of {} total", content.size(), resultPage.getTotalElements());

        return new PagedResponse<>(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    @Override
    public StaffRegisterdResponse patchStaff(Long staffPrimeId, StaffPatchRequest request) {
        logger.info("Patching staff with staffPrimeId={}", staffPrimeId);

        StaffEntity entity = staffRepository.findById(staffPrimeId)
                .orElseThrow(() -> {
                    logger.warn("Patch failed — staff not found for staffPrimeId={}", staffPrimeId);
                    return new StaffNotFoundException("Staff not found with id: " + staffPrimeId);
                });

        if (request.getStaffEmail() != null
                && staffRepository.existsByStaffEmailAndStaffPrimeIdNot(request.getStaffEmail(), staffPrimeId)) {
            logger.warn("Patch failed — duplicate email={} for staffPrimeId={}", request.getStaffEmail(), staffPrimeId);
            throw new DuplicateStaffException("A staff record already exists with email: " + request.getStaffEmail());
        }
        if (request.getStaffMobile() != null
                && staffRepository.existsByStaffMobileAndStaffPrimeIdNot(request.getStaffMobile(), staffPrimeId)) {
            logger.warn("Patch failed — duplicate mobile={} for staffPrimeId={}", request.getStaffMobile(), staffPrimeId);
            throw new DuplicateStaffException("A staff record already exists with mobile number: " + request.getStaffMobile());
        }

        if (request.getStaffFirstName() != null) {
            entity.setStaffFirstName(request.getStaffFirstName());
        }
        if (request.getStaffMiddleName() != null) {
            entity.setStaffMiddleName(request.getStaffMiddleName());
        }
        if (request.getStaffLastName() != null) {
            entity.setStaffLastName(request.getStaffLastName());
        }
        if (request.getStaffMobile() != null) {
            entity.setStaffMobile(request.getStaffMobile());
        }
        if (request.getStaffEmail() != null) {
            entity.setStaffEmail(request.getStaffEmail());
        }
        if (request.getStaffWorkingEmail() != null) {
            entity.setStaffWorkingEmail(request.getStaffWorkingEmail());
        }
        if (request.getStaffAddress() != null) {
            entity.setStaffAddress(request.getStaffAddress());
        }
        if (request.getStaffSalary() != null) {
            entity.setStaffSalary(request.getStaffSalary());
        }
        if (request.getJoiningDate() != null) {
            entity.setJoiningDate(request.getJoiningDate());
        }
        if (request.getStaffRole() != null) {
            entity.setStaffRole(request.getStaffRole());
        }
        if (request.getStaffDepartment() != null) {
            entity.setStaffDepartment(request.getStaffDepartment());
        }

        StaffEntity updated = staffRepository.save(entity);
        logger.info("Staff patched successfully for staffPrimeId={}", staffPrimeId);

        return toResponse(updated);
    }

    @Override
    public void deleteStaff(Long staffPrimeId) {
        logger.info("Deleting staff with staffPrimeId={}", staffPrimeId);

        if (!staffRepository.existsById(staffPrimeId)) {
            logger.warn("Delete failed — staff not found for staffPrimeId={}", staffPrimeId);
            throw new StaffNotFoundException("Staff not found with id: " + staffPrimeId);
        }

        staffRepository.deleteById(staffPrimeId);
        logger.info("Staff deleted successfully for staffPrimeId={}", staffPrimeId);
    }

    @Override
    @Transactional
    public void softDeleteStaff(Long staffPrimeId) {
        logger.info("Soft-deleting staff with staffPrimeId={}", staffPrimeId);

        StaffEntity entity = staffRepository.findById(staffPrimeId)
                .orElseThrow(() -> {
                    logger.warn("Delete failed — staff not found for staffPrimeId={}", staffPrimeId);
                    return new StaffNotFoundException("Staff not found with id: " + staffPrimeId);
                });

        entity.setStaffActive(false);
        staffRepository.save(entity);

        int unassignedCount = leadRepository.unassignStaffFromAllLeads(staffPrimeId);
        logger.info("Staff soft-deleted for staffPrimeId={}, auto-unassigned {} lead(s)", staffPrimeId, unassignedCount);
    }


    @Override
    public void changePassword(Long staffPrimeId, String oldPassword, String newPassword) {
        logger.info("Changing password for staffPrimeId={}", staffPrimeId);

        StaffEntity entity = staffRepository.findById(staffPrimeId)
                .orElseThrow(() -> {
                    logger.warn("Password change failed — staff not found for staffPrimeId={}", staffPrimeId);
                    return new StaffNotFoundException("Staff not found with id: " + staffPrimeId);
                });

        if (!bCryptPasswordEncoder.matches(oldPassword, entity.getStaffPassword())) {
            logger.warn("Password change failed — old password mismatch for staffPrimeId={}", staffPrimeId);
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        entity.setStaffPassword(bCryptPasswordEncoder.encode(newPassword));
        staffRepository.save(entity);
        logger.info("Password changed successfully for staffPrimeId={}", staffPrimeId);
    }

    @Override
    public List<StaffDropdownDto> getStaffDropdownList() {
        logger.debug("Fetching staff dropdown list");

        List<StaffDropdownDto> result = staffRepository.findByStaffActiveTrue()
                .stream()
                .map(s -> new StaffDropdownDto(
                        s.getStaffPrimeId(),
                        s.getStaffStrId(),
                        s.getStaffFirstName(),
                        s.getStaffLastName(),
                        s.getStaffRole(),
                        s.getStaffEmail()
                ))

                .collect(Collectors.toList());

        logger.debug("Staff dropdown list size={}", result.size());
        return result;
    }

    @Override
    public StaffStatsDto getStaffStats() {
        logger.debug("Computing staff dashboard stats");

        long totalStaff = staffRepository.countByStaffActiveTrue();
        long totalLeads = leadRepository.countByDeletedLeadFalseAndLeadConvertedFalse();
        long pendingFollowups = leadRepository
                .countByDeletedLeadFalseAndLeadConvertedFalseAndFollowUpDateLessThanEqual(java.time.LocalDate.now());

        logger.debug("Stats — totalStaff={}, totalLeads={}, pendingFollowups={}", totalStaff, totalLeads, pendingFollowups);
        return new StaffStatsDto(totalStaff, totalLeads, pendingFollowups);
    }


    private int resolvePageSize(int requestedSize) {
        if (requestedSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(requestedSize, MAX_PAGE_SIZE);
    }

    private String generateStaffStrId() {
        return "STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private StaffRegisterdResponse toResponse(StaffEntity entity) {
        return new StaffRegisterdResponse(
                entity.getStaffPrimeId(),
                entity.getStaffStrId(),
                entity.getStaffFirstName(),
                entity.getStaffMiddleName(),
                entity.getStaffLastName(),
                entity.getStaffMobile(),
                entity.getStaffEmail(),
                entity.getStaffWorkingEmail(),
                entity.getStaffAddress(),
                entity.getStaffSalary(),
                entity.getJoiningDate(),
                entity.getStaffRole(),
                entity.getStaffDepartment()
        );
    }
}