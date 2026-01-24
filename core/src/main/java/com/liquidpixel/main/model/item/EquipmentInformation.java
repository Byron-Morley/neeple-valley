package com.liquidpixel.main.model.item;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.model.equip.EquipSlot;
import com.liquidpixel.main.model.equip.EquipType;
import com.liquidpixel.core.core.Action;
import com.liquidpixel.core.core.Direction;

import java.util.Map;

public class EquipmentInformation {
    private EquipSlot equipSlot;
    private EquipType equipType;
    private Map<Direction, GridPoint2> reach;
    Action action;

    public EquipmentInformation() { }

    public EquipSlot getEquipSlot() {
        return equipSlot;
    }

    public void setEquipSlot(EquipSlot equipSlot) {
        this.equipSlot = equipSlot;
    }

    public EquipType getEquipType() {
        return equipType;
    }

    public void setEquipType(EquipType equipType) {
        this.equipType = equipType;
    }

    public Map<Direction, GridPoint2> getReach() {
        return reach;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "EquipmentInformation{" +
                "equipSlot=" + equipSlot +
                ", equipType=" + equipType +
                '}';
    }
}
