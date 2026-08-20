package com.crm.service.serviceImple;


import com.crm.entity.ReasonBucketEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextBucketMatcherService {

    // Returns bucket names matched in the given free text (case-insensitive, no LLM/API call)
    public List<String> matchBuckets(String text, List<ReasonBucketEntity> buckets) {
        if (text == null || text.isBlank()) return List.of();
        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");

        List<String> matched = new ArrayList<>();
        for (ReasonBucketEntity bucket : buckets) {
            if (bucket.getKeywords() == null) continue;
            for (String kw : bucket.getKeywords().split(",")) {
                String k = kw.trim().toLowerCase();
                if (!k.isEmpty() && normalized.contains(k)) {
                    matched.add(bucket.getBucketName());
                    break; // one match per bucket is enough
                }
            }
        }
        return matched.isEmpty() ? List.of("Unclassified") : matched;
    }
}
