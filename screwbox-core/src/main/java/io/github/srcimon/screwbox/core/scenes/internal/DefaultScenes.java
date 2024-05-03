package io.github.srcimon.screwbox.core.scenes.internal;

import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.Time;
import io.github.srcimon.screwbox.core.environment.Environment;
import io.github.srcimon.screwbox.core.environment.internal.DefaultEnvironment;
import io.github.srcimon.screwbox.core.graphics.Screen;
import io.github.srcimon.screwbox.core.graphics.Sprite;
import io.github.srcimon.screwbox.core.loop.internal.Updatable;
import io.github.srcimon.screwbox.core.scenes.DefaultLoadingScene;
import io.github.srcimon.screwbox.core.scenes.DefaultScene;
import io.github.srcimon.screwbox.core.scenes.Scene;
import io.github.srcimon.screwbox.core.scenes.SceneTransition;
import io.github.srcimon.screwbox.core.scenes.Scenes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

import static java.util.Objects.nonNull;

public class DefaultScenes implements Scenes, Updatable {

    private final Map<Class<? extends Scene>, SceneData> scenes = new HashMap<>();

    private final Executor executor;
    private final Engine engine;
    private final Screen screen;
    private Sprite previousSceneScreenshot;

    private SceneData activeScene;
    private SceneData loadingScene;
    private ActiveTransition activeTransition;
    private boolean hasChangedToTargetScene = true;

    public DefaultScenes(final Engine engine, final Screen screen, final Executor executor) {
        this.engine = engine;
        this.executor = executor;
        this.screen = screen;
        SceneData defaultSceneData = new SceneData(new DefaultScene(), engine);
        defaultSceneData.setInitialized();
        scenes.put(DefaultScene.class, defaultSceneData);
        this.activeScene = defaultSceneData;
        setLoadingScene(new DefaultLoadingScene());
    }

    @Override
    public Scenes switchTo(final Class<? extends Scene> sceneClass) {
        return switchTo(sceneClass, SceneTransition.instant());
    }

    @Override
    public Scenes switchTo(final Class<? extends Scene> sceneClass, final SceneTransition transition) {
        ensureSceneExists(sceneClass);
        activeTransition = new ActiveTransition(sceneClass, transition);
        hasChangedToTargetScene = false;
        return this;
    }

    @Override
    public boolean isTransitioning() {
        return nonNull(activeTransition);
    }

    public DefaultEnvironment activeEnvironment() {
        return activeScene.environment();
    }

    @Override
    public Scenes remove(final Class<? extends Scene> sceneClass) {
        ensureSceneExists(sceneClass);
        if (activeScene.isSameAs(sceneClass)) {
            throw new IllegalArgumentException("cannot remove active scene");
        }
        scenes.remove(sceneClass);
        return this;
    }

    @Override
    public int sceneCount() {
        return scenes.size();
    }


    @Override
    public Scenes addOrReplace(final Scene scene) {
        final var sceneClass = scene.getClass();
        if (contains(sceneClass)) {
            remove(sceneClass);
        }
        add(scene);
        return this;
    }

    @Override
    public boolean contains(Class<? extends Scene> sceneClass) {
        return scenes.containsKey(sceneClass);
    }

    @Override
    public Scenes add(final Scene... scenes) {
        for (final var scene : scenes) {
            add(scene);
        }
        return this;
    }

    @Override
    public Class<? extends Scene> activeScene() {
        return activeScene.scene().getClass();
    }

    @Override
    public boolean isActive(final Class<? extends Scene> sceneClass) {
        return sceneClass.equals(activeScene());
    }

    @Override
    public Environment environmentOf(final Class<? extends Scene> sceneClass) {
        ensureSceneExists(sceneClass);
        return scenes.get(sceneClass).environment();
    }

    @Override
    public Scenes setLoadingScene(final Scene loadingScene) {
        this.loadingScene = new SceneData(loadingScene, engine);
        this.loadingScene.initialize();
        return this;
    }

    @Override
    public Optional<Sprite> previousSceneScreenshot() {
        return Optional.ofNullable(previousSceneScreenshot);
    }

    public boolean isShowingLoadingScene() {
        return !engine.isWarmedUp() || !activeScene.isInitialized();
    }

    @Override
    public void update() {
        final var sceneToUpdate = isShowingLoadingScene() ? loadingScene : activeScene;
        sceneToUpdate.environment().update();

        if (isTransitioning()) {
            final Time time = Time.now();
            final boolean mustSwitchScenes = !hasChangedToTargetScene && time.isAfter(activeTransition.switchTime());
            if (mustSwitchScenes) {
                previousSceneScreenshot = screen.takeScreenshot();
                activeScene.scene().onExit(engine);
                activeScene = scenes.get(activeTransition.targetScene());
                activeScene.scene().onEnter(engine);
                hasChangedToTargetScene = true;
            }
            if (hasChangedToTargetScene) {
                activeTransition.transition().introAnimation().draw(
                        screen,
                        activeTransition.transition().introEase().applyOn(activeTransition.introProgress(time)),
                        previousSceneScreenshot);
            } else {
                activeTransition.transition().extroAnimation().draw(
                        screen,
                        activeTransition.transition().extroEase().applyOn(activeTransition.extroProgress(time)));
            }

            if (hasChangedToTargetScene && activeTransition.introProgress(time).isMax()) {
                activeTransition = null;
            }
        }
    }

    //TODO prevent scene change while already changing scenes
    private void add(final Scene scene) {
        final SceneData sceneData = new SceneData(scene, engine);
        executor.execute(sceneData::initialize);
        scenes.put(scene.getClass(), sceneData);
    }

    private void ensureSceneExists(final Class<? extends Scene> sceneClass) {
        if (!scenes.containsKey(sceneClass)) {
            throw new IllegalArgumentException("scene doesn't exist: " + sceneClass);
        }
    }

}
