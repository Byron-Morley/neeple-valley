package com.liquidpixel.main.model.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.FollowComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.model.RenderPriority;
import com.liquidpixel.main.utils.Mappers;

public class InGameSelectionUI {

    float startX = 0f;
    float startY = 0f;

    Entity topLeft;
    Entity topRight;
    Entity bottomLeft;
    Entity bottomRight;

    public InGameSelectionUI(IItemService itemService) {
        bottomLeft = itemService.getItem("ui/selection_bottom_left").build();
        bottomRight = itemService.getItem("ui/selection_bottom_right").build();
        topLeft = itemService.getItem("ui/selection_top_left").build();
        topRight = itemService.getItem("ui/selection_top_right").build();

        RenderComponent renderComponent = Mappers.render.get(bottomLeft);
        renderComponent.setPriority(RenderPriority.UI);

        renderComponent = Mappers.render.get(bottomRight);
        renderComponent.setPriority(RenderPriority.UI);

        renderComponent = Mappers.render.get(topLeft);
        renderComponent.setPriority(RenderPriority.UI);

        renderComponent = Mappers.render.get(topRight);
        renderComponent.setPriority(RenderPriority.UI);

        GameResources.get().getEngine().addEntity(bottomLeft);
        GameResources.get().getEngine().addEntity(bottomRight);
        GameResources.get().getEngine().addEntity(topLeft);
        GameResources.get().getEngine().addEntity(topRight);
    }

    public void select(Entity entity) {
        bottomRight.add(new PositionComponent(startX, startY));
        bottomLeft.add(new PositionComponent(startX, startY));
        topRight.add(new PositionComponent(startX, startY));
        topLeft.add(new PositionComponent(startX, startY));

        RenderComponent renderComponent = Mappers.render.get(entity);

        float width = renderComponent.getWidth();
        float height = renderComponent.getHeight();

        bottomLeft.add(new FollowComponent(entity, new Vector2(0, 0)));
        bottomRight.add(new FollowComponent(entity, new Vector2(width - 1f, 0)));
        topLeft.add(new FollowComponent(entity, new Vector2(0, height - 1f)));
        topRight.add(new FollowComponent(entity, new Vector2(width - 1f, height - 1f)));
    }

    public void unselect() {
        bottomRight.remove(PositionComponent.class);
        bottomLeft.remove(PositionComponent.class);
        topRight.remove(PositionComponent.class);
        topLeft.remove(PositionComponent.class);
    }
}
