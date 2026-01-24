package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.render.ShaderComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.model.render.OutlineShader;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;

public class SelectionScenario extends Scenario implements IScenario {

    public SelectionScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                             ISettlementService settlementService, IAgentService agentService, IItemService itemService, IStorageService storageService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService, storageService);
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("initial", "Selection")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        // Clear current state first
        reset();

        switch (stateId) {
            case "initial":
                start();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }


    @Override
    public void start() {
        Entity person = agentService.spawnAgent(new GridPoint2(12, 12), "population");
        trackEntity(person);


        Entity person2 = agentService.spawnAgent(new GridPoint2(15, 12), "population");
        trackEntity(person2);


        Entity one = itemService.getItem("tiled/fence", 1).build();
        Entity two = itemService.getItem("tiled/fence", 1).build();
        itemService.spawnItem(one, new GridPoint2(10, 10));
        itemService.spawnItem(two, new GridPoint2(10, 11));


//        selectionService.setSelectedEntity(person);

//        // Load the outline shader
//        String vertexShaderPath = "assets/shaders/selection/default.vert";
//        String fragmentShaderPath = "assets/shaders/selection/outline.frag";
//
//        ShaderProgram outlineShaderProgram = new ShaderProgram(
//            Gdx.files.internal(vertexShaderPath),
//            Gdx.files.internal(fragmentShaderPath)
//        );
//
//        if (!outlineShaderProgram.isCompiled()) {
//            Gdx.app.error("Shader", "Outline shader compilation failed: " + outlineShaderProgram.getLog());
//        }
//
//        ShaderComponent shaderComponent = getShaderComponent(outlineShaderProgram);
//        person.add(shaderComponent);


//        Entity building = itemService.getItem("ui/circle").build();
//        itemService.spawnItem(building, new GridPoint2(16, 16));


        // Spawn a building
//        Entity building = itemService.getItem("providers/stone-cutter").build();
//        itemService.spawnItem(building, new GridPoint2(15, 15));
//        trackEntity(building);


    }

    private static ShaderComponent getShaderComponent(ShaderProgram outlineShaderProgram) {
        OutlineShader outlineShader = new OutlineShader(outlineShaderProgram);

        // Configure the outline properties if needed
        outlineShader.setOutlineWidth(2.0f);
//        outlineShader.setOutlineColor(1f, 1f, 0f, 1f); // Yellow outline

        // Create and add the shader component to the entity
        ShaderComponent shaderComponent = new ShaderComponent();
        shaderComponent.addShader(outlineShader);
        return shaderComponent;
    }

    @Override
    public void reset() {

    }
}
