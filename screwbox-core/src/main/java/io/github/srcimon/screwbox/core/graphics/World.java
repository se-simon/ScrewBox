package io.github.srcimon.screwbox.core.graphics;

import io.github.srcimon.screwbox.core.*;

public interface World {

    World drawColor(Color color);

    Color drawColor();

    World drawFadingCircle(Vector position, double diameter, Color color);

    World drawSpriteBatch(SpriteBatch spriteBatch, Bounds clipArea);

    default World drawSpriteBatch(final SpriteBatch spriteBatch) {
        return drawSpriteBatch(spriteBatch, null);
    }

    World drawSprite(Sprite sprite, Vector origin, double scale, Percent opacity, Rotation rotation,
                     Flip flip, Bounds clipArea);

    default World drawSprite(final Sprite sprite, final Vector origin, final Percent opacity,
                             final Rotation rotation, final Flip flip) {
        return drawSprite(sprite, origin, 1, opacity, rotation, flip, null);
    }

    default World drawSprite(final Sprite sprite, final Vector origin, final Percent opacity) {
        return drawSprite(sprite, origin, opacity, Rotation.none(), Flip.NONE);
    }

    default World drawSprite(final Sprite sprite, final Vector origin) {
        return drawSprite(sprite, origin, Percent.max());
    }

    World drawText(Vector offset, String text, Font font, Color color);

    default World drawText(final Vector offset, final String text, final Font font) {
        return drawText(offset, text, font, drawColor());
    }

    World drawTextCentered(Vector position, String text, Font font, Color color);

    default World drawTextCentered(final Vector position, final String text, final Font font) {
        return drawTextCentered(position, text, font, drawColor());
    }

    World drawLine(Vector from, Vector to, Color color);

    default World drawLine(final Line line, final Color color) {
        return drawLine(line.from(), line.to(), color);
    }

    default World drawLine(final Line line) {
        return drawLine(line, drawColor());
    }

    default World drawLine(final Vector from, final Vector to) {
        return drawLine(from, to, drawColor());
    }

    World fillCircle(Vector position, int diameter, Color color);

    default World fillCircle(final Vector position, final int diameter) {
        return fillCircle(position, diameter, drawColor());
    }

    World drawCircle(Vector position, int diameter, Color color, int strokeWidth);

    World fillRectangle(Bounds bounds, Color color);

    default World fillRectangle(final Bounds bounds) {
        return fillRectangle(bounds, drawColor());
    }

    World drawTextCentered(Vector position, String text, Pixelfont font, Percent opacity, double scale);

    default World drawTextCentered(final Vector position, final String text, final Pixelfont font,
                                   final Percent opacity) {
        return drawTextCentered(position, text, font, opacity, 1);
    }

    Bounds visibleArea();

    World drawRectangle(Bounds bounds, Rotation rotation, Color color);

    default World drawRectangle(final Bounds bounds, final Color color) {
        return drawRectangle(bounds, Rotation.none(), color);
    }
}