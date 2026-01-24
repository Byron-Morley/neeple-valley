package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.ResourceComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.ui.view.selection.SelectionUI;
import com.liquidpixel.main.utils.Mappers;

/**
 * This just renders the quantity on the resource
 */
public class ResourceSelectionRenderSystem extends IteratingSystem {

    ISelectionService selectionService;
    ICameraService cameraService;
    OrthographicCamera camera;

    public ResourceSelectionRenderSystem(ISelectionService selectionService, ICameraService cameraService) {
        super(Family.all(ResourceComponent.class, ItemComponent.class, RenderComponent.class, PositionComponent.class).get());
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.camera = GameResources.get().getCamera();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ResourceComponent resourceComponent = Mappers.resource.get(entity);

        if (resourceComponent.getSelectionUI() == null) {

            SelectionUI selectionUI = new SelectionUI(selectionService, cameraService);
            resourceComponent.setSelectionUI(selectionUI);
            GameResources.get().getStage().addActor(selectionUI.get());

        } else {

            ItemComponent itemComponent = Mappers.item.get(entity);
            RenderComponent renderComponent = Mappers.render.get(entity);
            PositionComponent positionComponent = Mappers.position.get(entity);

//            String label = itemComponent.getQuantity() + "/" + itemComponent.getStackSize();
            String label = "" + itemComponent.getQuantity();

            resourceComponent.getSelectionUI().updateUIElements(
                positionComponent.getPosition(),
                renderComponent.getWidth(),
                renderComponent.getHeight(),
                label
            );

            resourceComponent.getSelectionUI().setVisible(camera.zoom <= 0.02f);
        }
    }
}
