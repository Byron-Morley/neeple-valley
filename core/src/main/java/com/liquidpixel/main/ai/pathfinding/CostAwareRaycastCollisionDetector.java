package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.pathfinding.TiledGraph;
import com.liquidpixel.main.ai.pathfinding.TiledNode;
import com.liquidpixel.main.ai.pathfinding.TiledRaycastCollisionDetector;
import com.liquidpixel.main.model.sprite.NodeType;

public class CostAwareRaycastCollisionDetector<N extends TiledNode<N>> extends TiledRaycastCollisionDetector<N> {
    private final com.liquidpixel.main.ai.pathfinding.TiledGraph<N> worldMap;
    private final float costThreshold;

    public CostAwareRaycastCollisionDetector(TiledGraph<N> worldMap, float costThreshold) {
        super(worldMap);
        this.worldMap = worldMap;
        this.costThreshold = costThreshold;
    }

    @Override
    public boolean collides(Ray<Vector2> ray) {
        // First check for physical collisions using the parent implementation
        boolean physicalCollision = super.collides(ray);
        if (physicalCollision) return true;

        // Now check if the ray crosses any path tiles
        int x0 = (int) ray.start.x;
        int y0 = (int) ray.start.y;
        int x1 = (int) ray.end.x;
        int y1 = (int) ray.end.y;

        int tmp;
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        if (steep) {
            tmp = x0;
            x0 = y0;
            y0 = tmp;
            tmp = x1;
            x1 = y1;
            y1 = tmp;
        }
        if (x0 > x1) {
            tmp = x0;
            x0 = x1;
            x1 = tmp;
            tmp = y0;
            y0 = y1;
            y1 = tmp;
        }

        int deltax = x1 - x0;
        int deltay = Math.abs(y1 - y0);
        int error = 0;
        int y = y0;
        int ystep = (y0 < y1 ? 1 : -1);

        for (int x = x0; x <= x1; x++) {
            N tile = steep ? worldMap.getNode(new GridPoint2(y, x)) : worldMap.getNode(new GridPoint2(x, y));

            // If this tile is a path tile, don't allow smoothing through it
            if (tile != null && tile.getType() == NodeType.TILE_FLOOR) {
                return true;
            }

            error += deltay;
            if (error + error >= deltax) {
                y += ystep;
                error -= deltax;
            }
        }

        return false;
    }

    private float getCostForNode(N node) {
        // This is a simplified version; you'll need to implement based on your cost system
        // For example, if path tiles have NodeType.TILE_PATH:
        if (node.getType() == NodeType.TILE_FLOOR) {
            return 0.1f; // Very low cost for path tiles
        }
        return 1.0f; // Normal cost for regular floor tiles
    }

    @Override
    public boolean findCollision(Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
        throw new UnsupportedOperationException();
    }
}
