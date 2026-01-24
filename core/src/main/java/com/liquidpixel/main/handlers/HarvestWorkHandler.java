package com.liquidpixel.main.handlers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.HarvestableComponent;
import com.liquidpixel.main.components.ai.actions.HarvestComponent;
import com.liquidpixel.item.components.ItemComponent;
import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.pathfinding.api.IMapService;
import com.liquidpixel.main.interfaces.work.IWorkOrder;
import com.liquidpixel.main.interfaces.work.IWorkOrderHandler;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.main.results.WorkOrderResult;
import com.liquidpixel.main.utils.Mappers;
import com.liquidpixel.main.utils.SteeringUtils;
import com.liquidpixel.main.utils.predicates.Sorter;

import java.util.List;

public class HarvestWorkHandler implements IWorkOrderHandler {
    private final IMapService mapService;
    private final IItemService itemService;

    public HarvestWorkHandler(IMapService mapService, IItemService itemService) {
        this.mapService = mapService;
        this.itemService = itemService;
    }

    @Override
    public WorkOrderResult process(IWorkOrder workOrder, Entity worker, SettlementComponent settlement) {
        Entity destination = workOrder.getDestination();

        if (destination == null) {
            return WorkOrderResult.remove();
        }

        // Find the best focus point and interaction point
        Sorter sorter = new Sorter(Mappers.position.get(worker).getPosition());
        BodyComponent destinationBody = Mappers.body.get(destination);
         List<GridPoint2> focusPoints = null;
        try {
            focusPoints = destinationBody.getFocusPoints()
                .stream()
                .sorted(sorter::sortByCloseness)
                .toList();
        } catch (Exception e) {
            System.out.println("Error in HarvestWorkHandler, probably missing focus points");
            throw new RuntimeException(e);
        }
        GridPoint2 destinationPosition = Mappers.position.get(destination).getGridPosition();
        GridPoint2 workerPosition = Mappers.position.get(worker).getGridPosition();
        GridPoint2 focusPoint = null;
        GridPoint2 interactionPoint = null;

        // Get the closest focus point and set interaction point
        for (GridPoint2 point : focusPoints) {
            focusPoint = new GridPoint2(destinationPosition).add(new GridPoint2(point));
            GridPoint2 iPoint = mapService.findValidInteractionPoint(workerPosition, focusPoint);
            if (iPoint != null) {
                interactionPoint = iPoint;
                break;
            }
        }

        // If no valid interaction point was found, abort the job
        if (interactionPoint == null) {
            return WorkOrderResult.remove();
        }

        // Work out the tool reach
        Direction direction = SteeringUtils.getDirection(interactionPoint, focusPoint);
        HarvestableComponent harvestableComponent = Mappers.harvestable.get(destination);

        Item tool = ItemFactory.getModels().get(harvestableComponent.getTool());
        GridPoint2 toolReach = tool.getEquipmentInformation().getReach().get(direction);
//        GridPoint2 toolReach = new GridPoint2(0,0);

        // Create and add the harvest component
        ItemComponent itemComponent = Mappers.item.get(destination);
        HarvestComponent harvestComponent = new HarvestComponent(
            itemComponent.getName(),
            focusPoint,
            new GridPoint2(interactionPoint).add(new GridPoint2(toolReach))
        );

        worker.add(harvestComponent);
        workOrder.setComponent(harvestComponent);
        workOrder.start(worker);

        // Mark worker as busy
        Mappers.worker.get(worker).setBusy(true);

        return WorkOrderResult.success();
    }

    @Override
    public void completeWork(IWorkOrder workOrder) {

    }
}
