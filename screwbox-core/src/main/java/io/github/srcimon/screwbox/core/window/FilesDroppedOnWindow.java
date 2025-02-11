package io.github.srcimon.screwbox.core.window;

import io.github.srcimon.screwbox.core.graphics.Offset;

import java.io.File;
import java.util.List;

public record FilesDroppedOnWindow(List<File> files, Offset offset) {
}
