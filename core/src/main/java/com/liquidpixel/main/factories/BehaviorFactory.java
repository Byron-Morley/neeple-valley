package com.liquidpixel.main.factories;

import com.badlogic.gdx.files.FileHandle;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BehaviorFactory {
    Map<String, FileHandle> behaviors;

    public BehaviorFactory() {

        Set<String> files = ModelFactory.getBehaviorTrees();

        this.behaviors = files.stream()
            .map(ModelFactory::getBehaviorTree)
            .collect(Collectors.toMap(FileHandle::nameWithoutExtension, f -> f));
    }

    public FileHandle get(String name) {
        return this.behaviors.get(name);
    }
}
