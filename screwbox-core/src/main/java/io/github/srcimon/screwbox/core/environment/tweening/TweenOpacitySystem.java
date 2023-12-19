package io.github.srcimon.screwbox.core.environment.tweening;

import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.Percent;
import io.github.srcimon.screwbox.core.environment.*;
import io.github.srcimon.screwbox.core.environment.rendering.RenderComponent;

/**
 * Updates the opacity of all {@link Entity}s that use tweening and have an {@link TweenOpacityComponent}.
 */
@Order(SystemOrder.PRESENTATION_PREPARE)
public class TweenOpacitySystem implements EntitySystem {

    private static final Archetype TWEENS = Archetype.of(TweenComponent.class, TweenOpacityComponent.class, RenderComponent.class);

    @Override
    public void update(Engine engine) {
        for (final var tweenEntity : engine.environment().fetchAll(TWEENS)) {
            final var opacityComponent = tweenEntity.get(TweenOpacityComponent.class);
            final var advance = (opacityComponent.to.value() - opacityComponent.from.value()) * tweenEntity.get(TweenComponent.class).progressValue.value();
            tweenEntity.get(RenderComponent.class).opacity = Percent.of(opacityComponent.from.value() + advance);
        }
    }
}