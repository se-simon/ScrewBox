package io.github.srcimon.screwbox.core.keyboard.internal;

import io.github.srcimon.screwbox.core.keyboard.Key;
import io.github.srcimon.screwbox.core.keyboard.KeyCombination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.event.KeyEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultKeyboardTest {

    @InjectMocks
    DefaultKeyboard keyboard;

    @Mock
    private KeyEvent keyEvent;

    @Test
    void isDown_keyWasPressedAndNotReleased_returnsTrue() {
        when(keyEvent.getKeyCode()).thenReturn(32);

        keyboard.keyPressed(keyEvent);

        assertThat(keyboard.isDown(Key.SPACE)).isTrue();
    }

    @Test
    void isDown_keyWasPressedAndReleased_returnsFalse() {
        when(keyEvent.getKeyCode()).thenReturn(32);

        keyboard.keyPressed(keyEvent);
        keyboard.keyReleased(keyEvent);

        assertThat(keyboard.isDown(Key.SPACE)).isFalse();
    }

    @Test
    void isDown_onlyOneKeyOfCombinationDown_returnsFalse() {
        when(keyEvent.getKeyCode()).thenReturn(32);
        KeyCombination combination = KeyCombination.ofKeys(Key.SPACE, Key.ARROW_DOWN);

        keyboard.keyPressed(keyEvent);

        assertThat(keyboard.isDown(combination)).isFalse();
    }

    @Test
    void isDown_allKeysOfCombinationDown_returnsTrue() {
        when(keyEvent.getKeyCode()).thenReturn(32, 40);
        KeyCombination combination = KeyCombination.ofKeys(Key.SPACE, Key.ARROW_DOWN);

        keyboard.keyPressed(keyEvent);
        keyboard.keyPressed(keyEvent);

        assertThat(keyboard.isDown(combination)).isTrue();
    }

    @Test
    void isPressed_sameFrame_false() {
        when(keyEvent.getKeyCode()).thenReturn(32);

        keyboard.keyPressed(keyEvent);

        assertThat(keyboard.isPressed(Key.SPACE)).isFalse();
    }

    @Test
    void isPressed_nextFrame_true() {
        when(keyEvent.getKeyCode()).thenReturn(32);

        keyboard.keyPressed(keyEvent);

        keyboard.update();

        assertThat(keyboard.isPressed(Key.SPACE)).isTrue();
    }

    @Test
    void pressedKeys_cAndSpacePressed_returnsCAndSpace() {
        when(keyEvent.getKeyCode()).thenReturn(Key.A.code(), Key.SPACE.code());

        keyboard.keyPressed(keyEvent);
        keyboard.keyPressed(keyEvent);

        keyboard.update();

        assertThat(keyboard.pressedKeys()).containsExactlyInAnyOrder(Key.A, Key.SPACE);
    }

}
