package com.liquidpixel.main.ui.view.infoPanel;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.liquidpixel.main.components.items.InformationComponent;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.utils.Mappers;
import com.kotcrab.vis.ui.widget.VisTable;


public class InfoPanelUI extends ReuseableWindow implements IGet<Group>, Updatable {

    ISelectionService selectionService;
    InfoPanelUIPresenter presenter;
    Entity entity;
    private VisTable container;

    public InfoPanelUI(ISelectionService selectionService) {
        super("Information");
        this.selectionService = selectionService;
        this.setVisible(false);
        addCloseButton();
        centerWindow();
    }

    public void init() {
        this.setMovable(true);
        this.setVisible(true);

        if (container == null) {
            container = new VisTable();
            container.setPosition(0, 0);
            container.defaults().pad(10, 5, 10, 5);

            presenter = new InfoPanelUIPresenter(entity);

            container.row().fill().expandX().expandY();
            container.add(presenter);

            defaults().pad(10, 5, 10, 5);
            row().fill().expandX().expandY();
            add(container).grow();
        }
        pack();
    }

    /**
     * Clears all content from the window
     */
    private void clearContent() {
        clearChildren();
        // Reset defaults after clearing
        defaults().pad(10, 5, 10, 5);
    }

    @Override
    public Window get() {
        return this;
    }

    @Override
    public void update() {
        if (entity != null && presenter != null) {
            presenter.update();
            pack();
        }
    }

    public void setEntity(Entity entity) {
        if (entity != this.entity) {
            this.entity = entity;

            // If presenter exists, update it with the new entity
            if (presenter != null) {
                presenter.setEntity(entity);
            }

            if (Mappers.information.has(entity)) {
                InformationComponent informationComponent = Mappers.information.get(entity);
                getTitleLabel().setText(informationComponent.getLabel());
                pack();
            }
        }
    }
}
