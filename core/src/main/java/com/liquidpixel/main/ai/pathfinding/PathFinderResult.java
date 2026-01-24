package com.liquidpixel.main.ai.pathfinding;


public class PathFinderResult {
    TiledSmoothableGraphPath<FlatTiledNode> path;


    private boolean found;
    private FlatTiledNode startNode;
    private FlatTiledNode endNode;
    private float totalCost;

    public PathFinderResult(TiledSmoothableGraphPath<FlatTiledNode> path, boolean found) {
        this.path = path;
        this.found = found;
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return path;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        sb.append(found ? "Found" : "Not Found");
        sb.append("\n");

        for (int i = 0; i < path.getCount(); i++) {
            sb.append(path.getNodePosition(i));
            sb.append(" ");
        }
        return sb.toString();
    }

    public boolean isFound() {
        return found;
    }

    public FlatTiledNode getStartNode() {
        return startNode;
    }

    public void setStartNode(FlatTiledNode startNode) {
        this.startNode = startNode;
    }

    public FlatTiledNode getEndNode() {
        return endNode;
    }

    public void setEndNode(FlatTiledNode endNode) {
        this.endNode = endNode;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }
}
