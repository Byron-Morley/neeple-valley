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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.InputAdapter;

public class FormationDemo extends ApplicationAdapter {
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture texture;
    private TextureRegion entityRegion;
    private TextureRegion leaderRegion;
    private BitmapFont font;

    private FormationLeader leader;
    private Array<FormationMember> members;

    private Vector2 targetPosition = new Vector2();

    // Formation types
    private enum FormationType {
        CIRCLE,
        V_SHAPE,
        LINE,
        SQUARE,
        RANDOM
    }

    private FormationType currentFormation = FormationType.CIRCLE;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        // Create textures
        texture = new Texture(Gdx.files.internal("assets/test/data/green_fish.png"));
        entityRegion = new TextureRegion(texture, 0, 0, 32, 32);
        leaderRegion = new TextureRegion(texture, 32, 0, 32, 32);

        // Create leader
        leader = new FormationLeader(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);

        // Create formation members
        members = new Array<>();
        int memberCount = 16;
        for (int i = 0; i < memberCount; i++) {
            FormationMember member = new FormationMember(
                    MathUtils.random(0, WORLD_WIDTH),
                    MathUtils.random(0, WORLD_HEIGHT));
            members.add(member);
        }

        // Set initial target
        targetPosition.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);

        // Setup input processing
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldPos = viewport.unproject(new Vector2(screenX, screenY));
                targetPosition.set(worldPos);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch(keycode) {
                    case Keys.NUM_1:
                        currentFormation = FormationType.CIRCLE;
                        return true;
                    case Keys.NUM_2:
                        currentFormation = FormationType.V_SHAPE;
                        return true;
                    case Keys.NUM_3:
                        currentFormation = FormationType.LINE;
                        return true;
                    case Keys.NUM_4:
                        currentFormation = FormationType.SQUARE;
                        return true;
                    case Keys.NUM_5:
                        currentFormation = FormationType.RANDOM;
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render() {
        // Update logic
        float deltaTime = Gdx.graphics.getDeltaTime();
        updateFormation(deltaTime);

        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw entities
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw leader
        batch.draw(leaderRegion,
                leader.position.x - leaderRegion.getRegionWidth() / 2,
                leader.position.y - leaderRegion.getRegionHeight() / 2);

        // Draw members
        for (FormationMember member : members) {
            batch.draw(entityRegion,
                    member.position.x - entityRegion.getRegionWidth() / 2,
                    member.position.y - entityRegion.getRegionHeight() / 2);
        }

        // Draw UI text
        font.draw(batch, "Current Formation: " + currentFormation.toString(), 10, WORLD_HEIGHT - 10);
        font.draw(batch, "Press 1-5 to change formation", 10, WORLD_HEIGHT - 40);
        font.draw(batch, "Click to set target", 10, WORLD_HEIGHT - 70);

        batch.end();
    }

    private void updateFormation(float deltaTime) {
        // Update leader to move toward target
        leader.update(targetPosition, deltaTime);

        // Update formation slots
        updateFormationSlots();

        // Update members to move toward their assigned slots
        for (FormationMember member : members) {
            member.update(deltaTime);
        }
    }

    private void updateFormationSlots() {
        switch (currentFormation) {
            case CIRCLE:
                updateCircleFormation();
                break;
            case V_SHAPE:
                updateVShapeFormation();
                break;
            case LINE:
                updateLineFormation();
                break;
            case SQUARE:
                updateSquareFormation();
                break;
            case RANDOM:
                updateRandomFormation();
                break;
        }
    }

    private void updateCircleFormation() {
        float radius = 100f;
        float angleStep = 360f / members.size;

        for (int i = 0; i < members.size; i++) {
            FormationMember member = members.get(i);

            // Calculate formation slot position
            float angle = i * angleStep * MathUtils.degreesToRadians;
            float x = leader.position.x + MathUtils.cos(angle) * radius;
            float y = leader.position.y + MathUtils.sin(angle) * radius;

            member.targetPosition.set(x, y);
        }
    }

    private void updateVShapeFormation() {
        float spacing = 40f;
        float depth = 50f;

        // Determine the center member index (apex of the V)
        int centerIndex = members.size / 2;

        for (int i = 0; i < members.size; i++) {
            FormationMember member = members.get(i);

            float x, y;
            if (i <= centerIndex) {
                // Left side of V
                int positionFromCenter = centerIndex - i;
                x = leader.position.x - (positionFromCenter * spacing);
                y = leader.position.y - (positionFromCenter * depth);
            } else {
                // Right side of V
                int positionFromCenter = i - centerIndex;
                x = leader.position.x + (positionFromCenter * spacing);
                y = leader.position.y - (positionFromCenter * depth);
            }

            member.targetPosition.set(x, y);
        }
    }

    private void updateLineFormation() {
        float spacing = 50f;

        // Place members in a horizontal line behind the leader
        Vector2 direction = new Vector2(leader.velocity).nor();
        Vector2 perpendicular = new Vector2(-direction.y, direction.x);

        float lineWidth = (members.size - 1) * spacing;
        float startX = leader.position.x - (perpendicular.x * lineWidth / 2);
        float startY = leader.position.y - (perpendicular.y * lineWidth / 2);

        // Place the leader at the center of the line
        for (int i = 0; i < members.size; i++) {
            FormationMember member = members.get(i);

            float x = startX + (perpendicular.x * i * spacing);
            float y = startY + (perpendicular.y * i * spacing);

            // Set the member's target position
            member.targetPosition.set(x, y);
        }
    }

    private void updateSquareFormation() {
        int membersPerSide = (int)Math.ceil(Math.sqrt(members.size));
        float spacing = 60f;

        float startX = leader.position.x - (spacing * (membersPerSide - 1) / 2);
        float startY = leader.position.y - (spacing * (membersPerSide - 1) / 2);

        for (int i = 0; i < members.size; i++) {
            FormationMember member = members.get(i);

            int row = i / membersPerSide;
            int col = i % membersPerSide;

            float x = startX + (col * spacing);
            float y = startY + (row * spacing);

            member.targetPosition.set(x, y);
        }
    }

    private void updateRandomFormation() {
        float maxRadius = 150f;

        for (int i = 0; i < members.size; i++) {
            FormationMember member = members.get(i);

            // Generate random position around leader
            float angle = MathUtils.random(0, MathUtils.PI2);
            float radius = MathUtils.random(50f, maxRadius);

            float x = leader.position.x + MathUtils.cos(angle) * radius;
            float y = leader.position.y + MathUtils.sin(angle) * radius;

            member.targetPosition.set(x, y);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        font.dispose();
    }

    // Helper classes

    public static class FormationLeader {
        Vector2 position = new Vector2();
        Vector2 velocity = new Vector2();
        float maxSpeed = 200f;

        public FormationLeader(float x, float y) {
            position.set(x, y);
        }

        public void update(Vector2 target, float deltaTime) {
            // Simple seek behavior
            Vector2 desiredVelocity = new Vector2(target).sub(position).nor().scl(maxSpeed);
            Vector2 steering = new Vector2(desiredVelocity).sub(velocity);

            // Apply steering with some limitations
            float maxSteeringForce = 300f;
            steering.limit(maxSteeringForce);

            // Update velocity and position
            velocity.add(steering.scl(deltaTime)).limit(maxSpeed);
            position.add(new Vector2(velocity).scl(deltaTime));
        }
    }

    public static class FormationMember {
        Vector2 position = new Vector2();
        Vector2 velocity = new Vector2();
        Vector2 targetPosition = new Vector2();
        float maxSpeed = 150f;

        public FormationMember(float x, float y) {
            position.set(x, y);
        }

        public void update(float deltaTime) {
            // Simple arrive behavior
            Vector2 toTarget = new Vector2(targetPosition).sub(position);
            float distance = toTarget.len();

            // Calculate desired velocity
            Vector2 desiredVelocity = new Vector2();
            float arrivalRadius = 50f;

            if (distance < arrivalRadius) {
                // Inside arrival radius, scale speed by distance
                desiredVelocity.set(toTarget).nor().scl(maxSpeed * (distance / arrivalRadius));
            } else {
                // Outside arrival radius, go full speed
                desiredVelocity.set(toTarget).nor().scl(maxSpeed);
            }

            // Calculate steering force
            Vector2 steering = new Vector2(desiredVelocity).sub(velocity);

            // Apply steering with limitations
            float maxSteeringForce = 350f;
            steering.limit(maxSteeringForce);

            // Update velocity and position
            velocity.add(steering.scl(deltaTime)).limit(maxSpeed);
            position.add(new Vector2(velocity).scl(deltaTime));
        }
    }
}
