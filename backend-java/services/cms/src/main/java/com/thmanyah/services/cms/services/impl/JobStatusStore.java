package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.model.ImportJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for import job status tracking.
 * In production, this should be replaced with a persistent store like Redis or database.
 */
@Component
@Slf4j
public class JobStatusStore {

    private final Map<UUID, ImportJob> jobs = new ConcurrentHashMap<>();

    /**
     * Save a new job
     */
    public void saveJob(ImportJob job) {
        jobs.put(job.getId(), job);
        log.debug("Saved job: {} with status: {}", job.getId(), job.getStatus());
    }

    /**
     * Update an existing job
     */
    public void updateJob(ImportJob job) {
        jobs.put(job.getId(), job);
        log.debug("Updated job: {} with status: {}", job.getId(), job.getStatus());
    }

    /**
     * Get a job by ID
     */
    public Optional<ImportJob> getJob(UUID jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    /**
     * Get all jobs
     */
    public List<ImportJob> getAllJobs() {
        return new ArrayList<>(jobs.values());
    }

    /**
     * Remove a job
     */
    public void removeJob(UUID jobId) {
        ImportJob removed = jobs.remove(jobId);
        if (removed != null) {
            log.debug("Removed job: {}", jobId);
        }
    }

    /**
     * Get jobs count
     */
    public int getJobsCount() {
        return jobs.size();
    }

    /**
     * Clear all jobs (for testing)
     */
    public void clearAll() {
        jobs.clear();
        log.debug("Cleared all jobs from store");
    }
}
