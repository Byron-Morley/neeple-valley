package com.liquidpixel.main.ai.formation.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

public class FormationAvoidanceDemo extends ApplicationAdapter {
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;
    private static final float ENTITY_RADIUS = 20f;
    private static final float MAX_AVOIDANCE_RADIUS = 80f;
    private static final float MIN_AVOIDANCE_RADIUS = 30f;
    private static final int NUM_ENTITIES = 9;

    // Formation parameters
    private static final float GRID_SPACING = 80f;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture texture;
    private TextureRegion entityRegion;
    private BitmapFont font;

    private Array<MovingEntity> entities;
    private Array<PathPoint> formationPoints;
    private Vector2 formationCenter;
    private FormationType currentFormationType;
    private float formationRotation = 0f;
    private float formationScale = 1.0f;

    private boolean showDebug = true;
    private boolean resetEntities = false;
    private boolean isDraggingFormation = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 touchPos = new Vector2();

    // Formation types
    enum FormationType {
        GRID,
        CIRCLE,
        WEDGE,
        LINE
    }

    @Override
    public void create() {
        // Initialize rendering components
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        // Load textures
        texture = new Texture(Gdx.files.internal("assets/test/data/green_fish.png"));
        entityRegion = new TextureRegion(texture);

        // Initialize font
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        // Initialize formation center
        formationCenter = new Vector2(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        currentFormationType = FormationType.GRID;
        formationPoints = new Array<>();
        createFormationPoints();

        // Create entities
        entities = new Array<>();
        createEntities();

        // Set up input handling
        setupInputHandling();
    }

    private void createFormationPoints() {
        formationPoints.clear();

        switch (currentFormationType) {
            case GRID:
                createGridFormation();
                break;
            case CIRCLE:
                createCircleFormation();
                break;
            case WEDGE:
                createWedgeFormation();
                break;
            case LINE:
                createLineFormation();
                break;
        }
    }

    private void createGridFormation() {
        int rows = (int) Math.sqrt(NUM_ENTITIES);
        int cols = (int) Math.ceil((float) NUM_ENTITIES / rows);

        float startX = -(cols - 1) * GRID_SPACING / 2f;
        float startY = -(rows - 1) * GRID_SPACING / 2f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (formationPoints.size < NUM_ENTITIES) {
                    float x = startX + col * GRID_SPACING;
                    float y = startY + row * GRID_SPACING;

                    Vector2 rotatedPoint = rotatePoint(x, y, formationRotation);
                    formationPoints.add(new PathPoint(
                        formationCenter.x + rotatedPoint.x * formationScale,
                        formationCenter.y + rotatedPoint.y * formationScale
                    ));
                }
            }
        }
    }

    private void createCircleFormation() {
        float radius = GRID_SPACING * 1.5f * formationScale;
        float angleStep = 360f / NUM_ENTITIES;

        for (int i = 0; i < NUM_ENTITIES; i++) {
            float angle = i * angleStep + formationRotation;
            float x = formationCenter.x + MathUtils.cosDeg(angle) * radius;
            float y = formationCenter.y + MathUtils.sinDeg(angle) * radius;
            formationPoints.add(new PathPoint(x, y));
        }
    }

    private void createWedgeFormation() {
        float spacing = GRID_SPACING * formationScale;
        int rows = 5;  // Maximum rows in the wedge

        for (int row = 0; row < rows; row++) {
            int entitiesInRow = row + 1;
            if (formationPoints.size + entitiesInRow > NUM_ENTITIES) {
                entitiesInRow = NUM_ENTITIES - formationPoints.size;
            }

            if (entitiesInRow <= 0) break;

            float rowWidth = (entitiesInRow - 1) * spacing;
            float startX = -rowWidth / 2f;
            float y = -row * spacing * 0.866f;  // sqrt(3)/2 for equilateral triangle

            for (int col = 0; col < entitiesInRow; col++) {
                float x = startX + col * spacing;

                Vector2 rotatedPoint = rotatePoint(x, y, formationRotation);
                formationPoints.add(new PathPoint(
                    formationCenter.x + rotatedPoint.x,
                    formationCenter.y + rotatedPoint.y
                ));

                if (formationPoints.size >= NUM_ENTITIES) break;
            }
        }
    }

    private void createLineFormation() {
        float spacing = GRID_SPACING * formationScale;
        float startX = -(NUM_ENTITIES - 1) * spacing / 2f;

        for (int i = 0; i < NUM_ENTITIES; i++) {
            float x = startX + i * spacing;

            Vector2 rotatedPoint = rotatePoint(x, 0, formationRotation);
            formationPoints.add(new PathPoint(
                formationCenter.x + rotatedPoint.x,
                formationCenter.y + rotatedPoint.y
            ));
        }
    }

    private Vector2 rotatePoint(float x, float y, float degrees) {
        float cos = MathUtils.cosDeg(degrees);
        float sin = MathUtils.sinDeg(degrees);
        return new Vector2(
            x * cos - y * sin,
            x * sin + y * cos
        );
    }

    private void createEntities() {
        entities.clear();

        // Create entities at random positions
        for (int i = 0; i < NUM_ENTITIES; i++) {
            float x = MathUtils.random(100, WORLD_WIDTH - 100);
            float y = MathUtils.random(100, WORLD_HEIGHT - 100);

            MovingEntity entity = new MovingEntity(x, y, i);

            // Assign target formation point
            if (i < formationPoints.size) {
                PathPoint target = formationPoints.get(i);
                entity.targetPoint = new Vector2(target.x, target.y);
            }

            entities.add(entity);
        }
    }

    private void setupInputHandling() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.D) {
                    showDebug = !showDebug;
                    return true;
                }
                if (keycode == Keys.R) {
                    resetEntities = true;
                    return true;
                }
                if (keycode == Keys.G) {
                    currentFormationType = FormationType.GRID;
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.C) {
                    currentFormationType = FormationType.CIRCLE;
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.W) {
                    currentFormationType = FormationType.WEDGE;
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.L) {
                    currentFormationType = FormationType.LINE;
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.LEFT) {
                    formationRotation -= 15f;
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.RIGHT) {
                    formationRotation += 15f;
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.UP) {
                    formationScale = Math.min(formationScale + 0.1f, 2.0f);
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                if (keycode == Keys.DOWN) {
                    formationScale = Math.max(formationScale - 0.1f, 0.5f);
                    createFormationPoints();
                    updateFormation();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen coordinates to world coordinates
                viewport.unproject(touchPos.set(screenX, screenY));

                // Check if the touch is on the formation center
                if (touchPos.dst(formationCenter) < ENTITY_RADIUS * 2) {
                    isDraggingFormation = true;
                    dragStart.set(touchPos);
                    return true;
                }

                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isDraggingFormation) {
                    // Convert screen coordinates to world coordinates
                    viewport.unproject(touchPos.set(screenX, screenY));

                    // Update formation center
                    formationCenter.add(
                        touchPos.x - dragStart.x,
                        touchPos.y - dragStart.y
                    );

                    // Update drag start position
                    dragStart.set(touchPos);

                    // Update formation points
                    createFormationPoints();
                    updateFormation();

                    return true;
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                isDraggingFormation = false;
                return false;
            }
        });
    }

    private void updateFormation() {
        for (int i = 0; i < entities.size; i++) {
            if (i < formationPoints.size) {
                PathPoint target = formationPoints.get(i);
                entities.get(i).targetPoint.set(target.x, target.y);
            }
        }
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Reset entities if requested
        if (resetEntities) {
            createEntities();
            resetEntities = false;
        }

        // Update entities
        for (int i = 0; i < entities.size; i++) {
            MovingEntity entity = entities.get(i);

            entity.update(deltaTime, entities);
        }

        // Draw everything
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw formation outlines if debug is enabled
        if (showDebug) {
            switch (currentFormationType) {
                case GRID:
                    drawGridOutline();
                    break;
                case WEDGE:
                    drawWedgeOutline();
                    break;
                case LINE:
                    drawLineOutline();
                    break;
            }

            // Draw formation points
            drawFormationPoints();
        }

        // Draw entities
        batch.begin();
        for (MovingEntity entity : entities) {
            batch.draw(entityRegion,
                entity.position.x - ENTITY_RADIUS,
                entity.position.y - ENTITY_RADIUS,
                ENTITY_RADIUS, ENTITY_RADIUS,
                ENTITY_RADIUS * 2, ENTITY_RADIUS * 2,
                1, 1, entity.getRotationDegrees());
        }

        // Draw info text
        font.draw(batch, "Press D to toggle debug view", 10, WORLD_HEIGHT - 10);
        font.draw(batch, "Press R to reset entities", 10, WORLD_HEIGHT - 40);
        font.draw(batch, "Formation: " + currentFormationType.name(), 10, WORLD_HEIGHT - 70);
        font.draw(batch, "Press G/C/W/L to change formation", 10, WORLD_HEIGHT - 100);
        font.draw(batch, "Use LEFT/RIGHT to rotate", 10, WORLD_HEIGHT - 130);
        font.draw(batch, "Use UP/DOWN to scale", 10, WORLD_HEIGHT - 160);
        font.draw(batch, "Drag center point to move formation", 10, WORLD_HEIGHT - 190);
        batch.end();

        // Draw debug information
        if (showDebug) {
            drawDebugInfo();
        }
    }

    private void drawFormationPoints() {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);

        for (PathPoint point : formationPoints) {
            shapeRenderer.circle(point.x, point.y, 5);
        }

        // Draw formation center
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.circle(formationCenter.x, formationCenter.y, ENTITY_RADIUS);

        shapeRenderer.end();
    }

    private void drawGridOutline() {
        if (formationPoints.size < 4) return;

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);

        int rows = (int) Math.sqrt(NUM_ENTITIES);
        int cols = (int) Math.ceil((float) NUM_ENTITIES / rows);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - 1; col++) {
                int index = row * cols + col;
                int nextIndex = row * cols + col + 1;

                if (index < formationPoints.size && nextIndex < formationPoints.size) {
                    PathPoint p1 = formationPoints.get(index);
                    PathPoint p2 = formationPoints.get(nextIndex);
                    shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows - 1; row++) {
                int index = row * cols + col;
                int nextIndex = (row + 1) * cols + col;

                if (index < formationPoints.size && nextIndex < formationPoints.size) {
                    PathPoint p1 = formationPoints.get(index);
                    PathPoint p2 = formationPoints.get(nextIndex);
                    shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        shapeRenderer.end();
    }

    private void drawWedgeOutline() {
        if (formationPoints.size < 3) return;

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);

        int rows = 5;  // Maximum rows in the wedge
        int pointIndex = 0;

        for (int row = 0; row < rows; row++) {
            int entitiesInRow = row + 1;
            if (pointIndex + entitiesInRow > formationPoints.size) {
                entitiesInRow = formationPoints.size - pointIndex;
            }

            if (entitiesInRow <= 0) break;

            // Draw horizontal connections in this row
            for (int i = 0; i < entitiesInRow - 1; i++) {
                PathPoint p1 = formationPoints.get(pointIndex + i);
                PathPoint p2 = formationPoints.get(pointIndex + i + 1);
                shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
            }

            // Draw diagonal connections to the next row
            if (row < rows - 1 && pointIndex + entitiesInRow < formationPoints.size) {
                for (int i = 0; i < entitiesInRow; i++) {
                    PathPoint p1 = formationPoints.get(pointIndex + i);

                    // Connect to one or two points in the next row
                    if (i < entitiesInRow + 1) {
                        int nextRowIndex = pointIndex + entitiesInRow + i;
                        if (nextRowIndex < formationPoints.size) {
                            PathPoint p2 = formationPoints.get(nextRowIndex);
                            shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
                        }
                    }

                    if (i + 1 < entitiesInRow + 1) {
                        int nextRowIndex = pointIndex + entitiesInRow + i + 1;
                        if (nextRowIndex < formationPoints.size) {
                            PathPoint p2 = formationPoints.get(nextRowIndex);
                            shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                }
            }

            pointIndex += entitiesInRow;
        }

        shapeRenderer.end();
    }

    private void drawLineOutline() {
        if (formationPoints.size < 2) return;

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);

        for (int i = 0; i < formationPoints.size - 1; i++) {
            PathPoint p1 = formationPoints.get(i);
            PathPoint p2 = formationPoints.get(i + 1);
            shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
        }

        shapeRenderer.end();
    }

    private void drawDebugInfo() {
        shapeRenderer.begin(ShapeType.Line);

        // Draw avoidance radii
        for (MovingEntity entity : entities) {
            // Draw entity ID
            batch.begin();
            font.draw(batch, String.valueOf(entity.id), entity.position.x - 5, entity.position.y - ENTITY_RADIUS - 10);
            batch.end();

            // Draw current avoidance radius
            shapeRenderer.setColor(1, 1, 0, 0.5f);
            shapeRenderer.circle(entity.position.x, entity.position.y, entity.currentAvoidanceRadius);

            // Draw velocity vector
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.line(
                entity.position.x,
                entity.position.y,
                entity.position.x + entity.velocity.x * 0.5f,
                entity.position.y + entity.velocity.y * 0.5f
            );

            // Draw line to target point
            if (entity.targetPoint != null) {
                shapeRenderer.setColor(1, 0, 0, 0.5f);
                shapeRenderer.line(
                    entity.position.x,
                    entity.position.y,
                    entity.targetPoint.x,
                    entity.targetPoint.y
                );
            }
        }

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        texture.dispose();
        font.dispose();
    }

    // Helper classes

    public static class PathPoint {
        float x, y;

        public PathPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class MovingEntity {
        Vector2 position = new Vector2();
        Vector2 velocity = new Vector2();
        Vector2 steering = new Vector2();
        Vector2 avoidance = new Vector2();
        Vector2 targetPoint = new Vector2();
        float maxSpeed = 150f;
        float maxForce = 300f;
        int id;
        float currentAvoidanceRadius = MAX_AVOIDANCE_RADIUS;

        // Distance threshold for reducing avoidance radius
        private static final float DISTANCE_THRESHOLD = 200f;
        private static final float MIN_DISTANCE = 10f;

        public MovingEntity(float x, float y, int id) {
            this.position.set(x, y);
            this.id = id;
        }

        public void update(float deltaTime, Array<MovingEntity> entities) {
            // Calculate steering force for seeking target
            Vector2 desired = new Vector2(targetPoint).sub(position);
            float distanceToTarget = desired.len();

            // Calculate dynamic avoidance radius based on distance to target
            // As the entity gets closer to its target, reduce the avoidance radius
            if (distanceToTarget < DISTANCE_THRESHOLD) {
                float t = MathUtils.clamp((distanceToTarget - MIN_DISTANCE) / (DISTANCE_THRESHOLD - MIN_DISTANCE), 0, 1);
                currentAvoidanceRadius = MIN_AVOIDANCE_RADIUS + t * (MAX_AVOIDANCE_RADIUS - MIN_AVOIDANCE_RADIUS);
            } else {
                currentAvoidanceRadius = MAX_AVOIDANCE_RADIUS;
            }

            // Normalize and scale by max speed
            if (distanceToTarget > 0) {
                desired.nor().scl(maxSpeed);
            } else {
                desired.setZero();
            }

            // Calculate steering force
            steering.set(desired).sub(velocity);
            if (steering.len() > maxForce) {
                steering.nor().scl(maxForce);
            }

            // Calculate avoidance force
            calculateAvoidance(entities);

            // Apply forces
            velocity.add(steering.scl(deltaTime));
            velocity.add(avoidance.scl(deltaTime));

            // Limit speed
            if (velocity.len() > maxSpeed) {
                velocity.nor().scl(maxSpeed);
            }

            // Update position
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        }

        private void calculateAvoidance(Array<MovingEntity> entities) {
            avoidance.setZero();

            for (MovingEntity other : entities) {
                // Skip self
                if (other == this) continue;

                // Calculate distance to other entity
                float distance = position.dst(other.position);

                // If within avoidance radius, calculate avoidance force
                float combinedRadius = (currentAvoidanceRadius + other.currentAvoidanceRadius) / 2;
                if (distance < combinedRadius) {
                    // Get vector pointing away from other entity
                    Vector2 awayVector = new Vector2(position).sub(other.position);

                    // Normalize and scale inversely by distance
                    float avoidanceStrength = (combinedRadius - distance) / combinedRadius;
                    awayVector.nor().scl(maxForce * 2 * avoidanceStrength);

                    // Add to avoidance force
                    avoidance.add(awayVector);
                }
            }

            // Limit avoidance force
            if (avoidance.len() > maxForce * 1.5f) {
                avoidance.nor().scl(maxForce * 1.5f);
            }
        }

        public float getRotationDegrees() {
            if (velocity.len2() < 0.1f) return 0;
            return velocity.angleDeg();
        }
    }
}
