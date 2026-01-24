package com.liquidpixel.main.ai.tasks.leaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.blackboards.TaskBlackboard;
import com.liquidpixel.main.ai.tasks.PoolableTask;
import com.liquidpixel.main.interfaces.IChunkBuilder;

public class BuildTerrainItemsTask extends PoolableTask<TaskBlackboard> {

    IChunkBuilder chunkBuilder;
    GridPoint2 location;
    int width;
    int height;

    public BuildTerrainItemsTask(IChunkBuilder chunkBuilder, GridPoint2 location, int width, int height) {
        this.chunkBuilder = chunkBuilder;
        this.location = location;
        this.width = width;
        this.height = height;
    }

    @Override
    public void tick(float delta) {
        Gdx.app.debug("Build Terrain Items", "" + location);
        long startTime = System.nanoTime();
        //TODO spawn trees
        chunkBuilder.spawnTerrainItems(location, width, height);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double milliseconds = (double) duration / 1_000_000;
//        System.out.println("Execution time in milliseconds: " + milliseconds);
        success();
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }
}
