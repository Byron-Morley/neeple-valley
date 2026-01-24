package com.liquidpixel.main.ui.view.infoPanel.people;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.utils.Mappers;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.VisUI;

import java.util.List;

public class PeoplePanelPresenter extends VisTable {
    private Entity entity;
    private VisLabel titleLabel;
    private VisLabel workerCountLabel;
    private VisTable workersListTable;
    private VisScrollPane scrollPane;

    public PeoplePanelPresenter() {
        super();
        init();
        updateWorkerInfo();
    }

    private void init() {
        titleLabel = new VisLabel("Workers");
        workerCountLabel = new VisLabel("");

        // Header row
        add(titleLabel).left().expandX();
        add(workerCountLabel).right();
        row();

        // Create a table for the workers list
        workersListTable = new VisTable();
        workersListTable.left().top();
        workersListTable.pad(5); // Add padding around the entire list

        // Create a scroll pane for the workers list
        scrollPane = new VisScrollPane(workersListTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Add the scroll pane to the main table - removed fixed height to allow shrinking
        add(scrollPane).colspan(2).expand().fill().top();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        updateWorkerInfo();
    }

    public void updateWorkerInfo() {
        if (entity == null) {
            workerCountLabel.setText("0/0");
            workersListTable.clear();
            return;
        }

        JobComponent jobComponent = Mappers.jobs.get(entity);
        if (jobComponent != null) {
            List<Entity> workers = jobComponent.getWorkers();
            int currentWorkers = workers != null ? workers.size() : 0;
            int maxWorkers = jobComponent.getJobCapacity();
            workerCountLabel.setText(currentWorkers + "/" + maxWorkers);

            // Update the workers list
            updateWorkersList(workers, entity);
        } else {
            workerCountLabel.setText("0/0");
            workersListTable.clear();
        }
    }

    private void updateWorkersList(List<Entity> workers, Entity jobEntity) {
        workersListTable.clear();

        if (workers == null || workers.isEmpty()) {
            VisTable emptyRow = new VisTable();
            emptyRow.setBackground(VisUI.getSkin().getDrawable("window-bg"));
            emptyRow.add(new VisLabel("No workers assigned")).expandX().left().pad(5); // Reduced from 10 to 5
            workersListTable.add(emptyRow).expandX().fillX().pad(2); // Reduced from 5 to 2
            workersListTable.row();
            return;
        }

        // Get the job entity position
        PositionComponent jobPosition = Mappers.position.get(jobEntity);
        GridPoint2 jobGridPos = jobPosition != null ? jobPosition.getGridPosition() : null;

        // Add each worker to the list
        for (Entity worker : workers) {
            // Create a panel-like container for each worker
            VisTable workerPanel = new VisTable();
            workerPanel.setBackground(VisUI.getSkin().getDrawable("window-bg")); // Use window background for panel effect

            WorkerComponent workerComponent = Mappers.worker.get(worker);
            // Worker name
            String workerName = workerComponent.getPerson().getName();
            workerPanel.add(new VisLabel(workerName)).left().expandX().pad(5); // Reduced from 10 to 5

            // Status indicator (green if at job location, red if not)
            VisTable statusIndicator = new VisTable();
            statusIndicator.setBackground(VisUI.getSkin().getDrawable("white"));

            boolean isAtJobLocation = false;
            if (jobGridPos != null) {
                PositionComponent workerPosition = Mappers.position.get(worker);
                if (workerPosition != null) {
                    GridPoint2 workerGridPos = workerPosition.getGridPosition();
                    isAtJobLocation = (workerGridPos != null && workerGridPos.equals(jobGridPos));
                }
            }

            Color indicatorColor = isAtJobLocation ? Color.GREEN : Color.RED;
            statusIndicator.setColor(indicatorColor);
            workerPanel.add(statusIndicator).size(12, 12).pad(5).right(); // Reduced from 10 to 5

            // Add the worker panel to the list with padding between items
            workersListTable.add(workerPanel).expandX().fillX().pad(2); // Reduced from 5 to 2
            workersListTable.row();
        }

        // Force layout to update size
        invalidate();
        validate();
    }

    public void update() {
        updateWorkerInfo();
    }
}
