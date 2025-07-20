package com.thmanyah.services.cms.model;

/**
 * Enhanced status enum for import jobs with background processing
 */
public enum ImportJobStatus {
    QUEUED("Job queued for processing"),
    PROCESSING("Job is being processed"),
    FETCHING("Fetching data from external provider"),
    SAVING("Saving data to database"),
    COMPLETED("Job completed successfully"),
    FAILED("Job failed"),
    CANCELLED("Job was cancelled"),
    RETRYING("Job is being retried after failure");

    private final String description;

    ImportJobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean isActive() {
        return this == QUEUED || this == PROCESSING || this == FETCHING || 
               this == SAVING || this == RETRYING;
    }
}
