package com.crm.service.serviceImple;

import com.crm.dto.request.AssignClientRequest;
import com.crm.dto.request.ClientCreateRequest;
import com.crm.dto.request.ConvertLeadRequest;
import com.crm.dto.response.ClientCreatedResponse;
import com.crm.dto.stats.ClientStatsResponse;
import com.crm.dto.stats.PendingPaymentAlertResponse;
import com.crm.entity.ClientListEntity;
import com.crm.entity.LeadEntity;
import com.crm.repository.ClientListRepository;
import com.crm.repository.LeadRepository;
import com.crm.service.ClientListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ClientListServiceImple implements ClientListService {

    private static final Logger log = LoggerFactory.getLogger(ClientListServiceImple.class);

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;

    private final ClientListRepository clientListRepository;
    private final LeadRepository leadRepository;

    public ClientListServiceImple(ClientListRepository clientListRepository, LeadRepository leadRepository) {
        this.clientListRepository = clientListRepository;
        this.leadRepository = leadRepository;
    }

    @Override
    public ClientStatsResponse getClientStats() {
        log.info("Fetching client stat counts");
        long total = clientListRepository.countByDeletedClientFalse();
        long staticCount = clientListRepository.countByTypeAndDeletedClientFalse("static");
        long dynamicCount = clientListRepository.countByTypeAndDeletedClientFalse("dynamic");
        long totalWebsites = total; // matches current frontend: total clients == total websites

        return new ClientStatsResponse(total, totalWebsites, staticCount, dynamicCount);
    }


    @Override
    @Transactional
    public ClientCreatedResponse createClient(ClientCreateRequest request) {
        log.info("Creating client manually — firstName={}, contact={}", request.getFirstName(), request.getContact());

        ClientListEntity entity = new ClientListEntity();
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setContact(request.getContact());
        entity.setEmail(request.getEmail());
        entity.setService(request.getService());
        entity.setProject(request.getProject());
        entity.setSource(request.getSource());
        entity.setType(request.getType());
        entity.setTotalAmount(request.getTotalAmount());
        entity.setAdvanceAmount(request.getAdvanceAmount());
        entity.setRemainAmount(computeRemain(request.getTotalAmount(), request.getAdvanceAmount()));
        entity.setPendingAmount(request.getPendingAmount());
        entity.setAssignTo(request.getAssignTo());
        entity.setRemainPayFollowUpDate(request.getRemainPayFollowUpDate());

        ClientListEntity saved = clientListRepository.save(entity);
        log.info("Client created — clientStrId={}", saved.getClientStrId());
        return ClientCreatedResponse.from(saved);
    }

    @Override
    @Transactional
    public ClientCreatedResponse convertLeadToClient(Long leadPrimeId, ConvertLeadRequest request) {
        log.info("Converting lead {} to client — totalAmount={}, advanceAmount={}",
                leadPrimeId, request.getTotalAmount(), request.getAdvanceAmount());

        LeadEntity lead = leadRepository.findById(leadPrimeId)
                .filter(l -> !Boolean.TRUE.equals(l.getDeletedLead()))
                .orElseThrow(() -> new NoSuchElementException("Lead not found with id: " + leadPrimeId));

        if (Boolean.TRUE.equals(lead.getLeadConverted())) {
            log.warn("Lead {} is already converted — rejecting duplicate conversion", leadPrimeId);
            throw new IllegalStateException("Lead is already converted to a client");
        }
        if (clientListRepository.existsBySourceLeadIdAndDeletedClientFalse(leadPrimeId)) {
            log.warn("A client already exists for lead {} — rejecting duplicate conversion", leadPrimeId);
            throw new IllegalStateException("A client already exists for this lead");
        }

        ClientListEntity entity = new ClientListEntity();

        entity.setFirstName(lead.getFirstName());
        entity.setLastName(lead.getLastName());
        entity.setContact(lead.getPhone());
        entity.setEmail(lead.getEmail());
        entity.setService(lead.getRequirementCategory());
        entity.setProject(lead.getCompany());
        entity.setSource(lead.getSource());
        entity.setSourceLeadId(lead.getLeadPrimeId());

        Double total = request.getTotalAmount();
        Double advance = request.getAdvanceAmount();
        entity.setTotalAmount(total);
        entity.setAdvanceAmount(advance);
        entity.setRemainAmount(computeRemain(total, advance));
        entity.setPendingAmount(request.getPendingAmount() != null ? request.getPendingAmount() : 0.0);
        entity.setRemainPayFollowUpDate(request.getRemainPayFollowUpDate());

        ClientListEntity saved = clientListRepository.save(entity);

        lead.setLeadConverted(true);
        leadRepository.save(lead);

        log.info("Lead {} converted successfully → client {}", leadPrimeId, saved.getClientStrId());
        return ClientCreatedResponse.from(saved);
    }

    @Override
    public Page<ClientCreatedResponse> getAllClients(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        log.info("Fetching clients — page={}, size={}", safePage, safeSize);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return clientListRepository.findByDeletedClientFalse(pageable).map(ClientCreatedResponse::from);
    }

    @Override
    public ClientCreatedResponse getClientById(Long clientPrimeId) {
        log.info("Fetching client id={}", clientPrimeId);
        ClientListEntity entity = clientListRepository.findByClientPrimeIdAndDeletedClientFalse(clientPrimeId)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + clientPrimeId));
        return ClientCreatedResponse.from(entity);
    }

    @Override
    @Transactional
    public ClientCreatedResponse updateClient(Long clientPrimeId, ClientCreateRequest request) {
        log.info("Updating client id={}", clientPrimeId);

        ClientListEntity entity = clientListRepository.findByClientPrimeIdAndDeletedClientFalse(clientPrimeId)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + clientPrimeId));

        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setContact(request.getContact());
        entity.setEmail(request.getEmail());
        entity.setService(request.getService());
        entity.setProject(request.getProject());
        entity.setSource(request.getSource());
        entity.setType(request.getType());
        entity.setTotalAmount(request.getTotalAmount());
        entity.setAdvanceAmount(request.getAdvanceAmount());
        entity.setRemainAmount(
                request.getRemainAmount() != null
                        ? request.getRemainAmount()
                        : computeRemain(request.getTotalAmount(), request.getAdvanceAmount())
        );
        entity.setPendingAmount(request.getPendingAmount());
        entity.setAssignTo(request.getAssignTo());
        entity.setRemainPayFollowUpDate(request.getRemainPayFollowUpDate());

        ClientListEntity saved = clientListRepository.save(entity);
        log.info("Client id={} updated successfully", clientPrimeId);
        return ClientCreatedResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteClient(Long clientPrimeId) {
        log.info("Soft-deleting client id={}", clientPrimeId);

        ClientListEntity entity = clientListRepository.findByClientPrimeIdAndDeletedClientFalse(clientPrimeId)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + clientPrimeId));

        entity.setDeletedClient(true);
        clientListRepository.save(entity);
        log.info("Client id={} soft-deleted", clientPrimeId);
    }

    private Double computeRemain(Double total, Double advance) {
        if (total == null || advance == null) return null;
        return total - advance;
    }

    @Override
    @Transactional
    public ClientCreatedResponse assignClient(Long clientPrimeId, AssignClientRequest request) {
        log.info("Assigning client id={} to '{}'", clientPrimeId, request.getAssignTo());

        ClientListEntity entity = clientListRepository.findByClientPrimeIdAndDeletedClientFalse(clientPrimeId)
                .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + clientPrimeId));

        entity.setAssignTo(request.getAssignTo());
        ClientListEntity saved = clientListRepository.save(entity);

        log.info("Client id={} assigned to '{}'", clientPrimeId, saved.getAssignTo());
        return ClientCreatedResponse.from(saved);
    }

    @Override
    public List<PendingPaymentAlertResponse> getPendingPaymentAlerts() {
        log.info("Fetching pending payment alerts");
        return clientListRepository
                .findByPendingAmountGreaterThanAndDeletedClientFalseOrderByPendingAmountDesc(0.0)
                .stream()
                .map(PendingPaymentAlertResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public long getPendingPaymentAlertCount() {
        return clientListRepository.countByPendingAmountGreaterThanAndDeletedClientFalse(0.0);
    }
}