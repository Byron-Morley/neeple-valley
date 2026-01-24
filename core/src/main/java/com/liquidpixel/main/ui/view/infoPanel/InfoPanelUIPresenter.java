package com.liquidpixel.main.ui.view.infoPanel;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.ui.view.infoPanel.people.PeoplePanelPresenter;
import com.liquidpixel.main.ui.view.infoPanel.storage.StoragePanelPresenter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.liquidpixel.main.utils.Mappers;

public class InfoPanelUIPresenter extends VisTable {

    VisTable contentTable;
    StorageComponent storageComponent;
    Entity entity;
    StoragePanelPresenter storagePanelPresenter;
    PeoplePanelPresenter peoplePanelPresenter;

    public InfoPanelUIPresenter(Entity entity) {
        super();
        this.entity = entity;
        this.storageComponent = entity.getComponent(StorageComponent.class);
        init();
    }

    private void init() {
//        debugAll();
        contentTable = new VisTable();

        // Create the storage panel presenter
        storagePanelPresenter = new StoragePanelPresenter();

        if (Mappers.storage.has(entity)) {
            storagePanelPresenter.setStorageComponent(storageComponent);
        }

        peoplePanelPresenter = new PeoplePanelPresenter();

        if(Mappers.jobs.has(entity)) {
            peoplePanelPresenter.setEntity(entity);
        }else{
            peoplePanelPresenter.setEntity(null);
        }

        // Add both presenters to the content table in vertical arrangement
        contentTable.add(storagePanelPresenter).growX().padBottom(10).row();
        contentTable.add(peoplePanelPresenter).growX().row();

        // Add the content table to this presenter
        this.add(contentTable).grow().pad(5);
    }

    public void update() {
        storagePanelPresenter.update();
        peoplePanelPresenter.update();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        this.storageComponent = entity.getComponent(StorageComponent.class);
        storagePanelPresenter.setStorageComponent(storageComponent);
        peoplePanelPresenter.setEntity(entity);
    }
}
