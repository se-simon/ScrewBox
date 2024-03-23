package io.github.srcimon.screwbox.core.audio.internal;

import io.github.srcimon.screwbox.core.Duration;
import io.github.srcimon.screwbox.core.audio.AudioConfiguration;
import io.github.srcimon.screwbox.core.test.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.sampled.TargetDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.srcimon.screwbox.core.Duration.ofSeconds;
import static io.github.srcimon.screwbox.core.test.TestUtil.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolumeMonitorTest {

    @Mock
    AudioAdapter audioAdapter;

    @Mock
    TargetDataLine targetDataLine;

    ExecutorService executor;

    VolumeMonitor volumeMonitor;

    AudioConfiguration configuration;


    @BeforeEach
    void setUp() {
        executor = Executors.newSingleThreadExecutor();
        configuration = new AudioConfiguration();
        volumeMonitor = new VolumeMonitor(executor, audioAdapter, configuration);
    }

    @Test
    void isActive_afterCallingLevel_isTrue() {
        volumeMonitor.level();

        assertThat(volumeMonitor.isActive()).isTrue();
    }

    @Test
    void isActive_noCallToLevel_isFalse() {
        assertThat(volumeMonitor.isActive()).isFalse();
    }

    @Test
    void isActive_afertIdleTimeoutReached_isFalse() {
        when(audioAdapter.getStartedTargetDataLine(any())).thenReturn(targetDataLine);
        configuration.setMicrophoneTimeout(Duration.ofMillis(40));
        volumeMonitor.level();

        await(() -> !volumeMonitor.isActive(), ofSeconds(1));
    }

    @Test
    void xxxx() {
        when(audioAdapter.getStartedTargetDataLine(any())).thenReturn(targetDataLine);
        when(targetDataLine.getBufferSize()).thenReturn(8);
        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();

            return null; // void method in a block-style lambda, so return null
        }).when(targetDataLine).read(Mockito.any(), Mockito.any(), Mockito.any());
        var x = volumeMonitor.level();

        await(() -> volumeMonitor.level().value() > 0, ofSeconds(1));
    }

    @AfterEach
    void tearDown() {
        TestUtil.shutdown(executor);
    }
}
