package io.github.amitalimollaei.mods.vibrantf3.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public abstract class VibrantDebugScreenDisplayer implements DebugScreenDisplayer {
    Color color;

    public void addPriorityLine(@NonNull String string) {
        vaddPriorityLine(Component.literal(string).withColor(color.getRGB()));
    }

    public void addLine(@NonNull String string) {
        vaddLine(Component.literal(string).withColor(color.getRGB()));
    }

    public void addToGroup(@NonNull Identifier identifier, Collection<String> collection) {
        Collection<Component> componentCollection = new ArrayList<>();
        for (String string: collection) {
            componentCollection.add(Component.literal(string).withColor(color.getRGB()));
        }
        vaddToGroup(identifier, componentCollection);
    }

    public void addToGroup(@NonNull Identifier identifier, @NonNull String string) {
        vaddToGroup(identifier, Component.literal(string).withColor(color.getRGB()));
    }

    public void vaddPriorityLine(Component text) {}

    public void vaddLine(Component text) {}

    public void vaddToGroup(Identifier identifier, Collection<Component> collection) {}

    public void vaddToGroup(Identifier identifier, Component text) {}

    public void setColor(Color color) {
        this.color = color;
    }
}
