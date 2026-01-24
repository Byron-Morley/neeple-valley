package com.liquidpixel.main.systems.colony;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.liquidpixel.main.components.ColonyComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.ISettlementService;

public class ColonySystem extends IntervalIteratingSystem {
    static final float INTERVAL_IN_SECONDS = 1f;
    Engine engine;
    ISettlementService settlementService;

    public ColonySystem(ISettlementService settlementService) {
        super(Family.all(ColonyComponent.class).get(), INTERVAL_IN_SECONDS);
        engine = GameResources.get().getEngine();
        this.settlementService = settlementService;
    }

    @Override
    protected void processEntity(Entity entity) {

        //TODO: these need to be redone so they pickup the current settlement
//        ItemComponent itemComponent = Mappers.item.get(entity);
//
//        if (itemComponent.getName().equals("icons/population")) {
//            Entity settlement = settlementService.getSelectedSettlement();
//            SettlementComponent settlementComponent = Mappers.settlement.get(settlement);
//            itemComponent.setQuantity(settlementComponent.getPopulation().size());
//        }
//        if (itemComponent.getName().equals("icons/jobs")) {
//            int count = engine.getEntitiesFor(Family.all(AgentJobComponent.class).get()).size();
//            itemComponent.setQuantity(count);
//        }
//        if (itemComponent.getName().equals("icons/housing")) {
//            int count = engine.getEntitiesFor(Family.all(HouseComponent.class).get()).size();
//            itemComponent.setQuantity(count);
//        }
//        if (itemComponent.getName().equals("icons/food")) {
//        }
    }
}
