package com.crm.util;

import com.crm.repository.LeadRepository;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// In-memory Guava Bloom filter over active lead phone numbers.
// Purpose: avoid a DB round-trip on every phone-field blur in the UI.
// mightExist()==false is a guaranteed negative (skip DB entirely).
// mightExist()==true can be a false positive, so callers must still
// confirm against the DB before treating it as a real duplicate.
@Component
public class LeadPhoneBloomFilterService {

    private static final int EXPECTED_INSERTIONS = 100_000;
    private static final double FALSE_POSITIVE_RATE = 0.01;

    private final LeadRepository leadRepository;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile BloomFilter<CharSequence> bloomFilter;

    @Autowired
    public LeadPhoneBloomFilterService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @PostConstruct
    public void init() {
        rebuild();
    }

    public void rebuild() {
        lock.lock();
        try {
            BloomFilter<CharSequence> fresh = BloomFilter.create(
                    Funnels.stringFunnel(StandardCharsets.UTF_8),
                    EXPECTED_INSERTIONS,
                    FALSE_POSITIVE_RATE
            );
            List<String> phones = leadRepository.findAllActivePhones();
            for (String phone : phones) {
                fresh.put(normalize(phone));
            }
            this.bloomFilter = fresh;
        } finally {
            lock.unlock();
        }
    }

    public void add(String phone) {
        if (phone == null || bloomFilter == null) return;
        bloomFilter.put(normalize(phone));
    }

    public boolean mightExist(String phone) {
        if (phone == null || bloomFilter == null) return false;
        return bloomFilter.mightContain(normalize(phone));
    }

    private String normalize(String phone) {
        return phone.replaceAll("\\D", "");
    }
}