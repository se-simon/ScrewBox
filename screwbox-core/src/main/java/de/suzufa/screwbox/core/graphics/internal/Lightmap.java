package de.suzufa.screwbox.core.graphics.internal;

import static de.suzufa.screwbox.core.graphics.internal.AwtMapper.toAwtColor;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;
import java.util.List;

import de.suzufa.screwbox.core.graphics.Color;
import de.suzufa.screwbox.core.graphics.Dimension;
import de.suzufa.screwbox.core.graphics.Offset;
import de.suzufa.screwbox.core.graphics.WindowBounds;

public class Lightmap implements AutoCloseable {

    private static final java.awt.Color FADE_TO_COLOR = toAwtColor(Color.TRANSPARENT);
    private static final float[] FRACTIONS = new float[] { 0.1f, 1f };

    private final BufferedImage image;
    private final Graphics2D graphics;
    private final int resolution;

    public Lightmap(final Dimension size, final int resolution) {
        this.image = new BufferedImage(
                size.width() / resolution + 1, // to avoid glitches add 1
                size.height() / resolution + 1, // to avoid glitches add 1
                BufferedImage.TYPE_INT_ARGB);
        this.resolution = resolution;
        this.graphics = (Graphics2D) image.getGraphics();
    }

    public void addFullBrightnessArea(final WindowBounds bounds) {
        graphics.setColor(toAwtColor(Color.BLACK));
        applyOpacityConfig(Color.BLACK);
        graphics.fillRect(bounds.offset().x() / resolution,
                bounds.offset().y() / resolution,
                bounds.size().width() / resolution,
                bounds.size().height() / resolution);
    }

    public void addPointLight(final Offset position, final int radius, final List<Offset> area, final Color color) {
        final Polygon polygon = new Polygon();
        for (final var node : area) {
            polygon.addPoint(node.x() / resolution, node.y() / resolution);
        }

        final RadialGradientPaint paint = radialPaint(position, radius, color);
        applyOpacityConfig(color);
        graphics.setPaint(paint);
        graphics.fillPolygon(polygon);
    }

    public void addSpotLight(final Offset position, final int radius, final Color color) {
        final RadialGradientPaint paint = radialPaint(position, radius, color);
        graphics.setPaint(paint);
        applyOpacityConfig(color);
        graphics.fillOval(
                position.x() / resolution - radius / resolution,
                position.y() / resolution - radius / resolution,
                radius / resolution * 2,
                radius / resolution * 2);
    }

    public BufferedImage image() {
        return (BufferedImage) ImageUtil.applyFilter(image, new InvertAlphaFilter());
    }

    private void applyOpacityConfig(final Color color) {
        applyOpacityConfig(color.opacity().valueFloat());
    }

    private void applyOpacityConfig(final float value) {
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value));
    }

    public int resolution() {
        return resolution;
    }

    @Override
    public void close() {
        graphics.dispose();
    }

    private RadialGradientPaint radialPaint(final Offset position, final int radius, final Color color) {
        final var usedRadius = radius > resolution ? radius : resolution;
        final var colors = new java.awt.Color[] { toAwtColor(color.opacity(1)), FADE_TO_COLOR };

        return new RadialGradientPaint(
                position.x() / (float) resolution,
                position.y() / (float) resolution,
                usedRadius / resolution,
                FRACTIONS, colors);
    }

}
