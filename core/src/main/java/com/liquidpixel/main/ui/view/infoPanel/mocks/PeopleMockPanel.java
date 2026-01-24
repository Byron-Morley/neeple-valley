package com.liquidpixel.main.ui.view.infoPanel.mocks;

import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.main.components.agent.WorkerComponent;
import com.liquidpixel.main.components.workshop.JobComponent;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.model.person.Person;
import com.liquidpixel.main.ui.UiTestScreen;
import com.liquidpixel.main.ui.view.infoPanel.people.PeoplePanelPresenter;
import com.kotcrab.vis.ui.widget.VisTable;
import com.badlogic.ashley.core.Entity;

import java.util.List;
import java.util.stream.Collectors;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class PeopleMockPanel implements UiTestScreen.TestUITab, Updatable {

    PeoplePanelPresenter presenter;

    @Override
    public String getTabTitle() {
        return "PeopleMockPanel";
    }

    @Override
    public void setupUI(VisTable contentTable) {
        VisTable container = new VisTable();
        container.debugAll();
        container.setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        container.defaults().pad(10, 5, 10, 5);

        // Create presenter
        presenter = new PeoplePanelPresenter();
        presenter.setEntity(mockData());

        container.row().fill().expandX().expandY();
        container.add(presenter).width(400 / UI_SCALE).height(500 / UI_SCALE);

        contentTable.add(container).grow();
    }

    private Entity mockData() {
        Entity entity = new Entity();
        JobComponent jobComponent = new JobComponent(3);
        entity.add(jobComponent);
        entity.add(new PositionComponent(100, 100));

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

    @Override
    public void update() {

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

}
