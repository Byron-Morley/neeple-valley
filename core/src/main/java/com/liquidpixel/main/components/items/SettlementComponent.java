package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.AssetComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.components.colony.HouseComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.utils.Mappers;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettlementComponent implements Component {

    Entity settlement;

    @JsonProperty
    float timeSinceLastUpdate = 0;

    @JsonProperty
    String name = "Settlement";

    @JsonProperty
    float baseWorkerMovementSpeed = 1f;

    @JsonProperty
    float baseWorkerHarvestSpeed = 1f;

    @JsonProperty
    float baseHarvestAmount = 1f;

    @JsonProperty
    float workerStackCarryLimit = 1f;

    @JsonProperty
    List<Entity> assets;

    @JsonProperty
    List<Integer> immigrationCounter;

    @JsonProperty
    List<Entity> population;

    @JsonProperty
    List<IWorkOrder> workOrders;

    @JsonProperty
    boolean freeBuilding = true;

    String settlementType;

    @JsonCreator
    public SettlementComponent(
        @JsonProperty("name") String name,
        @JsonProperty("baseWorkerMovementSpeed") float baseWorkerMovementSpeed,
        @JsonProperty("baseWorkerHarvestSpeed") float baseWorkerHarvestSpeed,
        @JsonProperty("buildings") List<Entity> assets,
        @JsonProperty("population") List<Entity> population,
        @JsonProperty("immigrationCounter") List<Integer> immigration,
        @JsonProperty("providerWorkList") List<IWorkOrder> workOrders
    ) {

        this.name = name != null ? name : "Settlement";
        this.baseWorkerMovementSpeed = baseWorkerMovementSpeed;
        this.baseWorkerHarvestSpeed = baseWorkerHarvestSpeed;

        this.assets = assets != null ? assets : new ArrayList<>();
        this.population = population != null ? population : new ArrayList<>();
        this.immigrationCounter = immigration != null ? immigration : new ArrayList<>();
        this.workOrders = workOrders != null ? workOrders : new ArrayList<>();
    }


    public SettlementComponent(String settlementType, Entity settlement) {
        this.settlementType = settlementType;
        timeSinceLastUpdate = 0;
        assets = new ArrayList<>();
        population = new ArrayList<>();
        immigrationCounter = new ArrayList<>();
        this.workOrders = new ArrayList<>();
        this.settlement = settlement;
    }

    public float getBaseWorkerMovementSpeed() {
        return baseWorkerMovementSpeed;
    }

    public float getBaseWorkerHarvestSpeed() {
        return baseWorkerHarvestSpeed;
    }

    public String getName() {
        return name;
    }

    public float getTimeSinceLastUpdate() {
        return timeSinceLastUpdate;
    }

    public void setTimeSinceLastUpdate(float timeSinceLastUpdate) {
        this.timeSinceLastUpdate = timeSinceLastUpdate;
    }

    public List<Entity> getAssets() {
        return assets;
    }

    public void addAsset(Entity asset, Entity settlement) {
        asset.add(new AssetComponent(settlement));
        assets.add(asset);
    }

    public void removeAsset(Entity asset) {
        asset.remove(AgentComponent.class);
        assets.remove(asset);
    }

    public void addImmigration(int immigration) {
        this.immigrationCounter.add(immigration);
    }

    public boolean hasImmigrationBeenCalculated(int day) {
        return immigrationCounter.contains(day);
    }

    public boolean isFreeBuilding() {
        return freeBuilding;
    }

    public void setFreeBuilding(boolean freeBuilding) {
        this.freeBuilding = freeBuilding;
    }

    public void addPopulation(Entity population) {
        WorkerComponent worker = Mappers.worker.get(population);
        worker.setSettlement(settlement);
        this.population.add(population);
    }

    public void removePopulation(Entity population) {
        this.population.remove(population);
    }

    public List<Entity> getPopulation() {
        return population;
    }

    public void addPersonToHouse(Entity person) {
        for (Entity building : getAssets()) {
            HouseComponent house = Mappers.house.get(building);
            if (house != null && house.availableCapacity() > 0) {
                house.addResident(person, building);
            }
        }
    }

    public void addPersonToJob(Entity person) {
        for (Entity building : getAssets()) {
            JobComponent jobs = Mappers.jobs.get(building);
            if (jobs != null && jobs.hasJobAvailable()) {
                jobs.addWorker(person, building);
                break;
            }
        }
    }

    public void addWorkOrder(IWorkOrder workOrder) {
        workOrders.add(workOrder);
    }

    public List<IWorkOrder> getWorkOrders() {
        return workOrders;
    }

    public float getWorkerStackCarryLimit() {
        return workerStackCarryLimit;
    }

    public float getBaseHarvestAmount() {
        return baseHarvestAmount;
    }

    public String getSettlementType() {
        return settlementType;
    }
}
