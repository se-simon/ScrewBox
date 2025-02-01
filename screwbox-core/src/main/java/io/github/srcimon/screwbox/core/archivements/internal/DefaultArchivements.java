package io.github.srcimon.screwbox.core.archivements.internal;

import io.github.srcimon.screwbox.core.Duration;
import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.Time;
import io.github.srcimon.screwbox.core.archivements.Archivement;
import io.github.srcimon.screwbox.core.archivements.ArchivementDefinition;
import io.github.srcimon.screwbox.core.archivements.Archivements;
import io.github.srcimon.screwbox.core.loop.internal.Updatable;
import io.github.srcimon.screwbox.core.utils.Reflections;
import io.github.srcimon.screwbox.core.utils.Sheduler;
import io.github.srcimon.screwbox.core.utils.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.srcimon.screwbox.core.utils.ListUtil.combine;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class DefaultArchivements implements Archivements, Updatable {

    private final Sheduler lazyUpdateSheduler = Sheduler.withInterval(Duration.ofMillis(500));
    private final Engine engine;
    private final Map<Class<? extends ArchivementDefinition>, List<DefaultArchivement>> archivementsByClass = new HashMap<>();
    private final List<DefaultArchivement> activeArchivements = new ArrayList<>();
    private final List<DefaultArchivement> completedArchivements = new ArrayList<>();
    private final Consumer<Archivement> onCompletion;

    public DefaultArchivements(final Engine engine, Consumer<Archivement> onCompletion) {
        this.engine = engine;
        this.onCompletion = onCompletion;
    }

    @Override
    public Archivements add(final ArchivementDefinition archivement) {
        requireNonNull(archivement, "archivement must not be null");
        final var defaultArchivement = new DefaultArchivement(archivement);
        final var archivementClazz = archivement.getClass();
        activeArchivements.add(defaultArchivement);
        final var archivementsOfClazz = archivementsByClass.get(archivementClazz);

        if (isNull(archivementsOfClazz)) {
            archivementsByClass.put(archivementClazz, new ArrayList<>(List.of(defaultArchivement)));
        } else {
            archivementsOfClazz.add(defaultArchivement);
        }
        return this;
    }

    @Override
    public List<Archivement> allArchivements() {
        return unmodifiableList(combine(activeArchivements, completedArchivements));
    }

    @Override
    public List<Archivement> activeArchivements() {
        return unmodifiableList(activeArchivements);
    }

    @Override
    public List<Archivement> completedArchivements() {
        return unmodifiableList(completedArchivements);
    }

    @Override
    public Archivements progess(final Class<? extends ArchivementDefinition> archivementType, int progress) {
        requireNonNull(archivementType, "archivementType must not be null");
        Validate.zeroOrPositive(progress, "progress must be positive");
        if (progress == 0) {
            return this;
        }
        final var archivmentsOfType = archivementsByClass.get(archivementType);
        if(isNull(archivmentsOfType)) {
            throw new IllegalArgumentException("archivement not present: " + archivementType.getSimpleName());
        }
        for (final var archivement : archivmentsOfType) {
            if(archivement.usesAutoProgression()) {
                throw new IllegalArgumentException("archivement %s uses automatic progression and cannot be updated manually".formatted(archivementType.getSimpleName()));
            }
            archivement.progress(progress);
        }
        return this;
    }

    @Override
    public Archivements addAllFromPackage(final String packageName) {
        Reflections.createInstancesFromPackage(packageName, ArchivementDefinition.class).forEach(this::add);
        return this;
    }

    @Override
    public void reset() {
        activeArchivements.addAll(completedArchivements);
        completedArchivements.clear();
        for(final var archivement : activeArchivements) {
            archivement.reset();
        }
    }

    @Override
    public void update() {
        final boolean mustRefreshAbsoluteArchivements = lazyUpdateSheduler.isTick();

        for (final var activeArchivement : new ArrayList<>(activeArchivements)) {
            if (mustRefreshAbsoluteArchivements || !activeArchivement.progressionIsAbsolute()) {
                activeArchivement.autoProgress(engine);
            }
            if (activeArchivement.isCompleted()) {
                activeArchivements.remove(activeArchivement);
                completedArchivements.add(activeArchivement);
                activeArchivement.setCompleted(Time.now());
                onCompletion.accept(activeArchivement);
            }
        }
    }

}
