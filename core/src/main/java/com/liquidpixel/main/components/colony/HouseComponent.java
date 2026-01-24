package com.liquidpixel.main.components.colony;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.ResidentComponent;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HouseComponent implements Component {

    int capacity;
    List<Entity> residents;
    Entity settlement;

    public HouseComponent(int capacity) {
        this.capacity = capacity;
        this.residents = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public Entity getSettlement() {
        return settlement;
    }

    public void setSettlement(Entity settlement) {
        this.settlement = settlement;
    }

    public List<Entity> getResidents() {
        return residents;
    }

    public void setResidents(List<Entity> residents) {
        this.residents = residents;
    }

    public void addResident(Entity resident, Entity house) {
        residents.add(resident);
        resident.add(new ResidentComponent(house));
    }

    public void removeResident(Entity entity) {
        residents.remove(entity);
    }

    public int availableCapacity() {
        return capacity - residents.size();
    }
}
