package com.liquidpixel.main.ai.tasks.leaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.ai.blackboards.TaskBlackboard;
import com.liquidpixel.main.ai.tasks.PoolableTask;
import com.liquidpixel.main.interfaces.IChunkBuilder;

public class BuildChunkTask extends PoolableTask<TaskBlackboard> {

    IChunkBuilder chunkBuilder;
    GridPoint2 location;
    int width;
    int height;

    public BuildChunkTask(IChunkBuilder chunkBuilder, GridPoint2 location, int width, int height) {
        this.chunkBuilder = chunkBuilder;
        this.location = location;
        this.width = width;
        this.height = height;
    }

    @Override
    public void tick(float delta) {
        Gdx.app.debug("Chunk Build", "" + location);
        long startTime = System.nanoTime();
        chunkBuilder.buildChunk(location, width, height);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double milliseconds = (double) duration / 1_000_000;
        System.out.println("Execution time in milliseconds: " + milliseconds);
        success();
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }
}
