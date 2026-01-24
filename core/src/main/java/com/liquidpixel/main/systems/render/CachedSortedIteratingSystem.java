package com.liquidpixel.main.systems.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.BodyComponent;
import com.liquidpixel.main.components.render.RenderComponent;
import com.liquidpixel.main.managers.EntityInteractionManager;
import com.liquidpixel.main.utils.Mappers;
import com.badlogic.gdx.math.GridPoint2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An extension of SortedIteratingSystem that provides caching for the comparator
 * to improve performance.
 */
public abstract class CachedSortedIteratingSystem extends SortedIteratingSystem {
    // Cache for comparison results
    private static final Map<Long, Integer> comparisonCache = new HashMap<>();
    private static final int MAX_CACHE_SIZE = 10000;

    public CachedSortedIteratingSystem(Family family, Comparator<Entity> comparator) {
        super(family, comparator);
    }

    /**
     * Clears the comparator cache.
     */
    protected void clearComparatorCache() {
        comparisonCache.clear();
    }

    /**
     * Checks if the cache is too large and clears it if necessary.
     */
    protected void checkCacheSize() {
        if (comparisonCache.size() > MAX_CACHE_SIZE) {
            comparisonCache.clear();
        }
    }

    /**
     * A comparator that caches its results for better performance.
     */
    protected static class ZComparator implements Comparator<Entity> {
        private static final int DIFF_MULTIPLIER = 10;
        private ComponentMapper<PositionComponent> pm = Mappers.position;
        private ComponentMapper<RenderComponent> rm = Mappers.render;
        private ComponentMapper<BodyComponent> bm = Mappers.body;
        private EntityInteractionManager interactionManager = EntityInteractionManager.getInstance();

        @Override
        public int compare(Entity e1, Entity e2) {
            // First check entity IDs to ensure consistent ordering
            if (e1.equals(e2)) return 0;

            // Generate a unique key for this entity pair (ensure e1 < e2 for consistent caching)
            long entityPairKey;
            boolean swapped = false;

            if (e1.hashCode() > e2.hashCode()) {
                Entity temp = e1;
                e1 = e2;
                e2 = temp;
                swapped = true;
            }

            entityPairKey = ((long)e1.hashCode() << 32) | (e2.hashCode() & 0xFFFFFFFFL);

            // Check cache first
            Integer cachedResult = comparisonCache.get(entityPairKey);
            if (cachedResult != null) {
                return swapped ? -cachedResult : cachedResult;
            }

            // Get components once to avoid multiple lookups
            RenderComponent r1 = rm.get(e1);
            RenderComponent r2 = rm.get(e2);

            // First check priority - this is fast
            int priorityDiff = r2.getPriority().getValue() - r1.getPriority().getValue();
            if (priorityDiff != 0) {
                comparisonCache.put(entityPairKey, priorityDiff);
                return swapped ? -priorityDiff : priorityDiff;
            }

            // Then check interaction rules
            if (interactionManager.shouldRenderAbove(e1, e2)) {
                comparisonCache.put(entityPairKey, 1);
                return swapped ? -1 : 1;
            }
            if (interactionManager.shouldRenderAbove(e2, e1)) {
                comparisonCache.put(entityPairKey, -1);
                return swapped ? 1 : -1;
            }

            // Finally check vertical position
            int result = getVerticalPositionDifferente(e1, e2);
            comparisonCache.put(entityPairKey, result);
            return swapped ? -result : result;
        }

        private int getVerticalPositionDifferente(Entity e1, Entity e2) {
            PositionComponent p1 = pm.get(e1);
            PositionComponent p2 = pm.get(e2);

            // For most cases, just compare Y positions
            float yDiff = p2.getY() - p1.getY();

            // Only do complex body checks if entities are very close in Y position
            if (Math.abs(yDiff) < 0.5f) {
                BodyComponent b2 = bm.get(e2);
                if (b2 != null) {
                    List<GridPoint2> walkablePoints = b2.generateAbsolutePositions(p2.getGridPosition());
                    GridPoint2 position = p1.getGridPosition();
                    for (GridPoint2 point : walkablePoints) {
                        if (position.x < point.x - 0.5f && position.x > point.x + 0.5f) {
                            if (position.y < point.y + 1) {
                                return 1;
                            }
                        }
                    }
                }
            }

            return (int) (yDiff * DIFF_MULTIPLIER);
        }
    }
}
