package com.liquidpixel.main.dto.agent;

import com.liquidpixel.sprite.model.Layer;
import com.liquidpixel.main.model.item.Animation;

import java.util.ArrayList;
import java.util.List;

public class Agent {
    private String name;
    private float velocity;
    private Animation animation;
    private boolean player = false;
    private boolean worker = false;
    private boolean camera = false;
    private boolean selectable = false;
    private List<Layer> layers = new ArrayList<>();
    String settlement;
    String bodyModel;
    String behavior;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getVelocity() {
        return velocity;
    }

    public String getAnimationModel() {
        return animation.getModel();
    }

    public boolean isPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public boolean isCamera() {
        return camera;
    }

    public void setCamera(boolean camera) {
        this.camera = camera;
    }

    public String getBehavior() {
        return behavior;
    }

    public boolean hasBehavior() {
        return behavior != null;
    }

    public Animation getAnimation() {
        return animation;
    }

    public boolean isWorker() {
        return worker;
    }

    public String getSettlement() {
        return settlement;
    }

    public boolean isSettlement() {
        return settlement != null;
    }

    public boolean hasAnimations() {
        return animation != null;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public String getBodyModel() {
        return bodyModel;
    }

    public List<Layer> getLayers() {
        return layers;
    }

}
