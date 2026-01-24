package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.colony.HouseComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.utils.Mappers;

import java.util.List;

public class AutoAssignHouseSystem extends IntervalIteratingSystem {
    public AutoAssignHouseSystem() {
        super(Family.all(SettlementComponent.class).get(), 5f);
    }

    @Override
    protected void processEntity(Entity entity) {
        SettlementComponent settlement = Mappers.settlement.get(entity);
        List<Entity> houses = getAvailableHouses(settlement);
        List<Entity> workers = getHomelessWorkers(settlement);

        if (houses.isEmpty() || workers.isEmpty()) {
            return;
        }

        for (Entity worker : workers) {
            for (Entity house : houses) {
                HouseComponent houseComponent = Mappers.house.get(house);
                if (houseComponent.availableCapacity() > 0) {
                    houseComponent.addResident(worker, house);
                }
            }
        }
    }

    private List<Entity> getHomelessWorkers(SettlementComponent settlement) {
        return settlement.getPopulation()
            .stream()
            .filter(Mappers.worker::has)
            .filter(worker -> !Mappers.resident.has(worker))
            .toList();
    }

    private List<Entity> getAvailableHouses(SettlementComponent settlement) {
        return settlement.getAssets()
            .stream()
            .filter(Mappers.house::has)
            .filter(house -> Mappers.house.get(house).availableCapacity() > 0)
            .toList();
    }

}
