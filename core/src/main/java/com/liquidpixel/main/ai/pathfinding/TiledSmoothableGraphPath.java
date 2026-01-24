package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.ai.pathfinding.TiledNode;

public class TiledSmoothableGraphPath<N extends TiledNode<N>> extends DefaultGraphPath<N> implements
    SmoothableGraphPath<N, Vector2> {

    private Vector2 tmpPosition = new Vector2();

    /**
     * Returns the position of the node at the given index.
     * <p>
     * <b>Note that the same Vector2 instance is returned each time this method is called.</b>
     *
     * @param index the index of the node you want to know the position
     */
    @Override
    public Vector2 getNodePosition(int index) {
        N node = nodes.get(index);
        return tmpPosition.set(node.x, node.y);
    }

    public N first() {
        return nodes.first();
    }

    public void pop() {
        if (nodes.size >= 0) {
            nodes.removeIndex(0);
        }
    }

    @Override
    public void swapNodes(int index1, int index2) {
// x.swap(index1, index2);
// y.swap(index1, index2);
        nodes.set(index1, nodes.get(index2));
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.truncate(newLength);
    }

    public String toString() {
        return nodes.toString();
    }

}
