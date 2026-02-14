package io.github.amitalimollaei.mods.vibrantf3.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public abstract class VibrantDebugScreenDisplayer implements DebugScreenDisplayer {
    Color color;
    Identifier debugIdentifier = null;

    private @NotNull String maybeAddDebugPrefix(@NotNull String string) {
        return (debugIdentifier == null ? "" : "[" + debugIdentifier + "] ") + string;
    }

    public void addPriorityLine(@NonNull String string) {
        vaddPriorityLine(Component.literal(maybeAddDebugPrefix(string)).withColor(color.getRGB()));
    }

    public void addLine(@NonNull String string) {
        vaddLine(Component.literal(maybeAddDebugPrefix(string)).withColor(color.getRGB()));
    }

    public void addToGroup(@NonNull Identifier identifier, Collection<String> collection) {
        Collection<Component> componentCollection = new ArrayList<>();
        for (String string: collection) {
            componentCollection.add(Component.literal(maybeAddDebugPrefix(string)).withColor(color.getRGB()));
        }
        vaddToGroup(identifier, componentCollection);
    }

    public void addToGroup(@NonNull Identifier identifier, @NonNull String string) {
        vaddToGroup(identifier, Component.literal(maybeAddDebugPrefix(string)).withColor(color.getRGB()));
    }

    public void vaddPriorityLine(Component text) {}

    public void vaddLine(Component text) {}

    public void vaddToGroup(Identifier identifier, Collection<Component> collection) {}

    public void vaddToGroup(Identifier identifier, Component text) {}

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDebugIdentifier(Identifier debugIdentifier) {
        this.debugIdentifier = debugIdentifier;
    }
}
