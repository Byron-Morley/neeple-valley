package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class AutoAssignJobSystem extends IntervalIteratingSystem {
    public AutoAssignJobSystem() {
        super(Family.all(SettlementComponent.class).get(), 1f);
    }

    @Override
    protected void processEntity(Entity entity) {
        SettlementComponent settlement = Mappers.settlement.get(entity);
        List<Entity> workshops = getAvailableJobs(settlement);
        List<Entity> workers = getWorkers(settlement);

        if (workshops.isEmpty() || workers.isEmpty()) {
            return;
        }

        for (Entity worker : workers) {
            for (Entity workshop : workshops) {
                JobComponent jobComponent = Mappers.jobs.get(workshop);
                if (jobComponent.hasJobAvailable()) {
                    jobComponent.addWorker(worker, workshop);
                    Gdx.app.log("AutoAssignJobSystem", "Assigning job to " + worker.toString());
                    break;
                }
            }
        }
    }

    private List<Entity> getWorkers(SettlementComponent settlement) {
        return settlement.getPopulation()
            .stream()
            .filter(Mappers.worker::has)
            .filter(worker -> !Mappers.agentjob.has(worker))
            .toList();
    }

    private List<Entity> getAvailableJobs(SettlementComponent settlement) {
        return settlement.getAssets()
            .stream()
            .filter(Mappers.jobs::has)
            .filter(workshop -> Mappers.jobs.get(workshop).hasJobAvailable())
            .toList();
    }

}
