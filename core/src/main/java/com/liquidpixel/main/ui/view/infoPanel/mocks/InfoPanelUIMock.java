package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.components.storage.StorageComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IStorageItem;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.model.item.StorageItem;
import com.liquidpixel.main.model.person.Person;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.ui.view.infoPanel.InfoPanelUIPresenter;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.List;
import java.util.stream.Collectors;

import static com.badlogic.gdx.math.MathUtils.random;

public class InfoPanelUIMock extends ReuseableWindow implements Updatable {

    InfoPanelUIPresenter presenter;

    public InfoPanelUIMock() {
        super("Info Panel Mock");
        init();
    }

    public void init() {
        setVisible(true);
        VisTable container = new VisTable();
//        container.debugAll();
        container.setPosition(0,0);
        container.defaults().pad(10, 5, 10, 5);
        addCloseButton();
        // Create presenter
        presenter = new InfoPanelUIPresenter(mockData());
        mockData();

        container.row().fill().expandX().expandY();
        container.add(presenter);
        add(container).grow();
    }

    private StorageComponent mockStorageComponent() {
        StorageComponent storageComponent = new StorageComponent(4);

        IStorageItem wood = getItem("wood", 30);
        IStorageItem iron = getItem("iron", 30);
        storageComponent.addItem(wood);
        storageComponent.addItem(iron);

        return storageComponent;
    }

    private IStorageItem getItem(String name, int quantity) {
        System.out.println("getItem: " + name);
        Item item = ModelFactory.getItemsModel().get("test/" + name);
        GameSprite sprite = null;
        return new StorageItem(name, quantity, item.getStackSize(), sprite);
    }

      private Entity mockData() {
        Entity entity = new Entity();
        JobComponent jobComponent = new JobComponent(3);
        entity.add(jobComponent);
        entity.add(new PositionComponent(100, 100));
        entity.add(mockStorageComponent());

        Entity worker = new Entity();
        Person person = getRandomPersonByGender("m");
        worker.add(new WorkerComponent(new Person(person)));

        Entity worker1 = new Entity();
        Person person1 = getRandomPersonByGender("m");
        worker1.add(new WorkerComponent(new Person(person1)));

        Entity worker2 = new Entity();
        Person person2 = getRandomPersonByGender("m");
        worker2.add(new WorkerComponent(new Person(person2)));
        worker2.add(new PositionComponent(100, 100));

        jobComponent.addWorker(worker, entity);
        jobComponent.addWorker(worker1, entity);
        jobComponent.addWorker(worker2, entity);
        return entity;
    }

    public Person getRandomPersonByGender(String gender) {

        List<Person> people = ModelFactory.getPeopleData();

        List<Person> filteredPeople = people.stream()
            .filter(person -> gender.equalsIgnoreCase(person.getGender()))
            .collect(Collectors.toList());

        if (filteredPeople.isEmpty()) {
            return null; // Or throw an exception, or return a default person
        }

        return filteredPeople.get(random.nextInt(filteredPeople.size()));
    }

    @Override
    public void update() {
        presenter.update();
    }
}
