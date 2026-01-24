package com.liquidpixel.main.model.sprite;

import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

public class BodyOffset {
    GridPoint2 offset = new GridPoint2(0,0);;
    GridPoint2 interactionPoint = new GridPoint2(0,0);
    int width;
    int height;
    NodeType n = NodeType.TILE_FLOOR;
    List<CustomGridPoint> customCollider = null;
    List<GridPoint2> focusPoints = null;
    boolean collision = true;

    public GridPoint2 getOffset() {
        return offset;
    }

    public GridPoint2 getInteractionPoint() {
        return interactionPoint;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<CustomGridPoint> getCustomCollider() {
        return customCollider;
    }

    public boolean isCollision() {
        return collision;
    }

    public boolean hasCollision() {
        return collision;
    }

    public List<GridPoint2> getFocusPoints() {
        return focusPoints;
    }

    public NodeType getN() {
        return n;
    }
}
