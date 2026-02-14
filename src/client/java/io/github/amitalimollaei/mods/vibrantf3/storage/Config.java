package io.github.amitalimollaei.mods.vibrantf3.storage;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.resources.Identifier;
import org.apache.commons.compress.utils.Lists;


import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Config extends MidnightConfig {
    @Entry(idMode = 1)
    public static List<String> identifierColor = Lists.newArrayList();
    @Entry
    public static boolean debugEnabled = false;

    public static List<String> getDefaultColors() {
        return List.of(
                "fabric:active_renderer#ff80dfff",
                "minecraft:fps#ff80ff8f",
                "minecraft:game_version#ff80ff8f",
                "minecraft:biome#fff9ff80",
                "minecraft:chunk_generation_stats#fff9ff80",
                "minecraft:chunk_render_stats#fff9ff80",
                "minecraft:chunk_source_stats#fff9ff80",
                "minecraft:entity_render_stats#fff9ff80",
                "minecraft:entity_spawn_counts#fff9ff80",
                "minecraft:heightmap#fff9ff80",
                "minecraft:light_levels#fff9ff80",
                "minecraft:local_difficulty#fff9ff80",
                "minecraft:looking_at_block#fff9ff80",
                "minecraft:looking_at_entity#fff9ff80",
                "minecraft:looking_at_fluid#fff9ff80",
                "minecraft:player_position#ff80ff8f",
                "minecraft:particle_render_stats#fff9ff80",
                "minecraft:player_section_position#ff80ff8f",
                "minecraft:post_effect#fff9ff80",
                "minecraft:tps#ff80ff8f",
                "minecraft:system_specs#ff80dfff",
                "minecraft:gpu_utilization#ffffc280",
                "minecraft:simple_performance_impactors#ff80dfff",
                "minecraft:memory#ffffc280",
                "minecraft:sound_mood#fff9ff80",
                "minecraft:3d_crosshair#ffe0e0e0",
                "minecraft:chunk_borders#ffe0e0e0",
                "minecraft:chunk_section_octree#ffe0e0e0",
                "minecraft:chunk_section_paths#ffe0e0e0",
                "minecraft:chunk_section_visibility#ffe0e0e0",
                "minecraft:entity_hitboxes#ffe0e0e0",
                "minecraft:visualize_block_light_levels#ffe0e0e0",
                "minecraft:visualize_chunks_on_server#ffe0e0e0",
                "minecraft:visualize_collision_boxes#ffe0e0e0",
                "minecraft:visualize_entity_supporting_blocks#ffe0e0e0",
                "minecraft:visualize_heightmap#ffe0e0e0",
                "minecraft:visualize_sky_light_levels#ffe0e0e0",
                "minecraft:visualize_sky_light_sections#ffe0e0e0",
                "minecraft:visualize_solid_faces#ffe0e0e0",
                "minecraft:visualize_water_levels#ffe0e0e0"
        );
    }

    public static void addEntryColor(Identifier identifier, Color color) {
        List<String> newIdentifierColor = Lists.newArrayList();
        String identifierString = identifier.toString();
        for (String entry: identifierColor) {
            if (!entry.startsWith(identifierString)) {
                newIdentifierColor.add(entry);
            }
        }
        newIdentifierColor.add(identifier + "#" + Integer.toHexString(color.getRGB()));
        identifierColor = newIdentifierColor;
    }

    public static void removeEntry(Identifier identifier) {
        List<String> newIdentifierColor = Lists.newArrayList();
        String identifierString = identifier.toString();
        for (String entry: identifierColor) {
            if (!entry.startsWith(identifierString)) {
                newIdentifierColor.add(entry);
            }
        }
        identifierColor = newIdentifierColor;
    }

    public static Color getDefaultEntryColor(Identifier identifier) {
        String identifierString = identifier.toString();
        AtomicReference<Color> color = new AtomicReference<>(new Color(0xFFE0E0E0));
        getDefaultColors().stream().filter(v -> v.startsWith(identifierString)).findFirst().ifPresent(
                v -> {
                    String hexColor = v.substring(identifierString.length()+1);
                    int colorInt = Integer.parseInt(hexColor.substring(2), 16);
                    color.set(new Color(colorInt));
                }
        );
        return color.get();
    }

    public static Color getEntryColor(Identifier identifier) {
        String identifierString = identifier.toString();
        AtomicReference<Color> color = new AtomicReference<>(null);
        identifierColor.stream().filter(v -> v.startsWith(identifierString)).findFirst().ifPresent(
                v -> {
                    String hexColor = v.substring(identifierString.length()+1);
                    int colorInt = Integer.parseInt(hexColor.substring(2), 16);
                    color.set(new Color(colorInt));
                }
        );
        Color entryColor = color.get();
        if (entryColor == null) {
            entryColor = getDefaultEntryColor(identifier);
        }
        return entryColor;
    }

    public static boolean isEntryPresent(Identifier identifier) {
        String identifierString = identifier.toString();
        return identifierColor.stream().anyMatch(v -> v.startsWith(identifierString));
    }
}
