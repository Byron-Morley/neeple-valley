package com.liquidpixel.main.model.entity;

public class PlayerModel {
    private int x;
    private int y;
    private float velocity;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }


    @Override
    public String toString() {
        return "PlayerModel{" +
                "x=" + x +
                ", y=" + y +
                ", velocity=" + velocity +
                '}';
    }
}
