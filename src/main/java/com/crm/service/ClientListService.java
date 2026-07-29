package com.crm.service;

import com.crm.dto.request.AssignClientRequest;
import com.crm.dto.request.ClientCreateRequest;
import com.crm.dto.request.ConvertLeadRequest;
import com.crm.dto.response.ClientCreatedResponse;
import com.crm.dto.stats.ClientStatsResponse;
import org.springframework.data.domain.Page;

public interface ClientListService {

    ClientCreatedResponse createClient(ClientCreateRequest request);

    ClientCreatedResponse convertLeadToClient(Long leadPrimeId, ConvertLeadRequest request);

    Page<ClientCreatedResponse> getAllClients(int page, int size);

    ClientCreatedResponse getClientById(Long clientPrimeId);

    ClientCreatedResponse updateClient(Long clientPrimeId, ClientCreateRequest request);

    void deleteClient(Long clientPrimeId);

    ClientStatsResponse getClientStats();

    ClientCreatedResponse assignClient(Long clientPrimeId, AssignClientRequest request);

}