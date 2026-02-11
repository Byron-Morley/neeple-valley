package com.liquidpixel.main.scenarios;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.render.ShaderComponent;
import com.liquidpixel.main.interfaces.IScenario;
import com.liquidpixel.main.interfaces.IShader;
import com.liquidpixel.main.interfaces.IWorldMap;
import com.liquidpixel.main.interfaces.ScenarioState;
import com.liquidpixel.main.interfaces.services.*;
import com.liquidpixel.main.model.render.ColorShader;
import com.liquidpixel.pathfinding.api.IMapService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class AgentColorScenario extends Scenario implements IScenario {

    public AgentColorScenario(IMapService mapService, IWorldMap worldMap, ISelectionService selectionService,
                              ISettlementService settlementService, IAgentService agentService, IItemService itemService) {
        super(mapService, worldMap, selectionService, settlementService, agentService, itemService);
    }

    @Override
    public void start() {
        Entity person = agentService.spawnAgent(new GridPoint2(12, 12), "population");
        trackEntity(person);

        ShaderProgram colorRampShader = new ShaderProgram(
            Gdx.files.internal("assets/shaders/color_ramps/color_ramp.vert"),
            Gdx.files.internal("assets/shaders/color_ramps/color_ramp_fixed.frag")
        );

        Random random = new Random();
        random.nextInt(63);

        Texture hairRampA = new Texture(Gdx.files.internal("assets/raw/sprites/ramps/hair_ramp_"+random.nextInt(57)+".png"));
        hairRampA.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        IShader shader = new ColorShader(colorRampShader, hairRampA);

        ShaderComponent shaderComponent = new ShaderComponent();

        shaderComponent.addShader("13hair/fbas_13hair_spiky1_00", shader);
        person.add(shaderComponent);

        Entity person1 = agentService.spawnAgent(new GridPoint2(14, 12), "population");
        trackEntity(person1);

    }

    @Override
    public void reset() {
        clearAllSpawnedEntities();
    }

    @Override
    public List<ScenarioState> getAvailableStates() {
        return Arrays.asList(
            new ScenarioState("initial", "Initial State")
        );
    }

    @Override
    public boolean loadState(String stateId) {
        reset();

        switch (stateId) {
            case "initial":
                loadInitialState();
                return true;
            default:
                System.out.println("Unknown state: " + stateId);
                return false;
        }
    }

    private void loadInitialState() {
        start();
    }
}
