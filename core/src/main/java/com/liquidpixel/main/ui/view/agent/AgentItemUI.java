package com.liquidpixel.main.ui.view.agent;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.ai.BehaviorComponent;
import com.liquidpixel.main.components.agent.AgentComponent;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.utils.Mappers;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class AgentItemUI extends VisTable {

    private final Entity agent;

    public AgentItemUI(Entity agent) {
        super();
        this.agent = agent;

        setBackground("window-bg");
        pad(10);

        setupMainContent();
    }

    private void setupMainContent() {
        // Create main content row
        VisTable mainRow = new VisTable();

        // Create left column with basic info
        VisTable leftColumn = new VisTable();
        leftColumn.top(); // Align content to top

        // Agent ID
        AgentComponent agentComponent = Mappers.agent.get(agent);
        String agentId = agentComponent != null ? agentComponent.getId() : getEntityId(agent);
        leftColumn.add(new VisLabel("Agent ID: " + agentId)).left().row();

        // Agent type from ItemComponent
        ItemComponent item = Mappers.item.get(agent);
        String agentType = item != null ? item.getLabel() : "Unknown Agent";
        leftColumn.add(new VisLabel("Type: " + agentType)).left().row();

        // Create right column with status info
        VisTable rightColumn = new VisTable();
        rightColumn.top(); // Align content to top

        // Position info
        PositionComponent position = Mappers.position.get(agent);
        String positionStr = position != null ? position.getGridPosition().toString() : "Unknown";
        rightColumn.add(new VisLabel("Position: " + positionStr)).left().row();

        // Behavior info
        BehaviorComponent behavior = Mappers.behavior.get(agent);
        String behaviorStr = behavior != null ? behavior.getBehaviorFileName() : "None";
        rightColumn.add(new VisLabel("Behavior: " + behaviorStr)).left().row();

        // Worker info
        WorkerComponent worker = Mappers.worker.get(agent);
        String workerStatus = worker != null ? "Worker" : "Not Working";
        rightColumn.add(new VisLabel("Status: " + workerStatus)).left().row();

        // Add columns to main row
        mainRow.add(leftColumn).expand().fill().pad(5);
        mainRow.add(rightColumn).expand().fill().pad(5);

        // Add main row to this table with fixed height
        add(mainRow).growX().height(60).row();
    }

    private String getEntityId(Entity entity) {
        if (entity == null) return "Unknown";

        // Try to get a meaningful ID from the entity
        return String.valueOf(entity.hashCode() % 10000); // Simple ID based on hash
    }
}
