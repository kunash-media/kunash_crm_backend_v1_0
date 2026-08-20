package com.crm.component;

import com.crm.entity.ReasonBucketEntity;
import com.crm.repository.ReasonBucketRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReasonBucketSeeder implements CommandLineRunner {

    private final ReasonBucketRepository repo;

    public ReasonBucketSeeder(ReasonBucketRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        List<ReasonBucketEntity> defaults = List.of(
                bucket("Pricing", "LOST", "price,expensive,budget,cost,costly,affordable"),
                bucket("Timing", "LOST", "not now,later,next quarter,timing,busy,postpone"),
                bucket("Competitor", "LOST", "competitor,already using,went with,other vendor"),
                bucket("No Response", "LOST", "no reply,unreachable,ghosted,not responding,no answer"),
                bucket("Payment Terms", "LOST", "net 30,net 60,credit period,payment terms,advance"),
                bucket("Feature Gap", "LOST", "feature,missing,doesn't support,not compatible"),
                bucket("Demo Impact", "WON", "demo,walkthrough,presentation"),
                bucket("Discount Offered", "WON", "discount,offer,deal,negotiat"),
                bucket("Founder Call", "WON", "founder,ceo call,leadership call"),
                bucket("Referral Trust", "WON", "referral,recommended,known contact")
        );
        repo.saveAll(defaults);
    }

    private ReasonBucketEntity bucket(String name, String applicableTo, String keywords) {
        ReasonBucketEntity b = new ReasonBucketEntity();
        b.setBucketName(name);
        b.setApplicableTo(applicableTo);
        b.setKeywords(keywords);
        b.setActive(true);
        return b;
    }
}