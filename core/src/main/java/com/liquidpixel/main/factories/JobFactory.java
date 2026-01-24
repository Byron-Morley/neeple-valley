package com.liquidpixel.main.factories;


import com.liquidpixel.main.dto.ai.JobDto;

import java.util.Map;

public class JobFactory {
    private Map<String, JobDto> jobs;

    public JobFactory() {
        this.jobs = ModelFactory.getJobs();
    }

    public JobDto get(String id) {
        return jobs.get(id);
    }

    public Map<String, JobDto> getJobs() {
        return jobs;
    }
}
