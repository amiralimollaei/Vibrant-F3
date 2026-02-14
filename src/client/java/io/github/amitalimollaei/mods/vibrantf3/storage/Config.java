package io.github.amitalimollaei.mods.vibrantf3.storage;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.resources.Identifier;
import org.apache.commons.compress.utils.Lists;


import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Config extends MidnightConfig {
    @Entry(idMode = 1)
    public static List<String> identifierColor = Lists.newArrayList(); // Array String Lists are also supported

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

    public static Color getEntryColor(Identifier identifier) {
        String identifierString = identifier.toString();
        AtomicReference<Color> color = new AtomicReference<>(new Color(0xFFE0E0E0));
        identifierColor.stream().filter(v -> v.startsWith(identifierString)).findFirst().ifPresent(
                v -> {
                    String hexColor = v.substring(identifierString.length()+1);
                    int colorInt = Integer.parseInt(hexColor.substring(2), 16);
                    color.set(new Color(colorInt));
                }
        );
        return color.get();
    }

    public static boolean isEntryPresent(Identifier identifier) {
        String identifierString = identifier.toString();
        return identifierColor.stream().anyMatch(v -> v.startsWith(identifierString));
    }
}
