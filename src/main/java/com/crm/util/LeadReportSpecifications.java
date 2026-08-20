package com.crm.util;

import com.crm.entity.LeadEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeadReportSpecifications {

    // outcome: "lost" -> filters leadOutcome; "won" -> filters leadConverted=true
    public static Specification<LeadEntity> build(
            String outcome, String source, Long staffPrimeId,
            String requirementCategory, LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deletedLead")));

            if ("lost".equalsIgnoreCase(outcome)) {
                predicates.add(cb.equal(root.get("leadOutcome"), "lost"));
            } else if ("won".equalsIgnoreCase(outcome)) {
                predicates.add(cb.isTrue(root.get("leadConverted")));
            }

            if (source != null && !source.isBlank())
                predicates.add(cb.equal(cb.lower(root.get("source")), source.toLowerCase()));

            if (staffPrimeId != null)
                predicates.add(cb.equal(root.get("assignedStaff").get("staffPrimeId"), staffPrimeId));

            if (requirementCategory != null && !requirementCategory.isBlank())
                predicates.add(cb.equal(cb.lower(root.get("requirementCategory")), requirementCategory.toLowerCase()));

            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));

            if (toDate != null)
                predicates.add(cb.lessThan(root.get("createdAt"), toDate.plusDays(1).atStartOfDay()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}