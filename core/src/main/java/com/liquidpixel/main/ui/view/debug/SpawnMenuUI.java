package com.liquidpixel.main.ui.view.debug;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.dto.agent.Agent;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.IAgentService;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.listeners.ScrollPaneListener;
import com.liquidpixel.main.listeners.spawnMenu.SpawnAgentClickListener;
import com.liquidpixel.main.listeners.spawnMenu.SpawnItemClickListener;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.ui.common.ReuseableWindow;
import com.liquidpixel.main.ui.common.WindowUI;
import com.liquidpixel.main.ui.view.common.ScrollPanelUI;
import com.kotcrab.vis.ui.widget.*;

import java.util.ArrayList;
import java.util.Map;

import static com.liquidpixel.main.screens.WorldScreen.UI_SCALE;

public class SpawnMenuUI extends ReuseableWindow implements IGet<Group> {

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    IAgentService agentService;
    Tree tree;

    class Node extends Tree.Node<Node, String, VisTextButton> {
        public Node(String text, ClickListener clickListener) {
            super(new VisTextButton(text, "menu"));
            setValue(text);
            this.getActor().addListener(clickListener);
        }
    }

    class MenuNode extends Tree.Node<Node, String, VisTextButton> {
        public MenuNode(String text) {
            super(new VisTextButton(text, "menu"));
            setValue(text);
        }
    }

    public SpawnMenuUI(
        IItemService itemService,
        IAgentService agentService,
        ISelectionService selectionService,
        ICameraService cameraService
    ) {
        super("Spawn Menu");
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.itemService = itemService;
        this.agentService = agentService;
//        this.debug();
        this.setVisible(true);
        addCloseButton();
        //TOP
        Label subtitleLabel = new VisLabel("Items:");

        Table labelTable = new VisTable();
        labelTable.add(subtitleLabel).expand().left();
        labelTable.row();


        //MIDDLE
        tree = new VisTree();
        tree.setPadding(10);
        tree.setIndentSpacing(10);
        tree.setIconSpacing(5, 0);


        buildItemTree();
        buildAgentTree();


        ScrollPane scrollPane = new ScrollPanelUI(tree);


        Table listPanel = new VisTable();
        listPanel.add(scrollPane).grow().padBottom(12).padRight(0);


        //WINDOW
        Table containerTable = new VisTable();
        containerTable.add(labelTable).growX().height(15);
        containerTable.row();
        containerTable.add(listPanel).growX().growY();


        setPosition(0, 500 / UI_SCALE);
        defaults().pad(10, 5, 10, 5);
        row().fill().expandX().expandY();
        add(containerTable).width(200 / UI_SCALE).height(300 / UI_SCALE);

        pack();
    }

    private void buildAgentTree() {
        Map<String, Agent> agents = agentService.getAgentFactory().getAgents();
        MenuNode menuNode = new MenuNode("agents");
        tree.add(menuNode);

        ArrayList<String> excludeList = new ArrayList<>();
        excludeList.add("camera");
        excludeList.add("player");

        for (Map.Entry<String, Agent> entry : agents.entrySet()) {
            String key = entry.getKey();
            if (!excludeList.contains(key)) {
                Agent agent = entry.getValue();
                ClickListener clickListener = new SpawnAgentClickListener(key, selectionService, cameraService, agentService);
                menuNode.add(new Node(key, clickListener));
            }
        }
    }

    private void buildItemTree() {
        Map<String, Item> items = itemService.getModels();

        ArrayList<String> menusAdded = new ArrayList<>();

        for (Map.Entry<String, Item> entry : items.entrySet()) {
            String key = entry.getKey();
            Item item = entry.getValue();

            String[] parts = key.split("/");
            String menu = parts[0];
            ClickListener clickListener = new SpawnItemClickListener(key, selectionService, cameraService, itemService);
            MenuNode menuNode;
            if (menusAdded.contains(menu)) {
                menuNode = (MenuNode) tree.findNode(menu);
            } else {
                menusAdded.add(menu);
                menuNode = new MenuNode(menu);
                tree.add(menuNode);
            }

            menuNode.add(new Node(parts[1], clickListener));
        }
    }


    @Override
    public VisWindow get() {
        return this;
    }
}
