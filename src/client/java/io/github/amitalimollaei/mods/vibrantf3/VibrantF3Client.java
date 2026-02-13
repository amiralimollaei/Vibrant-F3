package io.github.amitalimollaei.mods.vibrantf3;

import eu.midnightdust.lib.config.MidnightConfig;
import io.github.amitalimollaei.mods.vibrantf3.storage.Config;
import net.fabricmc.api.ClientModInitializer;


public class VibrantF3Client implements ClientModInitializer {
    public static final String MOD_ID = "vibrant_f3";

    @Override
    public void onInitializeClient() {
        MidnightConfig.init(MOD_ID, Config.class);
    }

    public static void saveConfig() {
        MidnightConfig.write(MOD_ID);
    }
}
