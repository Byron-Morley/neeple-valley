package com.liquidpixel.main.ui.view.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.*;
import com.liquidpixel.main.interfaces.services.ICameraService;
import com.liquidpixel.main.interfaces.services.IItemService;
import com.liquidpixel.main.interfaces.services.ISelectionService;
import com.liquidpixel.main.interfaces.ui.IWindowService;
import com.liquidpixel.main.model.ui.MenuTreeItem;
import com.liquidpixel.main.ui.view.panels.CostUI;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.liquidpixel.selection.api.IClickBehaviorService;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.List;

import static com.liquidpixel.main.utils.ui.Windows.COST_PANEL;


public class MenuUI extends VisTable implements IGet<Group> {
    private static final float TOTAL_HEIGHT = 80f;
    private static final float PADDING = 10f;
    private SecondaryMenuBlock currentVisibleSubmenu = null;

    private final VisTable leftMenu;
    private final VisTable rightMenu;

    List<MenuTreeItem> menu;

    ISelectionService selectionService;
    ICameraService cameraService;
    IItemService itemService;
    IWorldMap worldMap;
    IWindowService windowService;
    IClickBehaviorService clickBehaviorService;
    ISpriteFactory spriteFactory;

    public MenuUI(
        ISelectionService selectionService,
        ICameraService cameraService,
        IItemService itemService,
        IWorldMap worldMap,
        IWindowService windowService,
        IClickBehaviorService clickBehaviorService,
        ISpriteFactory spriteFactory
    ) {
        this.itemService = itemService;
        this.clickBehaviorService = clickBehaviorService;
        this.selectionService = selectionService;
        this.cameraService = cameraService;
        this.worldMap = worldMap;
        this.windowService = windowService;
        this.spriteFactory = spriteFactory;
        menu = ModelFactory.getMenuTree();

        setFillParent(true);
        setVisible(true);
        bottom();

        VisTable bottomBar = new VisTable();
        bottomBar.setBackground(VisUI.getSkin().getDrawable("menu-panel"));
        leftMenu = new VisTable();
        rightMenu = new VisTable();

        for (MenuTreeItem menuItem : menu) {
            MenuItemUI menuItemUI = new MenuItemUI(
                spriteFactory,
                menuItem.getLabel(),
                menuItem.getIcon(),
                menuItem.getName());

            menuItemUI.setBackground(VisUI.getSkin().getDrawable("panel-hover"));
            menuItemUI.addListener(new ClickListener() {
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    menuItemUI.setHovered(true);
                }

                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    menuItemUI.setHovered(false);
                }
            });

            if (menuItem.getChildren() != null && !menuItem.getChildren().isEmpty()) {
                SecondaryMenuBlock subMenu = createSubMenu(menuItem.getChildren());
                menuItemUI.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // Hide previous submenu if different from current
                        if (currentVisibleSubmenu != null && currentVisibleSubmenu != subMenu) {
                            currentVisibleSubmenu.setVisible(false);
                            // Reset previous menu item color
                            for (Actor actor : leftMenu.getChildren()) {
                                if (actor instanceof MenuItemUI) {
                                    ((MenuItemUI) actor).setSelected(false);
                                }
                            }
                        }

                        subMenu.setVisible(!subMenu.isVisible());
                        menuItemUI.setSelected(subMenu.isVisible());
                        currentVisibleSubmenu = subMenu.isVisible() ? subMenu : null;
                    }
                });
                addActor(subMenu);
            } else {
                setupListener(menuItem, menuItemUI);
            }
            leftMenu.add(menuItemUI).size(64, 64).padRight(PADDING);
        }

        bottomBar.add(leftMenu).expandX().center();
        add(bottomBar)
            .height(TOTAL_HEIGHT)
            .expandX()
            .fillX();
    }

    private SecondaryMenuBlock createSubMenu(List<MenuTreeItem> children) {
        SecondaryMenuBlock subMenu = new SecondaryMenuBlock();
        VisTable leftSubMenu = subMenu.get();
        leftSubMenu.clear();

        for (MenuTreeItem child : children) {
            MenuItemUI childMenuItem = new MenuItemUI(spriteFactory, child.getLabel(), child.getIcon(), child.getName());

            childMenuItem.setBackground(VisUI.getSkin().getDrawable("panel"));
            childMenuItem.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    childMenuItem.setHovered(true);
                    showCostPanel(childMenuItem);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    childMenuItem.setHovered(false);
                    hideCostPanel();
                }
            });

            setupListener(child, childMenuItem);
            leftSubMenu.add(childMenuItem).size(64, 64).padRight(PADDING);
        }
        return subMenu;
    }

    private void showCostPanel(MenuItemUI childMenuItem) {
        CostUI costUI = windowService.getWindow(COST_PANEL);

        try {
            IRecipe recipe = itemService.getRecipe(childMenuItem.getItemName());
            costUI.addResourceItems(recipe.getInput());
        } catch (NullPointerException e) {
            System.out.println("Error: Recipe not found for item: " + childMenuItem.getItemName());
            e.printStackTrace();
        }

        if (
            childMenuItem.getLabel() != null &&
                childMenuItem.getItemName() != null
        ) {
            costUI.setTitle(childMenuItem.getLabel());

            float menuItemX = childMenuItem.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0)).x;
            float menuItemY = childMenuItem.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0)).y;

            float costPanelX = menuItemX + (childMenuItem.getWidth() / 2) - (costUI.getWidth() / 2);
            float costPanelY = menuItemY + childMenuItem.getHeight() + 5;

            costPanelX = Math.max(0, Math.min(costPanelX, com.badlogic.gdx.Gdx.graphics.getWidth() - costUI.getWidth()));

            costUI.setPosition(costPanelX, costPanelY);
            costUI.setVisible(true);
        }
    }


    private void hideCostPanel() {
        CostUI costUI = windowService.getWindow(COST_PANEL);
        costUI.setVisible(false);
    }

    private void setupListener(MenuTreeItem menuTreeItem, MenuItemUI menuItemUI) {
        if (menuTreeItem.getBehaviorId() != null) {
            menuTreeItem.setClickBehavior(clickBehaviorService.createMenuClickBehavior(menuTreeItem.getBehaviorId()));
        }

        menuItemUI.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuTreeItem.onClick();
            }
        });
    }

    @Override
    public Table get() {
        return this;
    }
}
