package de.suzufa.screwbox.examples.pathfinding;

import java.util.List;
import java.util.Optional;

import de.suzufa.screwbox.core.Bounds;
import de.suzufa.screwbox.core.Engine;
import de.suzufa.screwbox.core.Vector;
import de.suzufa.screwbox.core.entityengine.Archetype;
import de.suzufa.screwbox.core.entityengine.Entity;
import de.suzufa.screwbox.core.entityengine.EntitySystem;
import de.suzufa.screwbox.core.entityengine.UpdatePriority;
import de.suzufa.screwbox.core.entityengine.components.PhysicsBodyComponent;
import de.suzufa.screwbox.core.entityengine.components.SpriteComponent;
import de.suzufa.screwbox.core.entityengine.components.TransformComponent;
import de.suzufa.screwbox.examples.pathfinding.EXPERIMENTAL.PathfindingSystem;

public class EnemyMovementSystem implements EntitySystem {

    private static final Archetype ENEMIES = Archetype.of(
            EnemyMovementComponent.class, PhysicsBodyComponent.class, SpriteComponent.class);

    private PathfindingSystem pathfindingSystem;

    @Override
    public void update(Engine engine) {
        if (pathfindingSystem == null) {
            pathfindingSystem = new PathfindingSystem(engine);
        }

        pathfindingSystem.update();
        pathfindingSystem.debugDraw();
        Entity player = engine.entityEngine().forcedFetch(PlayerMovementComponent.class, TransformComponent.class);
        Vector playerPosition = player.get(TransformComponent.class).bounds.position();

        for (Entity enemy : engine.entityEngine().fetchAll(ENEMIES)) {
            Vector enemyPosition = enemy.get(TransformComponent.class).bounds.position();
            Optional<List<Vector>> path = pathfindingSystem.findPath(playerPosition, enemyPosition);
            if (path.isPresent()) {
                List<Vector> list = path.get();

                for (var point : list) {
                    engine.graphics().world().drawRectangle(Bounds.atPosition(point, 4, 4));
                }
            }
        }

    }

    @Override
    public UpdatePriority updatePriority() {
        return UpdatePriority.PRESENTATION_UI;// TODO: fix
    }

}
