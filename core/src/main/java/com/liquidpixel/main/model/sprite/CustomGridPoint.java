package com.liquidpixel.main.model.sprite;

import com.badlogic.gdx.math.GridPoint2;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomGridPoint extends GridPoint2 {

    @JsonProperty
    private NodeType n = NodeType.TILE_WALL;

    public CustomGridPoint(GridPoint2 point, NodeType n) {
        super(point);
        this.n = n;
    }

    @JsonCreator
    public CustomGridPoint(
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("n") int n
    ) {
        super(new GridPoint2(x, y));
        this.n = NodeType.fromValue(n);
    }


    public CustomGridPoint(GridPoint2 point) {
        super(point);
    }

    public NodeType getN() {
        return n;
    }
}
