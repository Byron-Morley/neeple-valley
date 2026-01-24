package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.managers.IColonyManager;
import com.liquidpixel.main.interfaces.services.IItemService;

public class ColonyManager implements IColonyManager {

    Engine engine;
    Entity population;
    Entity housing;
    Entity jobs;

    public ColonyManager(IItemService itemService) {
//        engine = GameResources.get().getEngine();
//        population = itemService.getItem("icons/population", 1).build();
//        housing = itemService.getItem("icons/housing", 1).build();
//        jobs = itemService.getItem("icons/jobs", 0).build();
//
//        engine.addEntity(population);
//        engine.addEntity(housing);
//        engine.addEntity(jobs);
    }
}
