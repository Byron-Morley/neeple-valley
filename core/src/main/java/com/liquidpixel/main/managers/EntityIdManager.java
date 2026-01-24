package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.liquidpixel.main.components.EntityComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.engine.GameResources;
import com.liquidpixel.main.interfaces.services.ISettlementService;
import com.liquidpixel.main.ui.view.selection.SelectionUI;
import com.liquidpixel.main.utils.Mappers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class EntityIdManager implements EntityListener {

    ISettlementService settlementService;

    public EntityIdManager() {

    }

    @Override
    public void entityAdded(Entity entity) {
//        System.out.println("Entity Added");

        if (!Mappers.entity.has(entity)) {
            String id = generateUniqueId();
            entity.add(new EntityComponent(id));
        }
//        printEntityComponents(entity);


        Engine engine = GameResources.get().getEngine();

        // Clean up references from settlements
        for (Entity settlement : engine.getEntitiesFor(Family.all(SettlementComponent.class).get())) {
            SettlementComponent settlementComponent = Mappers.settlement.get(settlement);

            if (settlementComponent.getSettlementType().equals("player")) {
                settlementService.setSelectedSettlement(settlement);
            }
        }

    }

    public static String getEntityName(Entity entity) {
        if (entity == null) return "null";
        if (Mappers.item.has(entity)) {
            return "item " + Mappers.item.get(entity).getName();
        } else if (Mappers.agent.has(entity)) {
            return "agent " + Mappers.agent.get(entity).getId();
        } else {
            return entity.toString();
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        System.out.println("Entity Removed: " + getEntityName(entity));

        // Get the engine
        Engine engine = GameResources.get().getEngine();

        // Clean up references from settlements
        for (Entity settlement : engine.getEntitiesFor(Family.all(SettlementComponent.class).get())) {
            SettlementComponent settlementComponent = Mappers.settlement.get(settlement);

            // Remove from buildings list if it's a building
            if (settlementComponent.getAssets().contains(entity)) {
                settlementComponent.getAssets().remove(entity);
                System.out.println("Removed building reference from settlement");
            }
        }

        if (Mappers.resource.has(entity)) {
            SelectionUI selectionUI = Mappers.resource.get(entity).getSelectionUI();
            if (selectionUI != null) selectionUI.remove();
        }

    }


    private String generateUniqueId() {
        String timestamp = Long.toString(Instant.now().toEpochMilli());
        String uuid = UUID.randomUUID().toString();
        return timestamp + "-" + uuid;
    }

    public static Entity getEntityById(String id) {
        Engine engine = GameResources.get().getEngine();
        for (Entity entity : engine.getEntities()) {
            if (Mappers.entity.has(entity)) {
                EntityComponent entityComponent = Mappers.entity.get(entity);
                if (id.equals(entityComponent.getId())) return entity;
            }
        }
        return null;
    }

    public static ArrayList<String> getEntityStringData(ArrayList<String> data, ArrayList<Entity> entityArray) {
        if (data == null) {
            data = new ArrayList<>();
            for (Entity entity : entityArray) {
                data.add(Mappers.entity.get(entity).getId());
            }
        }
        return data;
    }

    public void setSettlementService(ISettlementService settlementService) {
        this.settlementService = settlementService;
    }
}
