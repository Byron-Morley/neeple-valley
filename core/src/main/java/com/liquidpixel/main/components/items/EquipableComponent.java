package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.model.equip.EquipSlot;
import com.liquidpixel.main.model.equip.EquipType;
import com.liquidpixel.main.model.item.EquipmentInformation;
import com.liquidpixel.core.core.Action;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipableComponent implements Component {

    @JsonProperty
    EquipType equipType;

    @JsonProperty
    EquipSlot equipSlot;

    Action action;

    @JsonCreator
    public EquipableComponent(
        @JsonProperty("equipType") EquipType equipType,
        @JsonProperty("equipSlot") EquipSlot equipSlot,
        @JsonProperty("action") Action action
        ) {
        this.equipType = equipType;
        this.equipSlot = equipSlot;
        this.action = action;
    }

    public EquipableComponent(EquipmentInformation eq) {
        this.equipSlot = eq.getEquipSlot();
        this.equipType = eq.getEquipType();
        this.action = eq.getAction();
    }

    public EquipType getEquipType() {
        return equipType;
    }

    public void setEquipType(EquipType equipType) {
        this.equipType = equipType;
    }

    public EquipSlot getEquipSlot() {
        return equipSlot;
    }

    public void setEquipSlot(EquipSlot equipSlot) {
        this.equipSlot = equipSlot;
    }

    public Action getAction() {
        return action;
    }
}
