package io.github.amitalimollaei.mods.vibrantf3.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amitalimollaei.mods.vibrantf3.VibrantF3Client;
import io.github.amitalimollaei.mods.vibrantf3.gui.ColorPickerButton;
import io.github.amitalimollaei.mods.vibrantf3.storage.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Mixin(DebugOptionsScreen.OptionEntry.class)
public abstract class MOptionEntry extends DebugOptionsScreen.AbstractOptionEntry {
    @Shadow
    @Final
    protected List<AbstractWidget> children;
    @Shadow
    @Final
    private CycleButton<Boolean> always;
    @Unique
    private static final int COLOR_PICKER_BUTTON_SIZE = 16;
    @Unique
    private ColorPickerButton colorPickerButton;

    @Unique
    private void setColor(Identifier identifier, Color newColor) {
        colorPickerButton.setColor(newColor);
        Config.addEntryColor(identifier, newColor);
        VibrantF3Client.saveConfig();
    }

    @Inject(
            method = "<init>(Lnet/minecraft/client/gui/screens/debug/DebugOptionsScreen;Lnet/minecraft/resources/Identifier;)V",
            at = @At("TAIL")
    )
    public void vibrant_f3$injectColorPickerButton(DebugOptionsScreen debugOptionsScreen, Identifier identifier, CallbackInfo ci) {
        ColorPickerButton colorPicker = new ColorPickerButton(
                0, 0, COLOR_PICKER_BUTTON_SIZE, Config.getEntryColor(identifier),
                input -> new Thread(() -> {
                    if (Minecraft.getInstance().hasShiftDown()) {
                        setColor(identifier, Color.WHITE);
                    } else {
                        Color initalColor = Config.getEntryColor(identifier);
                        Color newColor = JColorChooser.showDialog(null, Component.translatable("vibrantf3.gui.color_picker").getString(), initalColor);
                        if (newColor != null) {
                            setColor(identifier, newColor);
                        }
                    }
                }).start()
        );
        colorPicker.setTooltip(Tooltip.create(Component.translatable("vibrantf3.gui.color_picker")));

        colorPickerButton = colorPicker;
        children.add(colorPickerButton);
    }

    @WrapOperation(
            method = "renderContent(Lnet/minecraft/client/gui/GuiGraphics;IIZF)V",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton;setX(I)V",
                    ordinal = 0
            )
    )
    public void vibrant_f3$renderColorPickerButton1(CycleButton<Boolean> instance, int i, Operation<Void> original) {
        original.call(instance, i - COLOR_PICKER_BUTTON_SIZE);
    }

    @Inject(
            method = "renderContent(Lnet/minecraft/client/gui/GuiGraphics;IIZF)V",
            at = @At("TAIL")
    )
    public void vibrant_f3$renderColorPickerButton2(GuiGraphics guiGraphics, int i, int j, boolean bl, float f, CallbackInfo ci) {
        colorPickerButton.setX(always.getX() + always.getWidth());
        colorPickerButton.setY(this.getContentY());
        colorPickerButton.render(guiGraphics, i, j, f);
    }
}
