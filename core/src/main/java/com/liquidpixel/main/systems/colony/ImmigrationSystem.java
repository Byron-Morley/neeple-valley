package com.liquidpixel.main.systems.colony;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.colony.HouseComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.engine.GameClock;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.utils.Mappers;

public class ImmigrationSystem extends IteratingSystem {


    int DAYS_TO_IMMIGRATE = 1;
    IAgentService agentService;

    public ImmigrationSystem(IAgentService agentService) {
        super(Family.all(SettlementComponent.class).get());
        this.agentService = agentService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SettlementComponent settlement = Mappers.settlement.get(entity);

        if (
            GameClock.getDays() % DAYS_TO_IMMIGRATE == 0 &&
                GameClock.getDays() != 0 &&
                !settlement.hasImmigrationBeenCalculated(GameClock.getDays())
        ) {
            settlement.addImmigration(GameClock.getDays());

            int availableHousing = 0;
            int availableJobs = 0;
            for (Entity building : settlement.getAssets()) {
                HouseComponent house = Mappers.house.get(building);
                if (house != null) {
                    availableHousing += house.availableCapacity();
                }

                JobComponent jobs = Mappers.jobs.get(building);
                if (jobs != null) {
                    availableJobs += jobs.availableJobCount();
                }
            }

            int immigration = Math.min(availableHousing, availableJobs);
            System.out.println("Available Housing: " + availableHousing);
            System.out.println("Available Jobs: " + availableJobs);
            addPopulation(settlement, immigration);
        }
    }

    private void addPopulation(SettlementComponent settlement, int immigration) {
        for (int i = 0; i < immigration; i++) {
            Entity person = agentService.getAgent("population");
            settlement.addPopulation(person);
            settlement.addPersonToHouse(person);
            settlement.addPersonToJob(person);
        }
    }
}
