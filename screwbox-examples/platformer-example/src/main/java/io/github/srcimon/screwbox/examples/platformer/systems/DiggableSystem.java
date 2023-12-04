package io.github.srcimon.screwbox.examples.platformer.systems;

import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.Vector;
import io.github.srcimon.screwbox.core.assets.Asset;
import io.github.srcimon.screwbox.core.audio.Sound;
import io.github.srcimon.screwbox.core.environment.*;
import io.github.srcimon.screwbox.core.environment.components.*;
import io.github.srcimon.screwbox.core.physics.Borders;
import io.github.srcimon.screwbox.examples.platformer.components.DiggableComponent;
import io.github.srcimon.screwbox.examples.platformer.components.DiggingComponent;

import java.util.Optional;

@Order(SystemOrder.SIMULATION_BEGIN)
public class DiggableSystem implements EntitySystem {

    private static final Archetype DIGGINGS = Archetype.of(DiggingComponent.class, PhysicsBodyComponent.class);

    private static final Archetype DIGGABLES = Archetype.of(DiggableComponent.class, TransformComponent.class,
            RenderComponent.class);

    private static final Asset<Sound> DIG_SOUND = Sound.assetFromFile("sounds/dig.wav");

    @Override
    public void update(Engine engine) {
        for (final var digging : engine.environment().fetchAll(DIGGINGS)) {
            var diggingBody = digging.get(TransformComponent.class);
            Optional<Entity> hitEntity = engine.physics().raycastFrom(diggingBody.bounds.position())
                    .checkingFor(DIGGABLES)
                    .checkingBorders(Borders.TOP_ONLY)
                    .castingVertical(14)
                    .selectAnyEntity();

            if (hitEntity.isEmpty()) {
                return;
            }

            Entity entity = hitEntity.get();
            if (!entity.hasComponent(FadeOutComponent.class)) {
                entity.add(new FadeOutComponent(2));
                entity.remove(ColliderComponent.class);
                PhysicsBodyComponent physicsBodyComponent = digging.get(PhysicsBodyComponent.class);
                physicsBodyComponent.momentum = Vector.of(physicsBodyComponent.momentum.x(), -150);
                engine.audio().playEffect(DIG_SOUND);
            }
        }
    }
}
