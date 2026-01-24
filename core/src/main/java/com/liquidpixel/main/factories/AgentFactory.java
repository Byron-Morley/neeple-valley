package com.liquidpixel.main.factories;

import com.liquidpixel.main.builder.AgentBuilder;
import com.liquidpixel.main.dto.agent.Agent;
import com.liquidpixel.main.model.person.Person;
import com.liquidpixel.sprite.api.factory.IAnimationFactory;
import com.liquidpixel.sprite.api.models.IAnimationDefinition;
import com.liquidpixel.sprite.api.factory.ISpriteComponentFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AgentFactory {

    IAnimationFactory animationFactory;
    ISpriteComponentFactory spriteComponentFactory;

    private Map<String, Agent> agents;
    private int agentId = 0;
    private List<Person> people;
    Random random = new Random();

    public AgentFactory(ISpriteComponentFactory spriteComponentFactory, IAnimationFactory animationFactory) {
        this.agents = ModelFactory.getAgentsModel();
        this.people = ModelFactory.getPeopleData();
        this.animationFactory = animationFactory;
        this.spriteComponentFactory = spriteComponentFactory;
    }

    public AgentBuilder create(String type) {
        Agent agent = agents.get(type);
        String id = type + "_" + ++agentId;

        AgentBuilder builder = new AgentBuilder(id, spriteComponentFactory);

        if (agent.getVelocity() != 0) {
            builder.withVelocity(agent.getVelocity());
        }

        if (agent.hasAnimations()) {
            IAnimationDefinition animationDefinition = animationFactory.get(agent.getAnimationModel());
            builder.withAnimations(animationDefinition, id);
        }

        if (agent.isCamera()) {
            builder.isCamera();
        }

        if (agent.isPlayer()) {
            builder.withPlayerControls();
        }

        if (agent.isSettlement()) {
            builder.withSettlement(agent.getSettlement());
        }

        if(agent.getLayers()!=null){
            builder.withLayers(agent.getLayers());
        }

        if (agent.getBodyModel() != null) {
            builder.withBody(agent.getBodyModel());
        }

        if (agent.isSelectable()) builder.makeSelectable();

        if (agent.hasBehavior()) builder.withAiBehavior(agent.getBehavior());

        if (agent.isWorker()) {
            Person person = getRandomPersonByGender("m");
            builder.withWorker(new Person(person));
            builder.withStorage(1);
            builder.withEquipment();
        }

        return builder;
    }

    public AgentBuilder getGhostAgent(String name) {
        Agent agent = agents.get(name);
        String id = name + "_" + ++agentId;

        AgentBuilder builder = new AgentBuilder(id, spriteComponentFactory);

        return builder;
    }

    /**
     * Gets a random person with the specified gender.
     *
     * @param gender The gender to filter by (e.g., "m" for male, "f" for female)
     * @return A random Person of the specified gender, or null if none found
     */
    public Person getRandomPersonByGender(String gender) {
        List<Person> filteredPeople = this.people.stream()
            .filter(person -> gender.equalsIgnoreCase(person.getGender()))
            .collect(Collectors.toList());

        if (filteredPeople.isEmpty()) {
            return null; // Or throw an exception, or return a default person
        }

        return filteredPeople.get(random.nextInt(filteredPeople.size()));
    }

    public Map<String, Agent> getAgents() {
        return agents;
    }

}
