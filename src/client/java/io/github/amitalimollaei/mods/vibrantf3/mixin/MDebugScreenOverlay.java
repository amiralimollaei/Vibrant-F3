package io.github.amitalimollaei.mods.vibrantf3.mixin;

import com.google.common.base.Strings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amitalimollaei.mods.vibrantf3.debug.VibrantDebugScreenDisplayer;
import io.github.amitalimollaei.mods.vibrantf3.storage.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class MDebugScreenOverlay {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable protected abstract Level getLevel();
    @Shadow @Nullable protected abstract  LevelChunk getClientChunk();
    @Shadow @Nullable protected abstract LevelChunk getServerChunk();
    @Shadow @Final private Font font;

    @Unique
    private List<Component> leftLines = new ArrayList<>();
    @Unique
    private List<Component> rightLines = new ArrayList<>();

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;display(Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
            )
    )
    public void vibrant_f3$skipVanillaDebugEntryDisplay(DebugScreenEntry instance, DebugScreenDisplayer debugScreenDisplayer, Level level, LevelChunk levelChunk1, LevelChunk levelChunk2, Operation<Void> original) {
        // do nothing, we inject our own code instead
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
            )
    )
    public void vibrant_f3$skipVanillaRenderLines(DebugScreenOverlay instance, GuiGraphics graphics, List<String> lines, boolean left, Operation<Void> original) {
        ArrayList<Component> renderingLines = new ArrayList<>();
        if (left) {
            renderingLines.addAll(leftLines);
        } else {
            renderingLines.addAll(rightLines);
        }
        // this adds the lines that we didn't account for
        for (String line: lines) {
            renderingLines.add(Component.literal(line));
        }
        vibrant_f3$renderLines(graphics, renderingLines, left);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V",
                    ordinal = 1  // index of the last renderLines call
            )
    )
    public void vibrant_f3$doVibrantDebugEntryDisplay(GuiGraphics graphics, CallbackInfo ci) {
        Collection<Identifier> collection = minecraft.debugEntries.getCurrentlyEnabled();
        vibrant_f3$renderDebugEntries(collection);
    }

    @Unique
    private void vibrant_f3$renderDebugEntries(@NonNull Collection<Identifier> debugEntries) {
        leftLines = new ArrayList<>();
        rightLines = new ArrayList<>();
        final Map<Identifier, Collection<Component>> map = new LinkedHashMap<>();
        final List<Component> lines = new ArrayList<>();
        Level level = getLevel();

        VibrantDebugScreenDisplayer vibrantDebugScreenDisplayer = new VibrantDebugScreenDisplayer() {
            public void vaddPriorityLine(Component text) {
                if (leftLines.size() > rightLines.size()) {
                    rightLines.add(text);
                } else {
                    leftLines.add(text);
                }

            }

            public void vaddLine(Component text) {
                lines.add(text);
            }

            public void vaddToGroup(Identifier identifier, Collection<Component> collection) {
                map.computeIfAbsent(identifier, x -> new ArrayList<>()).addAll(collection);
            }

            public void vaddToGroup(Identifier identifier, Component text) {
                map.computeIfAbsent(identifier, x -> new ArrayList<>()).add(text);
            }
        };

        for(Identifier entryIdentifier : debugEntries) {
            DebugScreenEntry debugScreenEntry = DebugScreenEntries.getEntry(entryIdentifier);
            if (debugScreenEntry != null) {
                vibrantDebugScreenDisplayer.setColor(Config.getEntryColor(entryIdentifier));
                debugScreenEntry.display(vibrantDebugScreenDisplayer, level, getClientChunk(), getServerChunk());
            }
        }

        if (!leftLines.isEmpty()) {
            leftLines.add(Component.literal(""));
        }

        if (!rightLines.isEmpty()) {
            rightLines.add(Component.literal(""));
        }

        if (!lines.isEmpty()) {
            int i = (lines.size() + 1) / 2;
            leftLines.addAll(lines.subList(0, i));
            rightLines.addAll(lines.subList(i, lines.size()));
            leftLines.add(Component.literal(""));
            if (i < lines.size()) {
                rightLines.add(Component.literal(""));
            }
        }

        List<Collection<Component>> list4 = new ArrayList<>(map.values());
        if (!list4.isEmpty()) {
            int j = (list4.size() + 1) / 2;

            for(int k = 0; k < list4.size(); ++k) {
                Collection<Component> collection2 = list4.get(k);
                if (!collection2.isEmpty()) {
                    if (k < j) {
                        leftLines.addAll(collection2);
                        leftLines.add(Component.literal(""));
                    } else {
                        rightLines.addAll(collection2);
                        rightLines.add(Component.literal(""));
                    }
                }
            }
        }
    }

    @Unique
    private void vibrant_f3$renderLines(GuiGraphics graphics, @NonNull List<Component> lines, boolean left) {
        Objects.requireNonNull(font);
        int i = 9;

        // draws the background of lines
        for(int j = 0; j < lines.size(); ++j) {
            Component text = lines.get(j);
            if (!Strings.isNullOrEmpty(text.getString())) {
                int k = font.width(text.getString());
                int l = left ? 2 : graphics.guiWidth() - 2 - k;
                int m = 2 + i * j;
                graphics.fill(l - 1, m - 1, l + k + 1, m + i - 1, 0x90505050);
            }
        }

        // draws the actual text of lines
        for(int j = 0; j < lines.size(); ++j) {
            Component text = lines.get(j);
            if (!Strings.isNullOrEmpty(text.getString())) {
                int k = font.width(text.getString());
                int l = left ? 2 : graphics.guiWidth() - 2 - k;
                int m = 2 + i * j;
                graphics.drawString(font, text, l, m, -2039584, false);
            }
        }

    }
}
