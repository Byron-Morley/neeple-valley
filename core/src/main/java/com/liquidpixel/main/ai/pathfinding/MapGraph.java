package com.liquidpixel.main.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.ai.pathfinding.FlatTiledConnection;
import com.liquidpixel.main.ai.pathfinding.FlatTiledNode;
import com.liquidpixel.main.ai.pathfinding.TiledGraph;
import com.liquidpixel.main.model.sprite.NodeType;

import java.util.HashMap;
import java.util.Map;

public class MapGraph implements TiledGraph<FlatTiledNode> {

    protected int width;
    protected int height;
    protected Map<GridPoint2, FlatTiledNode> nodes;
    public boolean diagonal;

    public MapGraph(int width, int height) {
        this.width = width;
        this.height = height;
        this.nodes = new HashMap<>();
        this.diagonal = true;
    }

    @Override
    public int getIndex(FlatTiledNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return width * height;
    }

    @Override
    public Array<Connection<FlatTiledNode>> getConnections(FlatTiledNode node) {
        return node.getConnections();
    }

    @Override
    public Map<GridPoint2, FlatTiledNode> getNodes() {
        return nodes;
    }

    @Override
    public FlatTiledNode getNode(GridPoint2 position) {
        return nodes.get(position);
    }

    public void connectBothNodes(FlatTiledNode node1, FlatTiledNode node2) {
        if (node1.getType().equals(NodeType.TILE_WALL) || node2.getType().equals(NodeType.TILE_WALL)) {

        } else {
            connectNodes(node1, node2);
            connectNodes(node2, node1);
        }
    }

    private void connectNodes(FlatTiledNode sourceNode, FlatTiledNode subNode) {
        if (sourceNode.getType() == NodeType.TILE_FLOOR && subNode.getType() == NodeType.TILE_FLOOR && !areNodesConnected(sourceNode, subNode)) {
            sourceNode.addConnection(new FlatTiledConnection(this, sourceNode, subNode));
        }
    }

    public boolean areNodesConnected(FlatTiledNode node1, FlatTiledNode node2) {
        for (Connection<FlatTiledNode> connection : node1.getConnections()) {
            if (connection.getToNode().equals(node2)) {
                return true;
            }
        }
        return false;
    }

    public boolean nodeExists(GridPoint2 position) {
        try {
            getNode(position);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void removeNodeConnections(FlatTiledNode sourceNode) {
        if (nodeExists(sourceNode.getLocation())) {
            for (Connection<FlatTiledNode> sourceConnection : sourceNode.getConnections()) {
                FlatTiledNode destinationNode = sourceConnection.getToNode();
                for (Connection<FlatTiledNode> destinationConnection : destinationNode.getConnections()) {
                    if (destinationConnection.getToNode().equals(sourceNode)) {
                        destinationNode.getConnections().removeValue(destinationConnection, true);
                    }
                }
            }
        }
        sourceNode.clearConnections();
    }

    public void updateNodeType(GridPoint2 position, NodeType nodeType) {
        try {
            FlatTiledNode node = getNode(position);
            node.setType(nodeType);
            if(nodeType == NodeType.TILE_WALL){
                removeNodeConnections(node);
            }
        } catch (NullPointerException e) {
//            Gdx.app.log("MapGraph", "addObstacle: " + position + " does not exist");
        }
    }

    public boolean isReady() {
        return !nodes.isEmpty();
    }

    public void clear() {
        nodes.clear();
    }
}
