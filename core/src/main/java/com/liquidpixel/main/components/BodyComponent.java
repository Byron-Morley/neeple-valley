package com.liquidpixel.main.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.item.factories.ItemFactory;
import com.liquidpixel.main.model.sprite.BodyOffset;
import com.liquidpixel.main.model.sprite.CustomGridPoint;
import com.liquidpixel.main.model.sprite.NodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class BodyComponent implements Component {

    @JsonProperty
    public String name;

    @JsonProperty
    boolean isFoundation;

    @JsonIgnore
    public GridPoint2 offset;

    @JsonIgnore
    private GridPoint2 interactionPoint;

    @JsonIgnore
    public int width;

    @JsonIgnore
    public int height;

    @JsonIgnore
    public boolean collision;

    @JsonIgnore
    private List<GridPoint2> focusPoints;

    @JsonIgnore
    public List<CustomGridPoint> customCollider;

    private NodeType massNodeType;

    @JsonCreator
    public BodyComponent(
        @JsonProperty("name") String name,
        @JsonProperty("isFoundation") boolean isFoundation
    ) {
        this.name = name;
        this.isFoundation = isFoundation;

        BodyOffset bodyOffset = ItemFactory.getBodyOffset(name);
        if (bodyOffset != null) {
            this.offset = bodyOffset.getOffset();
            this.interactionPoint = bodyOffset.getInteractionPoint();
            this.width = bodyOffset.getWidth();
            this.height = bodyOffset.getHeight();
            this.customCollider = bodyOffset.getCustomCollider();
            this.collision = bodyOffset.isCollision();
            this.focusPoints = bodyOffset.getFocusPoints();
            this.massNodeType = bodyOffset.getN();
        }
        if (isFoundation) {
            createFoundationBody();
        }
    }

    private void createFoundationBody() {
        List<GridPoint2> positions = generateAbsolutePositions(new GridPoint2(0, 0));
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (GridPoint2 pos : positions) {
            minX = Math.min(minX, pos.x - 1);
            maxX = Math.max(maxX, pos.x + 1);
            minY = Math.min(minY, pos.y - 1);
            maxY = Math.max(maxY, pos.y + 1);
        }

        setOffset(new GridPoint2(0, 0));
        setInteractionPoint(new GridPoint2(0, 0));
        setWidth((maxX - minX + 1));
        setHeight((maxY - minY + 1));
        setCustomCollider(null);
    }

    public void setInteractionPoint(GridPoint2 interactionPoint) {
        this.interactionPoint = interactionPoint;
    }

    public GridPoint2 getInteractionPoint() {
        return interactionPoint;
    }

    @JsonIgnore
    public GridPoint2 getAbsoluteInteractionPoint(GridPoint2 origin) {
        return new GridPoint2(interactionPoint).add(new GridPoint2(origin).add(new GridPoint2(offset)));
    }

    public List<GridPoint2> generateAbsolutePositions(GridPoint2 origin) {
        return getCollisionAbsolutePositions(origin);
    }

    private List<GridPoint2> getAbsoluteWidthAndHeightPositions(GridPoint2 origin) {
        List<GridPoint2> positions = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                positions.add(
                    new GridPoint2(origin)
                        .add(new GridPoint2(x, y)
                            .add(new GridPoint2(offset))));
            }
        }
        return positions;
    }

    private List<CustomGridPoint> getAbsoluteWidthAndHeightPositionsCustom(GridPoint2 origin) {
        List<CustomGridPoint> positions = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                positions.add(
                    (CustomGridPoint) new CustomGridPoint(origin, massNodeType)
                        .add(new CustomGridPoint(new GridPoint2(x, y), massNodeType)
                            .add(new CustomGridPoint(offset, massNodeType))));
            }
        }
        return positions;
    }


    public List<CustomGridPoint> getAllAbsolutePositions(GridPoint2 origin) {
        List<CustomGridPoint> positions = getAbsoluteWidthAndHeightPositionsCustom(origin);

        if (customCollider != null) {
            for (CustomGridPoint point : customCollider) {
                positions.add(
                    (CustomGridPoint) new CustomGridPoint(origin, point.getN())
                        .add(new CustomGridPoint(offset)
                            .add(new CustomGridPoint(point))));
            }
        }

        return positions;
    }

    public List<GridPoint2> getCollisionAbsolutePositions(GridPoint2 origin) {
        List<GridPoint2> positions = getAbsoluteWidthAndHeightPositions(origin);

        if (customCollider != null) {
            for (CustomGridPoint point : customCollider) {

                if (point.getN() == NodeType.TILE_WALL) {
                    positions.add(
                        new GridPoint2(origin)
                            .add(new GridPoint2(offset)
                                .add(new GridPoint2(point))));
                }
            }
        }
        return positions;
    }


    @JsonIgnore
    public boolean hasCollision() {
        return collision;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOffset(GridPoint2 offset) {
        this.offset = offset;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCustomCollider(List<CustomGridPoint> customCollider) {
        this.customCollider = customCollider;
    }

    public List<GridPoint2> getFocusPoints() {
        return focusPoints;
    }
}
