package com.crm.dto.stats;

public class BucketCountDto {
    private String bucketName;
    private long count;

    public BucketCountDto(String key, Long value) {
        this.bucketName = key;
        this.count = value;
    }
    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

