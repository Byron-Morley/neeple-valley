package com.liquidpixel.main.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.items.SettlementComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class utils {

    public static float BASE_COST = 1f;

    public static List<Entity> getList(ImmutableArray<Entity> array) {
        List<Entity> entities = new LinkedList<>();

        for (Entity e : array) {
            entities.add(e);
        }

        return entities;
    }

    public static SettlementComponent getSettlementFromAsset(Entity entity) {
        try {
            Entity settlementEntity = Mappers.asset.get(entity).getSettlement();
            return Mappers.settlement.get(settlementEntity);
        } catch (Exception e) {
//            Gdx.app.log("Error getting settlement from asset: ", String.valueOf(e.getMessage()));
            return null;
        }
    }

    public static List<GridPoint2> getAdjacentCoordinates(int x, int y, int width, int height) {
        List<GridPoint2> adjacentCoordinates = new ArrayList<>();

        int xMax = x + width - 1;
        int yMax = y + height - 1;

        for (int i = x; i <= xMax; i++) {
            adjacentCoordinates.add(new GridPoint2(i, y - 1)); // above
            adjacentCoordinates.add(new GridPoint2(i, yMax + 1)); // below
        }

        for (int j = y; j <= yMax; j++) {
            adjacentCoordinates.add(new GridPoint2(x - 1, j)); // left
            adjacentCoordinates.add(new GridPoint2(xMax + 1, j)); // right
        }

        return adjacentCoordinates;
    }


    public static List<Vector2> getAdjacentCoordinates(float x, float y, float width, float height) {
        List<Vector2> adjacentCoordinates = new ArrayList<>();

        float xMax = x + width - 1;
        float yMax = y + height - 1;

        for (float i = x; i <= xMax; i++) {
            adjacentCoordinates.add(new Vector2(i, y - 1)); // above
            adjacentCoordinates.add(new Vector2(i, yMax + 1)); // below
        }

        for (float j = y; j <= yMax; j++) {
            adjacentCoordinates.add(new Vector2(x - 1, j)); // left
            adjacentCoordinates.add(new Vector2(xMax + 1, j)); // right
        }

        return adjacentCoordinates;
    }

    public static boolean isNotWholeNumber(float number) {
        return number != (int) number;
    }

    public static String getEntityId(Entity entity) {
        if (Mappers.entity.has(entity)) {
            return Mappers.entity.get(entity).getId();
        }

        return "";
    }

    public static boolean isEntityType(Entity entity, String type) {
        String name = Mappers.item.get(entity).getName();
        return name.contains(type);
    }

    public static String getFilenameFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return path;
        }

        return path.substring(lastSlashIndex + 1);
    }
}
