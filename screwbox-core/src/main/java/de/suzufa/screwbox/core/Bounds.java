package de.suzufa.screwbox.core;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import de.suzufa.screwbox.core.graphics.World;

/**
 * {@link Bounds} represents a space in the 2d world. It's defined by its
 * {@link Bounds#position()} (center of the area) and its {@link #extents()}
 * (the {@link Vector} from the center to it's lower right corner.
 * 
 * The {@link #origin()} defines the upper left corner.
 */
public final class Bounds implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Vector position;
    private final Vector origin;
    private final Vector bottomRight;
    private final Vector extents;

    /**
     * Creates a new {@link Bounds} at the given {@link #position()}.
     * 
     * @see #atOrigin
     */
    public static Bounds atPosition(final double x, final double y, final double width, final double height) {
        return new Bounds(x, y, width, height);
    }

    /**
     * Creates a new {@link Bounds} at the given {@link #position()}.
     * 
     * @see #atOrigin
     */
    public static Bounds atPosition(final Vector position, final double width, final double height) {
        return new Bounds(position.x(), position.y(), width, height);
    }

    /**
     * Creates a new {@link Bounds} at the given {@link #origin()}.
     * 
     * @see #atPosition
     */
    public static Bounds atOrigin(final Vector origin, final double width, final double height) {
        return atOrigin(origin.x(), origin.y(), width, height);
    }

    /**
     * Creates a new {@link Bounds} at the given {@link #origin()}. Short form of
     * {@link #atOrigin(double, double, double, double)}
     * 
     * @see #atPosition
     */
    public static Bounds $$(final double x, final double y, final double width, final double height) {
        return new Bounds(x + (width / 2.0), y + (height / 2.0), width, height);
    }

    /**
     * Creates a new {@link Bounds} at the given {@link #origin()}.
     * 
     * @see #atPosition
     * @see #$$(double, double, double, double)
     */
    public static Bounds atOrigin(final double x, final double y, final double width, final double height) {
        return new Bounds(x + (width / 2.0), y + (height / 2.0), width, height);
    }

    /**
     * Creates a new {@link Bounds} on maximum size and {@link #position()} on the
     * center of the game {@link World}.
     */
    public static Bounds max() {
        return atPosition(Vector.zero(), Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Checks if the given position is within this {@link Bounds}.
     */
    public boolean contains(final Vector position) {
        return minX() <= position.x() && maxX() >= position.x() && minY() <= position.y() && maxY() >= position.y();
    }

    private Bounds(final double x, final double y, final double width, final double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("size of width and length must no be negative");
        }
        this.position = Vector.of(x, y);
        this.extents = Vector.of(width / 2.0, height / 2.0);
        this.origin = position.substract(extents);
        this.bottomRight = position.add(extents);
    }

    private Bounds(final Vector position, final Vector extents) {
        this.position = position;
        this.extents = extents;
        this.origin = position.substract(extents);
        this.bottomRight = position.add(extents);
    }

    public Bounds moveBy(final Vector vector) {
        return moveBy(vector.x(), vector.y());
    }

    public Bounds moveBy(final double x, final double y) {
        return new Bounds(position.add(x, y), extents);
    }

    public Bounds moveTo(final Vector newPosition) {
        return new Bounds(newPosition, extents);
    }

    public Vector origin() {
        return origin;
    }

    /**
     * Returns the size of the {@link Bounds}.
     */
    public Vector size() {
        return Vector.of(width(), height());
    }

    /**
     * Returns a new instance of {@link Bounds} at the same {@link #position()} but
     * with different {@link #size()}. Positive values increase the {@link #size()},
     * negative one decreases the {@link #size()}.
     */
    public Bounds inflated(final double inflation) {
        return Bounds.atPosition(position, width() + inflation, height() + inflation);
    }

    // TODO: javadoc and test
    public Bounds inflatedTop(int value) {
        return Bounds.atPosition(position.addY(-value), width(), height() + value);
    }

    /**
     * Checks if this {@link Bounds} touches another {@link Bounds}. Is also
     * {@code true} if it {@link #intersects(Bounds)} the other {@link Bounds}.
     * 
     * @see #intersects(Bounds)
     */
    public boolean touches(final Bounds other) {
        return inflated(0.001).intersects(other);
    }

    /**
     * Checks if this {@link Bounds} intersects another {@link Bounds}.
     * 
     * @see #touches(Bounds)
     */
    public boolean intersects(final Bounds other) {
        return maxX() > other.minX() && minX() < other.maxX() && maxY() > other.minY() && minY() < other.maxY();
    }

    /**
     * Returns the width of this {@link Bounds}.
     */
    public double width() {
        return extents.x() * 2.0;
    }

    /**
     * Returns the height of this {@link Bounds}.
     */
    public double height() {
        return extents.y() * 2.0;
    }

    /**
     * Calculates the area of overlap between this {@link Bounds} and the other
     * {@link Bounds}.
     */
    public double overlapArea(final Bounds other) {
        final double xOverlap = Math.max(0, Math.min(maxX(), other.maxX()) - Math.max(minX(), other.minX()));
        if (xOverlap == 0) { // tweak performance: must not calculate yOverlap
            return 0;
        }
        final double yOverlap = Math.max(0, Math.min(maxY(), other.maxY()) - Math.max(minY(), other.minY()));
        return xOverlap * yOverlap;
    }

    public Vector extents() {
        return extents;
    }

    public double minX() {
        return origin.x();
    }

    public double maxX() {
        return bottomRight.x();
    }

    public double minY() {
        return origin.y();
    }

    public double maxY() {
        return bottomRight.y();
    }

    @Override
    public String toString() {
        return "Bounds [position=" + position + ", size=" + size() + "]";
    }

    public Vector position() {
        return position;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        return prime * result + ((extents == null) ? 0 : extents.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Bounds other = (Bounds) obj;
        return Objects.equals(extents, other.extents) && Objects.equals(position, other.position);
    }

    public Vector bottomLeft() {
        return Vector.of(minX(), maxY());
    }

    public Vector bottomRight() {
        return bottomRight;
    }

    public Vector topRight() {
        return Vector.of(maxX(), minY());
    }

    public Vector topLeft() {
        return Vector.of(minX(), minY());
    }

    // TODO: TEST (May be buggy)
    public Optional<Bounds> intersection(Bounds other) {
        var newMinX = Math.max(minX(), other.minX());
        var newMaxX = Math.min(maxX(), other.maxX());
        var newMinY = Math.max(minY(), other.minY());
        var newMaxY = Math.min(maxY(), other.maxY());
        double width = newMaxX - newMinX;
        double height = newMaxY - newMinY;
        if (width <= 0 || height <= 0) {
            return Optional.empty();
        }
        return Optional.of(Bounds.atOrigin(newMinX, newMinY, width, height));
    }

}