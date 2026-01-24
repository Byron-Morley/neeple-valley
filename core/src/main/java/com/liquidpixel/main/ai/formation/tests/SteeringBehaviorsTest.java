

package com.liquidpixel.main.ai.formation.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.main.ai.formation.tests.steer.SteeringTestBase;
import com.liquidpixel.main.ai.formation.tests.steer.scene2d.formation.Scene2dFormationTest;
import com.liquidpixel.main.ai.formation.tests.utils.scene2d.CollapsableWindow;
import com.liquidpixel.main.ai.formation.tests.utils.scene2d.FpsLabel;
import com.liquidpixel.main.ai.formation.tests.utils.scene2d.PauseButton;
import com.liquidpixel.main.ai.formation.tests.utils.scene2d.TabbedPane;

public class SteeringBehaviorsTest extends ApplicationAdapter {

	private static final boolean DEBUG_STAGE = false;

	private static final String[] ENGINES = {"Scene2d", "Box2d", "Bullet"};

	private CollapsableWindow testSelectionWindow;
	Label testHelpLabel;
	TextButton pauseButton;

	private SteeringTestBase[][] tests = {
		{
			new Scene2dFormationTest(this, false),
		}
	};
	// @on - enable libgdx formatter

	SteeringTestBase currentTest;

	public Stage stage;
	public float stageWidth;
	public float stageHeight;
	public Skin skin;

	public TextureRegion greenFish;
	public TextureRegion cloud;
	public TextureRegion badlogicSmall;
	public TextureRegion target;

	@Override
	public void create () {
		Gdx.gl.glClearColor(.3f, .3f, .3f, 1);

		greenFish = new TextureRegion(new Texture(Gdx.files.internal("assets/test/data/green_fish.png")));
		cloud = new TextureRegion(new Texture(Gdx.files.internal("assets/test/data/particle-cloud.png")));
		badlogicSmall = new TextureRegion(new Texture(Gdx.files.internal("assets/test/data/badlogicsmall.jpg")));
		target = new TextureRegion(new Texture(Gdx.files.internal("assets/test/data/target.png")));
//        skin = new Skin(Gdx.files.internal("assets/ui/skin/golden-gate/skin.json"));
		skin = new Skin(Gdx.files.internal("assets/test/data/uiskin.json"));

		stage = new Stage();
		stage.setDebugAll(DEBUG_STAGE);
		stageWidth = stage.getWidth();
		stageHeight = stage.getHeight();

		Gdx.input.setInputProcessor(new InputMultiplexer(stage));

		// Add translucent panel (it's only visible when AI is paused)
		final Image translucentPanel = new Image(skin, "window-bg");
		translucentPanel.setSize(stageWidth, stageHeight);
		translucentPanel.setVisible(false);
		stage.addActor(translucentPanel);

		// Create test selection window
		Array<List<String>> engineTests = new Array<List<String>>();
		for (int k = 0; k < tests.length; k++) {
			engineTests.add(createTestList(k));
		}
		testSelectionWindow = addTestSelectionWindow("Behaviors", ENGINES, engineTests, 0, -1);

		// Create status bar
		Table statusBar = new Table(skin);
		statusBar.left().bottom();
		statusBar.row().height(26);
		statusBar.add(pauseButton = new PauseButton(translucentPanel, skin)).width(90).left();
		statusBar.add(new FpsLabel("FPS: ", skin)).padLeft(15);
		statusBar.add(testHelpLabel = new Label("", skin)).padLeft(15);
		stage.addActor(statusBar);

		// Set selected behavior
		changeTest(0, 0);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Render current steering behavior test
		if (currentTest != null) {
			if (!pauseButton.isChecked()) {
				// Update AI time
				GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());

				// Update test
				currentTest.update();
			}
			// Draw test
			currentTest.draw();
		}

		stage.act();
		stage.draw();
	}

	@Override
	public void resize (int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
		stageWidth = width;
		stageHeight = height;
	}

	@Override
	public void dispose () {
		if (currentTest != null) currentTest.dispose();

		stage.dispose();
		skin.dispose();

		// Dispose textures
		greenFish.getTexture().dispose();
		cloud.getTexture().dispose();
		badlogicSmall.getTexture().dispose();
		target.getTexture().dispose();
	}

	private List<String> createTestList (final int engineIndex) {
		// Create test names
		int numTests = tests[engineIndex].length;
		String[] testNames = new String[numTests];
		for (int i = 0; i < numTests; i++) {
			testNames[i] = tests[engineIndex][i].testName;
		}

		final List<String> testList = new List<String>(skin);
		testList.setItems(testNames);
		testList.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (!testSelectionWindow.isCollapsed() && getTapCount() == 2) {
					changeTest(engineIndex, testList.getSelectedIndex());
					testSelectionWindow.collapse();
				}
			}
		});
		return testList;
	}

	private CollapsableWindow addTestSelectionWindow (String title, String[] tabTitles, Array<List<String>> tabLists,
		float x, float y) {
//		if (tabTitles.length != tabLists.size)
//			throw new IllegalArgumentException("tabTitles and tabList must have the same size.");
		CollapsableWindow window = new CollapsableWindow(title, skin);
		window.row();
		TabbedPane tabbedPane = new TabbedPane(skin);
		for (int i = 0; i < tabLists.size; i++) {
			ScrollPane pane = new ScrollPane(tabLists.get(i), skin);
			pane.setFadeScrollBars(false);
			pane.setScrollX(0);
			pane.setScrollY(0);

			tabbedPane.addTab(" " + tabTitles[i] + " ", pane);
		}
		window.add(tabbedPane);
		window.pack();
		window.pack();
		if (window.getHeight() > stage.getHeight()) {
			window.setHeight(stage.getHeight());
		}
		window.setX(x < 0 ? stage.getWidth() - (window.getWidth() - (x + 1)) : x);
		window.setY(y < 0 ? stage.getHeight() - (window.getHeight() - (y + 1)) : y);
		window.layout();
		window.collapse();
		stage.addActor(window);

		return window;
	}

	private void changeTest (int engineIndex, int testIndex) {
		// Remove the old test and its window
		if (currentTest != null) {
			if (currentTest.getDetailWindow() != null) currentTest.getDetailWindow().remove();
			currentTest.dispose();
		}

		// Add the new test and its window
		currentTest = tests[engineIndex][testIndex];
		currentTest.create();
		testHelpLabel.setText(currentTest.getHelpMessage());
		InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
		if (im.size() > 1) im.removeProcessor(1);
		if (currentTest.getInputProcessor() != null) im.addProcessor(currentTest.getInputProcessor());
		if (currentTest.getDetailWindow() != null) stage.addActor(currentTest.getDetailWindow());
	}

}
