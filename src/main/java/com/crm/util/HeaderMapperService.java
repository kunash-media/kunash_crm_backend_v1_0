package com.crm.util;

import com.crm.enum_status.LeadField;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HeaderMapperService {

    public Map<Integer, LeadField> mapHeaders(java.util.List<String> rawHeaders) {
        Map<Integer, LeadField> colIndexToField = new HashMap<>();
        for (int i = 0; i < rawHeaders.size(); i++) {
            String normalized = normalize(rawHeaders.get(i));
            LeadField matched = matchField(normalized);
            if (matched != null) {
                colIndexToField.put(i, matched);
            }
        }
        return colIndexToField;
    }

    private String normalize(String header) {
        if (header == null) return "";
        return header.trim().toLowerCase()
                .replaceAll("[^a-z0-9]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private LeadField matchField(String normalizedHeader) {
        if (normalizedHeader.isEmpty()) return null;
        for (LeadField field : LeadField.values()) {
            for (String syn : field.synonyms) {
                String normSyn = normalize(syn);
                if (normSyn.equals(normalizedHeader)) return field;
            }
        }
        // fuzzy fallback pass (separate loop so exact matches always win first)
        for (LeadField field : LeadField.values()) {
            for (String syn : field.synonyms) {
                String normSyn = normalize(syn);
                if (LevenshteinDistance.getDefaultInstance().apply(normSyn, normalizedHeader) <= 2) {
                    return field;
                }
            }
        }
        return null;
    }
}