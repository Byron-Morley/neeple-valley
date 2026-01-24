package com.liquidpixel.main.ai.behavior.stack;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.behavior.stack.NullValueException;
import com.liquidpixel.main.model.item.Recipe;

public class Value {
    private String stringValue;
    private Float floatValue;
    private Entity entityValue;
    private GridPoint2 positionValue;
    private Recipe recipe;

    public Value(String stringValue) {
        this.stringValue = stringValue;
    }

    public Value(int intValue) {
        this.floatValue = Float.valueOf(intValue);
    }

    public Value(float floatValue) {
        this.floatValue = floatValue;
    }

    public Value(Entity entityValue) {
        this.entityValue = entityValue;
    }

    public Value(GridPoint2 positionValue) {
        this.positionValue = positionValue;
    }

    public Value(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getString() {
        if (stringValue == null)
            throw new NullValueException();

        return stringValue;
    }

    public Float getFloat() {
        if (floatValue == null)
            throw new NullValueException();

        return floatValue;
    }

    public Entity getEntity() {
        if (entityValue == null) {
            throw new NullValueException();
        }
        return entityValue;
    }

    public GridPoint2 getPosition() {
        if (positionValue == null) {
            throw new NullValueException();
        }
        return positionValue;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
