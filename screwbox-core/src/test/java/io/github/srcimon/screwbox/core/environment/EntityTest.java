package io.github.srcimon.screwbox.core.environment;

import io.github.srcimon.screwbox.core.environment.core.TransformComponent;
import io.github.srcimon.screwbox.core.environment.physics.ColliderComponent;
import io.github.srcimon.screwbox.core.environment.physics.PhysicsComponent;
import io.github.srcimon.screwbox.core.environment.physics.StaticColliderComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static io.github.srcimon.screwbox.core.Bounds.$$;
import static io.github.srcimon.screwbox.core.Vector.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class EntityTest {

    private Entity entity;

    @BeforeEach
    void beforeEach() {
        this.entity = new Entity();
    }

    @Test
    void newEntity_withoutId_hasNoId() {
        assertThat(entity.id()).isEmpty();
    }

    @Test
    void newEntiy_withId_hasId() {
        assertThat(new Entity(123).id()).isEqualTo(Optional.of(123));
    }

    @Test
    void add_componentClassNotPresent_addsComponent() {
        entity.add(new PhysicsComponent())
                .add(new ColliderComponent());

        assertThat(entity.getAll()).hasSize(2);
    }

    @Test
    void add_componentClassNotPresent_notifiesListeners() {
        var listener = Mockito.mock(EntityListener.class);
        entity.registerListener(listener);

        entity.add(new PhysicsComponent());

        verify(listener).componentAdded(argThat(event -> event.entity().equals(entity)));
    }

    @Test
    void add_componentClassAlreadyPresent_throwsException() {
        Component component = new PhysicsComponent();
        entity.add(component);

        assertThatThrownBy(() -> entity.add(component))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("component already present: PhysicsComponent");
    }

    @Test
    void get_componentPresent_returnsComponent() {
        Component component = new PhysicsComponent();
        entity.add(component);

        PhysicsComponent result = entity.get(PhysicsComponent.class);

        assertThat(result).isEqualTo(component);
    }

    @Test
    void get_componentNotPresent_returnsNull() {
        assertThat(entity.get(PhysicsComponent.class)).isNull();
    }

    @Test
    void add_addsComponentsToExistingEntity() {
        var physicsBodyComponent = new PhysicsComponent();
        var colliderComponent = new ColliderComponent();

        entity.add(physicsBodyComponent, colliderComponent);

        assertThat(entity.getAll()).contains(physicsBodyComponent, colliderComponent);
    }

    @Test
    void hasComponent_componentNotPresent_returnsFalse() {
        assertThat(entity.hasComponent(PhysicsComponent.class)).isFalse();
    }

    @Test
    void hasComponent_componentPresent_returnsTrue() {
        entity.add(new PhysicsComponent());

        assertThat(entity.hasComponent(PhysicsComponent.class)).isTrue();
    }

    @Test
    void remove_componentPresent_removesComponent() {
        entity.add(new PhysicsComponent());

        entity.remove(PhysicsComponent.class);

        assertThat(entity.hasComponent(PhysicsComponent.class)).isFalse();
    }

    @Test
    void remove_componentPresent_notifiesListeners() {
        var listener = Mockito.mock(EntityListener.class);
        entity.registerListener(listener);
        entity.add(new PhysicsComponent());

        entity.remove(PhysicsComponent.class);

        verify(listener).componentRemoved(argThat(event -> event.entity().equals(entity)));
    }

    @Test
    void componentCount_returnsCountOfComponents() {
        entity.add(new PhysicsComponent());
        entity.add(new ColliderComponent());

        assertThat(entity.componentCount()).isEqualTo(2);
    }

    @Test
    void getComponentClasses_returnsClassesOfComponents() {
        entity.add(new PhysicsComponent());
        entity.add(new ColliderComponent());

        assertThat(entity.getComponentClasses())
                .contains(PhysicsComponent.class, ColliderComponent.class)
                .hasSize(2);
    }

    @Test
    void isEmpty_noComponents_true() {
        assertThat(entity.isEmpty()).isTrue();
    }

    @Test
    void isEmpty_hasComponents_false() {
        entity.add(new PhysicsComponent());

        assertThat(entity.isEmpty()).isFalse();
    }

    @Test
    void toString_returnsEntityInformation() {
        assertThat(new Entity(124).name("Player").add(new PhysicsComponent(), new StaticColliderComponent()))
                .hasToString("Entity[id='124', name='Player', components=2]");

        assertThat(new Entity().name("Player").add(new PhysicsComponent()))
                .hasToString("Entity[name='Player', components=1]");

        assertThat(new Entity())
                .hasToString("Entity[components=none]");
    }

    @Test
    void position_transformRemoved_throwsException() {
        entity.add(new TransformComponent(10, 20, 16, 16));
        entity.remove(TransformComponent.class);

        assertThatThrownBy(() -> entity.position())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("entity has no TransformComponent");
    }

    @Test
    void position_hasTransform_returnsPositon() {
        entity.add(new TransformComponent(10, 20, 16, 16));

        assertThat(entity.position()).isEqualTo($(10, 20));
    }

    @Test
    void bounds_hasTransform_returnsBounds() {
        entity.add(new TransformComponent(10, 20, 16, 16));

        assertThat(entity.bounds()).isEqualTo($$(2, 12, 16, 16));
    }

    @Test
    void origin_hasTransform_returnsOrigin() {
        entity.add(new TransformComponent(10, 20, 16, 16));

        assertThat(entity.origin()).isEqualTo($(2, 12));
    }

    @Test
    void moveTo_hasTransform_movesToPosition() {
        entity.add(new TransformComponent(10, 20, 16, 16));

        entity.moveTo($(19, 30));

        assertThat(entity.position()).isEqualTo($(19, 30));
        assertThat(entity.bounds()).isEqualTo($$(11, 22, 16, 16));
    }

    @Test
    void moveBy_hasTransform_movesByVector() {
        entity.add(new TransformComponent(20, 40, 16, 16));

        entity.moveBy($(4, 2));

        assertThat(entity.position()).isEqualTo($(24, 42));
        assertThat(entity.bounds()).isEqualTo($$(16, 34, 16, 16));
    }

    @Test
    void addIfNotPresent_notPresent_addsComponent() {
        entity.addIfNotPresent(new ColliderComponent());

        assertThat(entity.hasComponent(ColliderComponent.class)).isTrue();
    }

    @Test
    void addIfNotPresent_alreadyPresent_doesntAddComponent() {
        var originalComponent = new ColliderComponent();
        entity.add(originalComponent);

        entity.addIfNotPresent(new ColliderComponent());

        assertThat(entity.hasComponent(ColliderComponent.class)).isTrue();
        assertThat(entity.get(ColliderComponent.class)).isEqualTo(originalComponent);
    }

    @Test
    void bounds_boundsIsNull_throwsException() {
        assertThatThrownBy(() -> entity.bounds(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("bounds must not be null");
    }

    @Test
    void bounds_boundsValid_addsTransformComponent() {
        entity.bounds($$(10, 30, 16, 32));

        assertThat(entity.bounds()).isEqualTo($$(10, 30, 16, 32));
    }

    @Test
    void addOrRepace_componentIsNull_throwsException() {
        assertThatThrownBy(() -> entity.addOrReplace(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("component must not be null");
    }

    @Test
    void addOrRepace_componentAlreadyPresent_replacesComponentAndDoesntRaiseEvent() {
        var listener = Mockito.mock(EntityListener.class);

        entity.add(new TransformComponent(40, 40, 40, 40));

        entity.registerListener(listener);

        entity.addOrReplace(new TransformComponent(10, 10, 10, 10));

        verifyNoInteractions(listener);
        assertThat(entity.position()).isEqualTo($(10, 10));
    }

    @Test
    void addOrRepace_componentIsNew_addsComponentAndRaisesEvent() {
        var listener = Mockito.mock(EntityListener.class);
        entity.registerListener(listener);

        entity.addOrReplace(new TransformComponent(10, 10, 10, 10));

        verify(listener).componentAdded(any());
        assertThat(entity.position()).isEqualTo($(10, 10));
    }
}
