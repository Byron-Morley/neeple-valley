package com.liquidpixel.main.ui.view.agent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.liquidpixel.main.interfaces.IGet;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.Updatable;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.List;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class AgentUI extends ReuseableWindow implements IGet<Group>, Updatable {

    IAgentService agentService;
    AgentUIPresenter presenter;

    public AgentUI(IAgentService agentService) {
        super("Agents");
        this.agentService = agentService;
        setVisible(false);
        addCloseButton();
        presenter = new AgentUIPresenter();

        setPosition(250 / UI_SCALE, 500 / UI_SCALE);
        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        add(presenter).width(500 / UI_SCALE).height(600 / UI_SCALE);
        pack();
        updateContent();
    }

    public void updateContent() {
        List<Entity> agents = agentService.getAllAgents();
        presenter.setAgents(agents);
        pack();
    }

    @Override
    public VisWindow get() {
        return this;
    }

    @Override
    public void update() {
        updateContent();
    }
}
