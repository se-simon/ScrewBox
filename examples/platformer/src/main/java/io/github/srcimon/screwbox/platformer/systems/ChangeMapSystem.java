package io.github.srcimon.screwbox.platformer.systems;

import io.github.srcimon.screwbox.core.Ease;
import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.environment.Archetype;
import io.github.srcimon.screwbox.core.environment.Entity;
import io.github.srcimon.screwbox.core.environment.EntitySystem;
import io.github.srcimon.screwbox.core.environment.Order;
import io.github.srcimon.screwbox.core.environment.logic.TriggerAreaComponent;
import io.github.srcimon.screwbox.core.keyboard.Key;
import io.github.srcimon.screwbox.core.scenes.SceneTransition;
import io.github.srcimon.screwbox.core.scenes.animations.CirclesAnimation;
import io.github.srcimon.screwbox.platformer.components.ChangeMapComponent;
import io.github.srcimon.screwbox.platformer.scenes.GameScene;


@Order(Order.SystemOrder.SIMULATION_EARLY)
public class ChangeMapSystem implements EntitySystem {

    private static final Archetype CHANGE_MAP_ZONES = Archetype.of(ChangeMapComponent.class, TriggerAreaComponent.class);

    @Override
    public void update(Engine engine) {
        if (engine.keyboard().isPressed(Key.SPACE)) {
            for (Entity entity : engine.environment().fetchAll(CHANGE_MAP_ZONES)) {
                if (entity.get(TriggerAreaComponent.class).isTriggered) {
                    engine.scenes()
                            .addOrReplace(new GameScene(entity.get(ChangeMapComponent.class).fileName))
                            .switchTo(GameScene.class, SceneTransition.custom()
                                    .outroDurationMillis(750)
                                    .outroEase(Ease.SINE_IN)
                                    .introAnimation(new CirclesAnimation())
                                    .introDurationMillis(1200));
                }
            }
        }
    }

}
