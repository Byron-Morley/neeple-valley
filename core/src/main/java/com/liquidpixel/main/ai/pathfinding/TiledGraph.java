package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.ai.pathfinding.TiledNode;

import java.util.List;
import java.util.Map;

public interface TiledGraph<N extends TiledNode<N>> extends IndexedGraph<N> {
    N getNode (GridPoint2 position);
    Map<GridPoint2, FlatTiledNode> getNodes();
    int getWidth();
    int getHeight();
}
