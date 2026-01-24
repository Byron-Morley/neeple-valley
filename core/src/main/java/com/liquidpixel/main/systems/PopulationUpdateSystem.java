package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.model.person.Meter;
import com.liquidpixel.main.utils.Mappers;


public class PopulationUpdateSystem extends IteratingSystem {

    public PopulationUpdateSystem() {
        super(Family.all(WorkerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        WorkerComponent worker = Mappers.worker.get(entity);
        Meter foodMeter = worker.getFood();

        foodMeter.tick(delta);

        if (foodMeter.isThresholdReached()) {
            // The threshold has been reached, trigger hunger effects
            System.out.println("Character is hungry!");

            // make an order to find food
            // needs to be in the warehouses
            // After handling the threshold event



            foodMeter.acknowledgeThreshold();
        }


        Meter energy = worker.getEnergy();
        energy.tick(delta);

        if (energy.isThresholdReached()) {
            System.out.println("Character is running out of energy!");
            energy.acknowledgeThreshold();
        }

    }
}
