package com.thmanyah.services.cms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Message sent to RabbitMQ for background import job processing
 */
@Data
@Builder
public class ImportJobMessage {
    
    private UUID jobId;
    private String provider;
    private String userEmail;
    private String topic;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private boolean skipDuplicates;
    private int retryCount;
    private int maxRetries;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;
    
    // Additional metadata
    private Integer batchSize;
    private Integer priority; // 1 = high, 5 = low
    
    public static ImportJobMessage fromRequest(UUID jobId, String provider, 
                                             ImportRequest request, String userEmail) {
        return ImportJobMessage.builder()
                .jobId(jobId)
                .provider(provider)
                .userEmail(userEmail)
                .topic(request.topic())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .skipDuplicates(request.skipDuplicates())
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .scheduledAt(LocalDateTime.now())
                .batchSize(100)
                .priority(3) // Normal priority
                .build();
    }
    
    public ImportJobMessage withRetry() {
        return ImportJobMessage.builder()
                .jobId(this.jobId)
                .provider(this.provider)
                .userEmail(this.userEmail)
                .topic(this.topic)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .skipDuplicates(this.skipDuplicates)
                .retryCount(this.retryCount + 1)
                .maxRetries(this.maxRetries)
                .createdAt(this.createdAt)
                .scheduledAt(LocalDateTime.now().plusMinutes(this.retryCount * 2)) // Exponential backoff
                .batchSize(this.batchSize)
                .priority(this.priority)
                .build();
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
}
