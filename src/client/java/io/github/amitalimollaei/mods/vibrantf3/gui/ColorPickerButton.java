package io.github.amitalimollaei.mods.vibrantf3.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPickerButton extends AbstractButton {
    Consumer<InputWithModifiers> onPressFn;
    protected Color color;
    protected int size;
    
    public ColorPickerButton(int x, int y, int size, Color color, Consumer<InputWithModifiers> onPressFn) {
        super(x, y, size, size, Component.empty());
        this.color = color;
        this.size = size;
        this.onPressFn = onPressFn;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    private int getShadowColor() {
        return new Color(
                (int) Math.max(color.getRed() * 0.25F, 0),
                (int) Math.max(color.getGreen() * 0.25F, 0),
                (int) Math.max(color.getBlue() * 0.25F, 0),
                255
        ).getRGB();
    }

    private int getForegroundColor() {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                255
        ).getRGB();
    }

    @Override
    public void onPress(@NonNull InputWithModifiers inputWithModifiers) {
        this.onPressFn.accept(inputWithModifiers);
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.isHovered()) graphics.fill(getX()+2, getY()+2, getX()+getWidth(), getY()+getHeight(), getShadowColor());
        graphics.fill(getX()+1, getY()+1, getX()+getWidth()-1, getY()+getHeight()-1, getForegroundColor());
        if (this.isHovered()) graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }
}