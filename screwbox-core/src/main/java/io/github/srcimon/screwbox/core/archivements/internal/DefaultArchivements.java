package io.github.srcimon.screwbox.core.archivements.internal;

import io.github.srcimon.screwbox.core.Duration;
import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.archivements.Archivement;
import io.github.srcimon.screwbox.core.archivements.ArchivementInfo;
import io.github.srcimon.screwbox.core.archivements.Archivements;
import io.github.srcimon.screwbox.core.loop.internal.Updatable;
import io.github.srcimon.screwbox.core.utils.Reflections;
import io.github.srcimon.screwbox.core.utils.Sheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public class DefaultArchivements implements Archivements, Updatable {

    private final Sheduler lazyUpdateSheduler = Sheduler.withInterval(Duration.ofMillis(500));
    private final Engine engine;
    private final List<ArchivementInfoData> activeArchivements = new ArrayList<>();
    private final List<ArchivementInfoData> completedArchivements = new ArrayList<>();
    private Consumer<ArchivementInfo> completionReaction;

    public DefaultArchivements(final Engine engine) {
        this.engine = engine;
    }

    @Override
    public Archivements add(final Archivement archivement) {
        requireNonNull(archivement, "archivement must not be null");
        activeArchivements.add(new ArchivementInfoData(archivement));
        return this;
    }

    @Override
    public Archivements setCompletionReaction(Consumer<ArchivementInfo> completionReaction) {
        this.completionReaction = completionReaction;
        return this;
    }

    @Override
    public List<ArchivementInfo> allArchivements() {
        return Stream.concat(activeArchivements.stream(), completedArchivements.stream())
                .map(ArchivementInfo.class::cast)
                .toList();
    }

    @Override
    public List<ArchivementInfo> activeArchivements() {
        return unmodifiableList(activeArchivements);
    }

    @Override
    public List<ArchivementInfo> completedArchivements() {
        return unmodifiableList(completedArchivements);
    }

    @Override
    public Archivements progess(final Class<? extends Archivement> archivementFamily, int progress) {
        requireNonNull(archivementFamily, "archivement family must not be null");
        if (progress > 0) {
            for (final var activeArchivement : activeArchivements) {
                if (activeArchivement.isOfFamily(archivementFamily)) {
                    activeArchivement.progress(progress);
                }
            }
        }
        return this;
    }

    @Override
    public Archivements addAllFromPackage(String packageName) {
        Reflections.findClassesInPackage(packageName).stream()
                .filter(Archivement.class::isAssignableFrom)
                .map(clazz -> {
                    var constructors = clazz.getConstructors();
                    try {
                        return (Archivement) constructors[0].newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(this::add);
        return this;
    }

    @Override
    public void update() {
        final boolean refreshLazyArchivements = lazyUpdateSheduler.isTick();
        final var transferItems = new ArrayList<ArchivementInfoData>();

        for (var x : activeArchivements) {
            if (refreshLazyArchivements || !x.isLazy()) {
                var progress = x.autoProgress(engine);
                x.progress(progress);
            }
            if (x.isCompleted()) {
                transferItems.add(x);
                completionReaction.accept(x);
            }
        }

        for (var y : transferItems) {
            activeArchivements.remove(y);
            completedArchivements.add(y);
        }
        transferItems.clear();
    }

}
