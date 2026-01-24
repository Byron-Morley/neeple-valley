package com.liquidpixel.main.listeners.spawnMenu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.actions.SpawnAgentMenuAction;
import com.liquidpixel.item.components.SpectralPickupComponent;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.ISelectionService;

public class SpawnAgentClickListener extends ClickListener {

    ISelectionService selectionService;
    ICameraService cameraService;
    IAgentService agentService;
    String key;

    public SpawnAgentClickListener(String key, ISelectionService selectionService, ICameraService cameraService, IAgentService agentService) {
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.agentService = agentService;
        this.key = key;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        selectionService.clearLeftClickActions();

        Entity ghost = agentService.getGhostAgent(key);
        ghost.add(new SpectralPickupComponent());
        selectionService.addLeftClickAction(new SpawnAgentMenuAction(selectionService, cameraService, agentService, key, ghost));
    }
}
