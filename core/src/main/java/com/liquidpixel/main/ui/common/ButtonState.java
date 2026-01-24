package com.liquidpixel.main.ui.common;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImageButton;

public class ButtonState {
    VisImageButton button;
    public final TextureRegion defaultRegion;
    public final TextureRegion checkedRegion;
    public final String tooltip;
    public ChangeListener changeListener;

    public ButtonState(
        TextureRegion defaultRegion,
        TextureRegion checkedRegion,
        String tooltip,
        ChangeListener changeListener
    ) {
        this.defaultRegion = defaultRegion;
        this.checkedRegion = checkedRegion;
        this.tooltip = tooltip;
        this.changeListener = changeListener;

        TextureRegionDrawable defaultDrawable = new TextureRegionDrawable(defaultRegion);
        TextureRegionDrawable checkedDrawable = new TextureRegionDrawable(checkedRegion);
        VisImageButton.VisImageButtonStyle style = new VisImageButton.VisImageButtonStyle();
        style.imageUp = defaultDrawable;
        style.imageDown = defaultDrawable;
        style.imageChecked = checkedDrawable;

        button = new VisImageButton(style);
        button.addListener(changeListener);
    }

    public void setChecked(boolean checked) {
        button.setChecked(checked);
    }

    public VisImageButton getButton() {
        return button;
    }
}
