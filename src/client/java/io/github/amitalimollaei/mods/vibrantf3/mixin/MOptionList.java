package io.github.amitalimollaei.mods.vibrantf3.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DebugOptionsScreen.OptionList.class)
public class MOptionList {
    @ModifyReturnValue(method = "getRowWidth", at = @At("RETURN"))
    private int vibrant_f3$modifyRowWidth(int original) {
        return original + 40;
    }
}
