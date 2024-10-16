package io.github.srcimon.screwbox.core.ui;

import io.github.srcimon.screwbox.core.graphics.ScreenBounds;
import io.github.srcimon.screwbox.core.graphics.internal.DefaultCanvas;

public interface UiRenderer {

    void renderSelectableItem(String label, ScreenBounds bounds, DefaultCanvas canvas);

    void renderSelectedItem(String label, ScreenBounds bounds, DefaultCanvas canvas);

    void renderInactiveItem(String label, ScreenBounds bounds, DefaultCanvas canvas);
}
