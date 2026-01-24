package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class AssetComponent implements Component {

    Entity settlement;

    public AssetComponent(Entity settlement) {
        this.settlement = settlement;
    }

    public Entity getSettlement() {
        return settlement;
    }

    public void setSettlement(Entity settlement) {
        this.settlement = settlement;
    }
}
