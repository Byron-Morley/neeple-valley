package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

public class EquipmentComponent implements Component {

    List<Entity> equipment;

    public EquipmentComponent() {
        this.equipment = new ArrayList<>();
    }

    public void addEquipment(Entity item) {
        equipment.add(item);
    }

    public void removeEquipment(Entity item) {
        equipment.remove(item);
    }

    public boolean hasEquipment() {
        return !equipment.isEmpty();
    }

    public List<Entity> getEquipment() {
        return equipment;
    }
}
