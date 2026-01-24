package com.liquidpixel.main.ui.view.modes;

import com.liquidpixel.main.ui.common.ButtonState;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.Tooltip;

import java.util.Map;

public class ButtonListUI extends VisTable {

    public ButtonListUI(Map<Integer, ButtonState> buttonStates) {
        for (Map.Entry<Integer, ButtonState> entry : buttonStates.entrySet()) {
            ButtonState state = entry.getValue();
            new Tooltip.Builder(state.tooltip).target(state.getButton()).build();
            add(state.getButton()).pad(5);
        }
        left();
    }
}
