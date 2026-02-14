package io.github.amitalimollaei.mods.vibrantf3;

import com.mojang.blaze3d.platform.InputConstants;
import eu.midnightdust.lib.config.MidnightConfig;
import io.github.amitalimollaei.mods.vibrantf3.storage.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;


public class VibrantF3Client implements ClientModInitializer {
    public static final String MOD_ID = "vibrant_f3";

    private static KeyMapping debugKeyBinding;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "key.category"));

    @Override
    public void onInitializeClient() {
        MidnightConfig.init(MOD_ID, Config.class);
        debugKeyBinding = new KeyMapping(
                "vibrant_f3.key.debug",
                InputConstants.Type.KEYSYM,
                -1,
                CATEGORY
        );
        KeyBindingHelper.registerKeyBinding(debugKeyBinding);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (debugKeyBinding.consumeClick()) {
                Config.debugEnabled = !Config.debugEnabled;
            }
        });
    }

    public static void saveConfig() {
        MidnightConfig.write(MOD_ID);
    }
}
