package com.liquidpixel.main.factories;
import com.liquidpixel.core.api.components.IPositionComponent;
import com.liquidpixel.core.api.components.IStatusComponent;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.StatusComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.sprite.api.component.IRenderComponent;
import com.liquidpixel.sprite.api.factory.IComponentFactory;

public class ComponentFactory implements IComponentFactory {

    @Override
    public IPositionComponent createPositionComponent(float x, float y) {
        return new PositionComponent(x, y);
    }

    @Override
    public IRenderComponent createRenderComponent() {
        return new RenderComponent();
    }

    @Override
    public IStatusComponent createStatusComponent() {
        return new StatusComponent();
    }
}
