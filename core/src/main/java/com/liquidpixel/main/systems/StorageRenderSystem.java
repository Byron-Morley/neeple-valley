package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.components.storage.StorageRenderRefreshComponent;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.components.SpriteComponent;
import com.liquidpixel.sprite.model.GameSprite;


public class StorageRenderSystem extends IteratingSystem {

    ISpriteFactory spriteFactory;
    private Array<String> missingSprites = new Array<>();

    public StorageRenderSystem(ISpriteFactory spriteFactory) {
        super(Family.all(StorageRenderRefreshComponent.class, StorageComponent.class).get());
        this.spriteFactory = spriteFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StorageComponent storageComponent = Mappers.storage.get(entity);
        IStorageItem item = storageComponent.getFirstItem();

        if (item != null) {

            RenderComponent renderComponent = Mappers.render.get(entity);
            GameSprite gameSprite = item.getSprite();
            if (gameSprite != null) {
                renderComponent.setSprite(gameSprite);
            }
//            else if (entity.getComponent(SpriteComponent.class) != null) {
//                SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
//                renderComponent.setSprite(spriteFactory.getSprite(spriteComponent.getName()));
//            }
        }
        entity.remove(StorageRenderRefreshComponent.class);
    }
}
