package io.github.srcimon.screwbox.core.archivements.internal;

import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.archivements.ArchivementInfo;
import io.github.srcimon.screwbox.core.archivements.Archivement;
import io.github.srcimon.screwbox.core.archivements.ArchivementConfiguration;

public class ArchivementInfoData implements ArchivementInfo {

    private ArchivementConfiguration options;
    private Archivement definition;
    private int score = 0;

    public ArchivementInfoData(final Archivement definition, final ArchivementConfiguration options) {
        this.definition = definition;
        this.options = options;
    }

    @Override
    public String title() {
        return options.title();
    }

    @Override
    public int goal() {
        return options.goal();
    }

    @Override
    public boolean isArchived() {
        return score() >= goal();
    }

    public int score() {
        return score;
    }

    public void progress(final int progress) {
        setProgress(score + progress);
    }

    public void setProgress(final int progress) {
        score = Math.min(goal(),  progress);
    }

    public boolean isOfFamily(Class<? extends Archivement> definition) {
        return definition.equals(options.family()) || this.definition.getClass().equals(definition);
    }

    public void autoProgress(Engine engine) {
        if(options.isFixedProgressMode()) {
            setProgress(definition.progress(engine));
        } else {
            progress(definition.progress(engine));
        }

    }

    public boolean isLazy() {
        return options.usesLazyRefresh();
    }
}
