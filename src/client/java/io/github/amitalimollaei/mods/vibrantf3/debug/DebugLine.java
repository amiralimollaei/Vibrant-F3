package io.github.amitalimollaei.mods.vibrantf3.debug;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class DebugLine {
    private final String text;
    private final Color color;
    private final Identifier entryId;

    private final Font font;

    public static final int LINE_HEIGHT = 9;
    public static final int DEFAULT_COLOR = 0xFFE0E0E0;
    public static final int BACKGROUND_ALPHA = 0x90;

    public DebugLine(String text) {
        this(text, null, null);
    }

    public DebugLine(String text, Identifier entryId) {
        this(text, null, entryId);
    }

    public DebugLine(String text, Color color) {
        this(text, color, null);
    }

    public DebugLine(String  text, Color color, Identifier entryId) {
        this.text = text;
        this.color = color;
        this.entryId = entryId;

        this.font = Minecraft.getInstance().font;
    }

    private @NotNull String maybeAddDebugInfo(@NotNull String string, boolean left) {
        if (left) {
            return (entryId == null ? "" : "[" + entryId + "] ") + string;
        } else {
            return string + (entryId == null ? "" : " [" + entryId + "]");
        }

    }

    public void renderLine(GuiGraphics graphics, int lineY, boolean left) {
        if (Strings.isNullOrEmpty(text)) return;
        Objects.requireNonNull(font);

        String line = maybeAddDebugInfo(text, left);
        int lineWidth = font.width(line);
        int lineX = left ? 2 : graphics.guiWidth() - 2 - lineWidth;
        Color lineColor = new Color(color == null ? DEFAULT_COLOR : color.getRGB());
        Color backgroundColor = new Color(
                (int) Math.max(lineColor.getRed() * 0.25F, 0),
                (int) Math.max(lineColor.getGreen() * 0.25F, 0),
                (int) Math.max(lineColor.getBlue() * 0.25F, 0),
                BACKGROUND_ALPHA
        );
        graphics.fill(lineX - 1, lineY - 1, lineX + lineWidth + 1, lineY + LINE_HEIGHT - 1, backgroundColor.getRGB());
        graphics.drawString(font, line, lineX, lineY, lineColor.getRGB(), false);
    }
}
