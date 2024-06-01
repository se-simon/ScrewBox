package io.github.srcimon.screwbox.vacuum;

import io.github.srcimon.screwbox.core.Engine;
import io.github.srcimon.screwbox.core.ScrewBox;
import io.github.srcimon.screwbox.vacuum.scenes.GameScene;

public class VacuumOutlawApp {

    public static void main(String[] args) {
        Engine screwBox = ScrewBox.createEngine("Vacuum Outlaws");
        screwBox.assets()
                .prepareClassPackageAsync(VacuumOutlawApp.class)
                .prepareEngineAssetsAsync();
        screwBox.scenes().add(new GameScene());
        screwBox.start(GameScene.class);
    }
}
