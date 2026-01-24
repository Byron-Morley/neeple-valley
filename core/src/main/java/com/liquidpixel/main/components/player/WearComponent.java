package com.liquidpixel.main.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.items.EquipableComponent;
import com.liquidpixel.main.model.equip.EquipSlot;
import com.liquidpixel.main.utils.Mappers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WearComponent implements Component {

    @JsonProperty
    public Map<EquipSlot, Entity> wearables;


    @JsonCreator
    public WearComponent(
        @JsonProperty("wearables") Map<EquipSlot, Entity> wearables) {
        this.wearables = wearables;
    }

    @JsonIgnore
    public List<Entity> asList() {
        return wearables.keySet()
            .stream()
            .sorted(Comparator.comparingInt(EquipSlot::getRenderPriority))
            .map(e -> wearables.get(e))
            .collect(Collectors.toList());
    }


    public Map<EquipSlot, Entity> getWearables() {
        return wearables;
    }

    public void addItem(Entity item) {
        if (Mappers.equipable.has(item)) {
            EquipableComponent equipableComponent = Mappers.equipable.get(item);
            wearables.put(equipableComponent.getEquipSlot(), item);
        } else {
            System.out.println("This item is not wearable");
        }
    }

    public void remove(EquipSlot equipSlot) {
        wearables.remove(equipSlot);
    }
}
