package com.liquidpixel.main.listeners.farming;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.components.items.FarmComponent;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.services.FarmService;
import com.liquidpixel.main.ui.view.windows.ConfirmationDialog;
import com.liquidpixel.main.utils.Mappers;

import static com.liquidpixel.main.utils.ui.Windows.CONFIRMATION_DIALOG;

public class RemoveCropButtonListener extends ClickListener {

    IWindowService windowService;
    IItemService itemService;
    Entity farm;

    Runnable onCropRemoved;

    public RemoveCropButtonListener(Entity farm, IItemService itemService, IWindowService windowService, Runnable onCropRemoved) {
        super();
        this.farm = farm;
        this.itemService = itemService;
        this.windowService = windowService;
        this.onCropRemoved = onCropRemoved;
    }


    @Override
    public void clicked(InputEvent event, float x, float y) {

        ConfirmationDialog dialog = windowService.getWindow(CONFIRMATION_DIALOG);
        dialog.show(
            "Are you sure you want to remove the crops?",
            "Yes",
            "No",
            () -> removeCropsFromFarm(farm)
        );
    }

    private void removeCropsFromFarm(Entity farm) {
        System.out.println("RemoveCropButtonListener");
        FarmService farmService = new FarmService(itemService);
        farmService.removePlantFromFarm(farm);


        // Trigger the callback to update the UI
        if (onCropRemoved != null) {
            onCropRemoved.run();
        }
    }
}
