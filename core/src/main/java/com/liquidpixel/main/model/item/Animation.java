package com.liquidpixel.main.model.item;

public class Animation {

    private boolean enabled = true;
    private String model = "DEFAULT";
    private String sync = null;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
