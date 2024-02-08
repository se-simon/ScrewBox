package io.github.srcimon.screwbox.core.audio;

import java.util.EventObject;

/**
 * Occurs on any Change of the {@link AudioConfiguration} properties.
 */
public class AudioConfigurationEvent extends EventObject {

    private final ConfigurationProperty changedProperty;

    enum ConfigurationProperty {
        MUSIC_VOLUME,
        EFFECTS_VOLUME
    }
    public AudioConfigurationEvent(final Object source, final ConfigurationProperty changedProperty) {
        super(source);
        this.changedProperty = changedProperty;
    }

    public ConfigurationProperty changedProperty() {
        return changedProperty;
    }
}
