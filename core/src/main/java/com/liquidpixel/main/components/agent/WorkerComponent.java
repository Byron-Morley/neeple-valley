package com.liquidpixel.main.components.agent;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.model.person.Meter;
import com.liquidpixel.main.model.person.Person;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkerComponent implements Component {


    public enum State {
        IDLE,
        BUSY;
    }

    @JsonProperty
    public State state = State.IDLE;

    Person person;
    Meter food;
    Meter energy;
    Entity settlement;

    public WorkerComponent(Person person) {
        this.person = person;
        food = new Meter("food", 150f, 100f, 25f, 100f);
        energy = new Meter("energy", 150f, 100f, 25f, 100f);
    }

    public Person getPerson() {
        return person;
    }

    public Meter getFood() {
        return food;
    }

    public Meter getEnergy() {
        return energy;
    }

    public boolean isBusy() {
        return state == State.BUSY;
    }

    public void setBusy(boolean busy) {
        state = busy ? State.BUSY : State.IDLE;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setSettlement(Entity settlement) {
        this.settlement = settlement;
    }

    public Entity getSettlement() {
        return settlement;
    }
}
