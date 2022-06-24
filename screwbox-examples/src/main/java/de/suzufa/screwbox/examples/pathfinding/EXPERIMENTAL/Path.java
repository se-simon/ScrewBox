package de.suzufa.screwbox.examples.pathfinding.EXPERIMENTAL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.suzufa.screwbox.core.Segment;
import de.suzufa.screwbox.core.Vector;

public class Path implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Vector> nodes;

    public static Path withNodes(final List<Vector> nodes) {
        return new Path(nodes);
    }

    private Path(final List<Vector> nodes) {
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException("Path must have at least one node.");
        }
        this.nodes = nodes;
    }

    public List<Segment> segments() {
        final var segments = new ArrayList<Segment>();
        for (int i = 0; i < nodeCount() - 1; i++) {
            final var segment = Segment.between(nodes.get(i), nodes.get(i + 1));
            segments.add(segment);
        }
        return segments;
    }

    public void dropStart() {
        if (nodeCount() == 1) {
            throw new IllegalStateException("Cannot drop last node.");
        }
        nodes.remove(0);
    }

    public List<Vector> nodes() {
        return nodes;
    }

    public Vector start() {
        return nodes.get(0);
    }

    public int nodeCount() {
        return nodes.size();
    }

    public Vector end() {
        return nodes.get(nodeCount() - 1);
    }
}
