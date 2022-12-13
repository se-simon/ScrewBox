package de.suzufa.screwbox.examples.pathfinding;

import de.suzufa.screwbox.core.Engine;
import de.suzufa.screwbox.core.ScrewBox;
import de.suzufa.screwbox.examples.pathfinding.scenes.DemoScene;

public class PathfindingExample {

    public static void main(String[] args) {
        Engine engine = ScrewBox.createEngine("Pathfinding Example");

        engine.assets().preparePackage("de.suzufa.screwbox.examples.pathfinding");

        engine.scenes().add(new DemoScene("map.json"));

        engine.start(DemoScene.class);
    }

}
