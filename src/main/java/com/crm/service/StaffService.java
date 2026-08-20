package com.crm.service;

import com.crm.dto.response.PagedResponse;
import com.crm.dto.request.StaffPatchRequest;
import com.crm.dto.request.StaffRegisterRequest;
import com.crm.dto.response.StaffDropdownDto;
import com.crm.dto.response.StaffRegisterdResponse;
import com.crm.dto.stats.StaffStatsDto;

import java.util.List;

public interface StaffService {

    StaffRegisterdResponse createStaff(StaffRegisterRequest request);

    StaffRegisterdResponse getStaffById(Long staffPrimeId);

    PagedResponse<StaffRegisterdResponse> getAllStaff(int page, int size);

    StaffRegisterdResponse patchStaff(Long staffPrimeId, StaffPatchRequest request);

    void deleteStaff(Long staffPrimeId);

    void changePassword(Long staffPrimeId, String oldPassword, String newPassword);

    List<StaffDropdownDto> getStaffDropdownList();

    StaffStatsDto getStaffStats();

    void softDeleteStaff(Long staffPrimeId);
}