package io.github.amitalimollaei.mods.vibrantf3.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public abstract class VibrantDebugScreenDisplayer implements DebugScreenDisplayer {
    private Color color = null;
    private Identifier debugIdentifier = null;

    public void addPriorityLine(@NonNull String string) {
        vaddPriorityLine(new DebugLine(string, color, debugIdentifier));
    }

    public void addLine(@NonNull String string) {
        vaddLine(new DebugLine(string, color, debugIdentifier));
    }

    public void addToGroup(@NonNull Identifier identifier, Collection<String> collection) {
        Collection<DebugLine> componentCollection = new ArrayList<>();
        for (String string: collection) {
            componentCollection.add(new DebugLine(string, color, debugIdentifier));
        }
        vaddToGroup(identifier, componentCollection);
    }

    public void addToGroup(@NonNull Identifier identifier, @NonNull String string) {
        vaddToGroup(identifier, new DebugLine(string, color, debugIdentifier));
    }

    public void vaddPriorityLine(DebugLine debugLine) {}

    public void vaddLine(DebugLine debugLine) {}

    public void vaddToGroup(Identifier identifier, Collection<DebugLine> collection) {}

    public void vaddToGroup(Identifier identifier, DebugLine debugLine) {}

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDebugIdentifier(Identifier debugIdentifier) {
        this.debugIdentifier = debugIdentifier;
    }
}
