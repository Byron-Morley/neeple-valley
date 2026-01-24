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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.InputAdapter;

public class CollisionAvoidanceDemo extends ApplicationAdapter {
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;
    private static final float ENTITY_RADIUS = 20f;
    private static final float AVOIDANCE_RADIUS = 80f;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture texture;
    private TextureRegion entityRegion;
    private BitmapFont font;

    private Array<MovingEntity> entities;
    private Array<PathPoint> path1;
    private Array<PathPoint> path2;

    private boolean showDebug = true;
    private boolean resetEntities = false;

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

        // Create entities
        entities = new Array<>();
        entities.add(new MovingEntity(100, 100, 0));
        entities.add(new MovingEntity(700, 500, 1));

        // Create paths
        createPaths();

        // Set up input handling
        setupInputHandling();
    }

    private void createPaths() {
        // Create first path - from top-left to bottom-right
        path1 = new Array<>();
        path1.add(new PathPoint(100, 100));
        path1.add(new PathPoint(300, 300));
        path1.add(new PathPoint(500, 300));
        path1.add(new PathPoint(700, 500));

        // Create second path - from bottom-right to top-left
        path2 = new Array<>();
        path2.add(new PathPoint(700, 500));
        path2.add(new PathPoint(500, 300));
        path2.add(new PathPoint(300, 300));
        path2.add(new PathPoint(100, 100));

        // Assign paths to entities
        entities.get(0).setPath(path1);
        entities.get(1).setPath(path2);
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
                return false;
            }
        });
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Reset entities if requested
        if (resetEntities) {
            entities.get(0).reset(100, 100);
            entities.get(1).reset(700, 500);
            resetEntities = false;
        }

        // Update entities
        for (MovingEntity entity : entities) {

            entity.update(deltaTime, entities);
        }

        // Draw everything
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw paths
        if (showDebug) {
            drawPaths();
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
        batch.end();

        // Draw debug information
        if (showDebug) {
            drawDebugInfo();
        }
    }

    private void drawPaths() {
        shapeRenderer.begin(ShapeType.Line);

        // Draw first path
        shapeRenderer.setColor(0, 1, 0, 1);
        for (int i = 0; i < path1.size - 1; i++) {
            PathPoint p1 = path1.get(i);
            PathPoint p2 = path1.get(i + 1);
            shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
        }

        // Draw second path
        shapeRenderer.setColor(0, 0, 1, 1);
        for (int i = 0; i < path2.size - 1; i++) {
            PathPoint p1 = path2.get(i);
            PathPoint p2 = path2.get(i + 1);
            shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
        }

        shapeRenderer.end();
    }

    private void drawDebugInfo() {
        shapeRenderer.begin(ShapeType.Line);

        // Draw avoidance radii
        for (MovingEntity entity : entities) {
            if (entity.id == 0) {
                shapeRenderer.setColor(0, 1, 0, 0.5f);
            } else {
                shapeRenderer.setColor(0, 0, 1, 0.5f);
            }
            shapeRenderer.circle(entity.position.x, entity.position.y, AVOIDANCE_RADIUS);

            // Draw velocity vector
            shapeRenderer.setColor(1, 1, 0, 1);
            shapeRenderer.line(
                entity.position.x,
                entity.position.y,
                entity.position.x + entity.velocity.x,
                entity.position.y + entity.velocity.y
            );

            // Draw current target
            if (entity.currentPathIndex < entity.path.size) {
                PathPoint target = entity.path.get(entity.currentPathIndex);
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.circle(target.x, target.y, 5);
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
        Vector2 desired = new Vector2();
        Array<PathPoint> path;
        int currentPathIndex = 0;
        float maxSpeed = 100f;
        float maxForce = 200f;
        int id;

        public MovingEntity(float x, float y, int id) {
            this.position.set(x, y);
            this.id = id;
        }

        public void setPath(Array<PathPoint> path) {
            this.path = path;
            this.currentPathIndex = 0;
        }

        public void reset(float x, float y) {
            position.set(x, y);
            velocity.setZero();
            steering.setZero();
            avoidance.setZero();
            currentPathIndex = 0;
        }

        public void update(float deltaTime, Array<MovingEntity> entities) {
            // Skip if we reached the end of the path
            if (currentPathIndex >= path.size) {
                return;
            }

            // Get current target
            PathPoint target = path.get(currentPathIndex);

            // Calculate desired velocity (path following)
            desired.set(target.x - position.x, target.y - position.y);
            float distanceToTarget = desired.len();

            // If we're close enough to the current target, move to the next one
            if (distanceToTarget < ENTITY_RADIUS * 2) {
                currentPathIndex++;
                if (currentPathIndex < path.size) {
                    target = path.get(currentPathIndex);
                    desired.set(target.x - position.x, target.y - position.y);
                    distanceToTarget = desired.len();
                } else {
                    velocity.setZero();
                    return;
                }
            }

            // Normalize and scale by max speed
            desired.nor().scl(maxSpeed);

            // Calculate steering force for path following
            steering.set(desired).sub(velocity);
            if (steering.len() > maxForce) {
                steering.nor().scl(maxForce);
            }

            // Calculate avoidance force
            calculateAvoidance(entities);

            // Apply steering force
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

         for (int i = 0; i < entities.size; i++) {
           MovingEntity other = entities.get(i);

                // Skip self
                if (other == this) continue;

                // Calculate distance to other entity
                float distance = position.dst(other.position);

                // If within avoidance radius, calculate avoidance force
                if (distance < AVOIDANCE_RADIUS) {
                    // Get vector pointing away from other entity
                    Vector2 awayVector = new Vector2(position).sub(other.position);

                    // Normalize and scale inversely by distance
                    float avoidanceStrength = (AVOIDANCE_RADIUS - distance) / AVOIDANCE_RADIUS;
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
