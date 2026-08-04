package com.crm.util;

import com.crm.dto.response.BulkUploadResult;
import com.crm.entity.LeadEntity;
import com.crm.enum_status.LeadField;
import com.crm.repository.LeadRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LeadRowExtractor{

    @Autowired
    private LeadRepository leadRepository;

    public LeadEntity extractRow(Row row, Map<Integer, LeadField> mapping, int rowNum, List<BulkUploadResult.RowError> errors) {
        LeadEntity lead = new LeadEntity();
        for (Map.Entry<Integer, LeadField> entry : mapping.entrySet()) {
            String value = getCellValueAsString(row.getCell(entry.getKey()));
            entry.getValue().apply(lead, value);
        }
        if (isBlank(lead.getFirstName()) && isBlank(lead.getLastName())) {
            errors.add(new BulkUploadResult.RowError(rowNum, "Missing name"));
            return null;
        }
        if (isBlank(lead.getPhone()) && isBlank(lead.getEmail())) {
            errors.add(new BulkUploadResult.RowError(rowNum, "Missing phone and email"));
            return null;
        }
        if (!isBlank(lead.getPhone()) && leadRepository.existsByPhoneAndDeletedLeadFalse(lead.getPhone())) {
            errors.add(new BulkUploadResult.RowError(rowNum, "Duplicate phone"));
            return null;
        }
        if (!isBlank(lead.getEmail()) && leadRepository.existsByEmailAndDeletedLeadFalse(lead.getEmail())) {
            errors.add(new BulkUploadResult.RowError(rowNum, "Duplicate email"));
            return null;
        }
        return lead;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                double d = cell.getNumericCellValue();
                // avoid scientific notation on phone numbers
                return (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return null;
        }
    }
    private boolean isBlank(String s){ return s == null || s.isBlank(); }
}