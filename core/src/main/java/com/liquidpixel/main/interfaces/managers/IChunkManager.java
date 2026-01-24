package com.liquidpixel.main.interfaces.managers;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.model.terrain.Chunk;

public interface IChunkManager {
    void addChunk(GridPoint2 location, Chunk chunk);

    void addNode(FlatTiledNode node);

    void setActiveChunk(Chunk activeChunk);
}
