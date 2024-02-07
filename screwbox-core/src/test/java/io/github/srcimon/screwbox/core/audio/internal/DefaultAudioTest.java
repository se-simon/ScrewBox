package io.github.srcimon.screwbox.core.audio.internal;

import io.github.srcimon.screwbox.core.Percent;
import io.github.srcimon.screwbox.core.audio.Sound;
import io.github.srcimon.screwbox.core.test.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import java.io.Serial;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.srcimon.screwbox.core.Percent.max;
import static io.github.srcimon.screwbox.core.Percent.zero;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Timeout(1)
@ExtendWith(MockitoExtension.class)
class DefaultAudioTest {

    DefaultAudio audio;

    @Mock
    AudioAdapter audioAdapter;

    @Mock
    Clip clip;

    ExecutorService executor;

    @BeforeEach
    void beforeEach() {
        executor = Executors.newSingleThreadExecutor();
        audio = new DefaultAudio(executor, audioAdapter);
    }

    @Test
    void playEffect_effectVolumeZero_doesntPlayEffect() {
        Sound sound = Sound.fromFile("kill.wav");

        audio.setEffectVolume(zero());
        audio.playEffect(sound);

        awaitShutdown();

        verify(audioAdapter, never()).createClip(any());
    }

    @Test
    void playEffectLooped_effectVolumeZero_doesntPlayEffect() {
        Sound sound = Sound.fromFile("kill.wav");

        audio.muteEffects();
        audio.playEffectLooped(sound);

        awaitShutdown();

        verify(audioAdapter, never()).createClip(any());
    }

    @Test
    void playMusic_musicVolumeZero_doesntPlayEffect() {
        Sound sound = Sound.fromFile("kill.wav");

        audio.muteMusic();
        audio.playMusic(sound);

        awaitShutdown();

        verify(audioAdapter, never()).createClip(any());
    }

    @Test
    void playMusicLooped_musicVolumeZero_doesntPlayEffect() {
        Sound sound = Sound.fromFile("kill.wav");

        audio.muteMusic();
        audio.playMusicLooped(sound);

        awaitShutdown();

        verify(audioAdapter, never()).createClip(any());
    }

    @Test
    void isActive_noInstanceActive_isFalse() {
        Sound sound = Sound.fromFile("kill.wav");

        assertThat(audio.isActive(sound)).isFalse();
    }

    @Test
    void isActive_twoInstanceActive_isTrue() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.playMusic(sound);
        audio.playMusic(sound);

        awaitShutdown();

        assertThat(audio.isActive(sound)).isTrue();
    }

    @Test
    void activeCount_noneActive_isZero() {
        assertThat(audio.activeCount()).isZero();
    }

    @Test
    void activeCount_noInstanceActive_isZero() {
        Sound sound = Sound.fromFile("kill.wav");

        assertThat(audio.activeCount(sound)).isZero();
    }

    @Test
    void activeCount_onlyAnotherSoundIsActive_isZero() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.playEffect(sound);

        Sound secondSound = Sound.fromFile("kill.wav");

        assertThat(audio.activeCount(secondSound)).isZero();
    }

    @Test
    void playEffect_invokesMethodsOnClipAndIncreasesActiveCount() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.playEffect(sound);

        awaitShutdown();

        verify(clip).start();

        assertThat(audio.activeCount(sound)).isEqualTo(1);
    }

    @Test
    void playEffectLooped_invokesMethodsOnClipAndIncreasesActiveCount() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.playEffectLooped(sound);

        awaitShutdown();

        verify(clip).loop(Integer.MAX_VALUE);

        assertThat(audio.activeCount(sound)).isEqualTo(1);
    }

    @Test
    void activeCount_oneInstanceStartedAndStopped_isZero() {
        Sound sound = Sound.fromFile("kill.wav");

        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.playEffect(sound);

        awaitShutdown();

        audio.update(stopEventFor(clip));

        assertThat(audio.activeCount(sound)).isZero();
    }


    @Test
    void stopAllSounds_clipIsActive_clipIsStopped() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);
        audio.playMusic(sound);

        audio.stopAllSounds();

        awaitShutdown();

        verify(clip).stop();
        assertThat(audio.activeCount()).isZero();
    }

    @Test
    void setEffectVolume_setsEffectVolume() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.setEffectVolume(Percent.half());
        audio.playEffect(sound);

        awaitShutdown();

        verify(audioAdapter).setVolume(clip, Percent.half());
        assertThat(audio.effectVolume()).isEqualTo(Percent.half());
    }

    @Test
    void setMusicVolume_setsMusicVolume() {
        Sound sound = Sound.fromFile("kill.wav");
        when(audioAdapter.createClip(sound)).thenReturn(clip);

        audio.setMusicVolume(Percent.of(0.7));
        audio.playMusic(sound);

        awaitShutdown();

        verify(audioAdapter).setVolume(clip, Percent.of(0.7));
        assertThat(audio.musicVolume()).isEqualTo(Percent.of(0.7));
    }

    @Test
    void muteMusic_musicUnmuted_mutesMusic() {
        audio.muteMusic();

        assertThat(audio.musicVolume()).isEqualTo(max());
        assertThat(audio.effectVolume()).isEqualTo(max());
        assertThat(audio.isMusicMuted()).isTrue();
        assertThat(audio.areEffectsMuted()).isFalse();
    }

    @Test
    void muteEffects_effectUnmuted_mutesEffects() {
        audio.muteEffects();

        assertThat(audio.musicVolume()).isEqualTo(max());
        assertThat(audio.effectVolume()).isEqualTo(max());
        assertThat(audio.isMusicMuted()).isFalse();
        assertThat(audio.areEffectsMuted()).isTrue();
    }

    @Test
    void playEffect_effectsMuted_doesntPlayAnySound() {
        audio.muteEffects();

        audio.playEffect(Sound.dummyEffect());

        awaitShutdown();

        verify(audioAdapter, never()).createClip(any());
    }

    @Test
    void playMusicmusicMuted_doesntPlayAnySound() {
        audio.muteMusic();

        audio.playMusic(Sound.dummyEffect());

        awaitShutdown();

        verify(audioAdapter, never()).createClip(any());
    }

    @Test
    void mute_allUnmuted_mutesEffectsAndMusic() {
        audio.mute();

        assertThat(audio.musicVolume()).isEqualTo(max());
        assertThat(audio.effectVolume()).isEqualTo(max());
        assertThat(audio.isMusicMuted()).isTrue();
        assertThat(audio.areEffectsMuted()).isTrue();
    }

    @Test
    void unmuteEffects_effectsHadVolumeConfigBefore_returnsOldVolume() {
        when(audioAdapter.createClip(any())).thenReturn(clip);
        audio.setEffectVolume(Percent.of(0.7));
        audio.muteEffects();

        audio.unmute();

        Sound sound = Sound.dummyEffect();
        audio.playEffect(sound);

        awaitShutdown();

        verify(audioAdapter).createClip(sound);
        verify(audioAdapter).setVolume(clip, Percent.of(0.7));
    }

    private LineEvent stopEventFor(Clip clipMock) {
        return new LineEvent(mock(Line.class), Type.STOP, 0) {

            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public Object getSource() {
                return clipMock;
            }
        };
    }

    @AfterEach
    void afterEach() {
        awaitShutdown();
    }

    private void awaitShutdown() {
        audio.shutdown();
        TestUtil.shutdown(executor);
    }
}