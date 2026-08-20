package com.crm.enum_status;


import java.util.List;
import java.util.function.BiConsumer;
import com.crm.entity.LeadEntity;

public enum LeadField {
    FIRST_NAME(List.of("first name","fname","given name"), LeadEntity::setFirstName),
    LAST_NAME(List.of("last name","lname","surname"), LeadEntity::setLastName),
    EMAIL(List.of("email","e-mail","email address"), LeadEntity::setEmail),
    PHONE(List.of("phone","mobile","contact no","mobile number","whatsapp","contact number"), LeadEntity::setPhone),
    COMPANY(List.of("company","company name","organisation","organization"), LeadEntity::setCompany),
    STATUS(List.of("status","lead status"), LeadEntity::setStatus),
    PRIORITY(List.of("priority"), LeadEntity::setPriority),
    SOURCE(List.of("source","lead source"), LeadEntity::setSource),
    REQUIREMENT_CATEGORY(List.of("requirement","requirement category","category"), LeadEntity::applyRequirementCategoriesFromRaw),    TAGS(List.of("tags","tag"), LeadEntity::setTags),
    NOTES(List.of("notes","remark","remarks"), LeadEntity::setNotes);

    public final List<String> synonyms;
    final BiConsumer<LeadEntity, String> setter;

    LeadField(List<String> synonyms, BiConsumer<LeadEntity, String> setter) {
        this.synonyms = synonyms;
        this.setter = setter;
    }

    public void apply(LeadEntity entity, String value) {
        if (value != null && !value.isBlank()) setter.accept(entity, value.trim());
    }
}