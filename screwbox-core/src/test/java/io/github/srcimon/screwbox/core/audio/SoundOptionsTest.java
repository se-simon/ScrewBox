package io.github.srcimon.screwbox.core.audio;

import io.github.srcimon.screwbox.core.Percent;
import org.junit.jupiter.api.Test;

import static io.github.srcimon.screwbox.core.Vector.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SoundOptionsTest {

    @Test
    void soundOptions_loopedWithHalfVolume_hasInfinitePlaybacksAtHalfVolume() {
        var options = SoundOptions.playContinuously().volume(Percent.half()).speed(0.4);

        assertThat(options.times()).isEqualTo(Integer.MAX_VALUE);
        assertThat(options.volume()).isEqualTo(Percent.half());
        assertThat(options.speed()).isEqualTo(0.4);
    }

    @Test
    void soundOptions_onePlaybackWithQuarterVolume_hasOnePlaybackAtQuarterVolume() {
        var options = SoundOptions.playOnce().volume(Percent.quarter());

        assertThat(options.times()).isEqualTo(1);
        assertThat(options.volume()).isEqualTo(Percent.quarter());
    }

    @Test
    void soundOptions_threePlaybacksWithoutVolumeChanged_hasThreePlaybacksAtMaxVolume() {
        var options = SoundOptions.playTimes(3);

        assertThat(options.times()).isEqualTo(3);
        assertThat(options.volume()).isEqualTo(Percent.max());
    }

    @Test
    void soundOptions_positionSet_hasPosition() {
        var options = SoundOptions.playTimes(3).position($(20, 25));

        assertThat(options.position()).isEqualTo($(20, 25));
    }

    @Test
    void soundOptions_panOutOfLowerBounds_throwsException() {
        var options = SoundOptions.playOnce();

        assertThatThrownBy(() -> options.pan(-4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pan is out of valid range (-1 to 1): -4.0");
    }

    @Test
    void soundOptions_panOutOfUpperBounds_throwsException() {
        var options = SoundOptions.playOnce();

        assertThatThrownBy(() -> options.pan(4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pan is out of valid range (-1 to 1): 4.0");
    }

    @Test
    void soundOptions_speedOutOfLowerBounds_throwsException() {
        var options = SoundOptions.playOnce();

        assertThatThrownBy(() -> options.speed(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("speed is out of valid range (0.1 to 10.0): -1.0");
    }

    @Test
    void soundOptions_speedOutOfUpperBounds_throwsException() {
        var options = SoundOptions.playOnce();

        assertThatThrownBy(() -> options.speed(41))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("speed is out of valid range (0.1 to 10.0): 41.0");
    }

    @Test
    void soundOptions_validInputForPan_hasPan() {
        var options = SoundOptions.playOnce().pan(0.2);

        assertThat(options.pan()).isEqualTo(0.2);
    }

    @Test
    void soundOptions_notMarkedAsMusic_isEffect() {
        var options = SoundOptions.playOnce();

        assertThat(options.isEffect()).isTrue();
        assertThat(options.isMusic()).isFalse();
    }

    @Test
    void soundOptions_markedAsMusic_isEffect() {
        var options = SoundOptions.playOnce().asMusic();

        assertThat(options.isEffect()).isFalse();
        assertThat(options.isMusic()).isTrue();
    }

    @Test
    void playTimes_timesIsZero_throwsException() {
        assertThatThrownBy(() -> SoundOptions.playTimes(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sound must be played at least once");
    }
}
