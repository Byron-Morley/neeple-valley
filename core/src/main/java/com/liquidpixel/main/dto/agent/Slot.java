package com.liquidpixel.main.dto.agent;

public class Slot {
    private String sprite;
    private int mod;

    // Add this default constructor
    public Slot() {
        // Default constructor for Jackson deserialization
    }

    // You might already have a constructor like this
    public Slot(String sprite, int mod) {
        this.sprite = sprite;
        this.mod = mod;
    }

    // Getters and setters
    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }
}
