package com.liquidpixel.main.components.workshop;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.agent.AgentJobComponent;
import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobComponent implements Component {

    int jobCapacity;
    List<Entity> workers;

    public JobComponent(int jobCapacity) {
        this.jobCapacity = jobCapacity;
        this.workers = new ArrayList<>();
    }

    public int getJobCapacity() {
        return jobCapacity;
    }

    public void setJobCapacity(int jobCapacity) {
        this.jobCapacity = jobCapacity;
    }

    public List<Entity> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Entity> workers) {
        this.workers = workers;
    }

    public int availableJobCount() {
        return jobCapacity - workers.size();
    }

    public boolean hasJobAvailable() {
        return workers.size() < jobCapacity;
    }

    public void addWorker(Entity worker, Entity job) {
        workers.add(worker);
        worker.add(new AgentJobComponent(job));
    }
}
