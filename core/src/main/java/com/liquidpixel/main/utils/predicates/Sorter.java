package com.liquidpixel.main.utils.predicates;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.utils.Mappers;

public class Sorter {
    Vector2 origin;


    public Sorter(Vector2 origin) {
        this.origin = origin;
    }

    public int sortByPriority(Entity agent1, Entity agent2) {
        StorageComponent storageComponent1 = Mappers.storage.get(agent1);
        StorageComponent storageComponent2 = Mappers.storage.get(agent2);

        if (storageComponent1.getPriority() > storageComponent2.getPriority()) {
            return -1;
        } else {
            return 1;
        }
    }


    public int sortByCloseness(Entity agent1, Entity agent2) {
        Vector2 d1 = Mappers.position.get(agent1).getPosition();
        Vector2 d2 = Mappers.position.get(agent2).getPosition();
        return sortByCloseness(d1, d2);
    }

    public int sortByCloseness(GridPoint2 d1, GridPoint2 d2) {
        return sortByCloseness(new Vector2(d1.x, d1.y), new Vector2(d2.x, d2.y));
    }

    public int sortByCloseness(Vector2 d1, Vector2 d2) {
        float diff = d1.dst(origin) - d2.dst(origin);

        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
