package io.github.srcimon.screwbox.core.environment.controls;

import io.github.srcimon.screwbox.core.Duration;
import io.github.srcimon.screwbox.core.environment.Component;
import io.github.srcimon.screwbox.core.environment.physics.CollisionDetailsComponent;

import java.io.Serial;

/**
 * Enforce ground contact for jumping. Automatically disable (and re-enable) {@link JumpControlComponent} based on the
 * last {@link CollisionDetailsComponent#lastBottomContact last bottom contact}.
 * Also needs {@link CollisionDetailsComponent} to work properly.
 */
//TODO check all names of the three components
public class SuspendJumpComponent implements Component {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Grace period that is granted after last bottom contact to allow jumping.
     */
    public Duration gracePeriod = Duration.ofMillis(100);
}
