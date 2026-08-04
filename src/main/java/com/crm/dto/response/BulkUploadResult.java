package com.crm.dto.response;

import java.util.ArrayList;
import java.util.List;

public class BulkUploadResult {
    public int totalRows;
    public int totalUploaded;
    public int totalSkipped;
    public List<RowError> errors = new ArrayList<>();

    public record RowError(int rowNumber, String reason) {}
}
