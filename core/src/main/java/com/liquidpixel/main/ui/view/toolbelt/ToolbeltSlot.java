package com.liquidpixel.main.ui.view.toolbelt;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.liquidpixel.sprite.model.GameSprite;
import com.badlogic.gdx.utils.Scaling;
import com.liquidpixel.item.components.ItemComponent;

public class ToolbeltSlot extends Stack {
    private ItemComponent item;
    private Image itemImage;
    private ImageButton slotButton;

    public ToolbeltSlot(Skin skin) {
        this.slotButton = new ImageButton(skin, "slot");
        this.itemImage = new Image();
        itemImage = new Image();
        itemImage.setScaling(Scaling.contain);
        add(slotButton);
        add(itemImage);
    }


    public void setContent(ItemComponent item){
        if(this.item == item)
            return;

        updatePicture(item);
        this.item = item;
    }
    private void updatePicture(ItemComponent item) {
        GameSprite sprite = item.getIcon();
        Drawable drawable = new TextureRegionDrawable(sprite);
        itemImage.setDrawable(drawable);
    }

    public void setChecked(boolean b) {
        slotButton.setChecked(b);
    }
}
