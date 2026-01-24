package com.liquidpixel.main.interfaces.work;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.main.components.ai.actions.WorkTaskComponent;
import com.liquidpixel.main.components.storage.StorageComponent;

public interface IWorkService {
    void createOutput(WorkTaskComponent workTaskComponent, Entity workshop);

    void removeInput(StorageComponent workshopStorageComponent, WorkTaskComponent workTaskComponent);

    void buildItem(WorkTaskComponent workTaskComponent, Entity workshop);
}
