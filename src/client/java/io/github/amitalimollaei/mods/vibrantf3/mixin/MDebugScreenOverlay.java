package io.github.amitalimollaei.mods.vibrantf3.mixin;

import com.google.common.base.Strings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amitalimollaei.mods.vibrantf3.storage.Config;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debugchart.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.*;
import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class MDebugScreenOverlay {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private @Nullable ChunkPos lastPos;

    @Shadow
    public abstract void clearChunkCache();

    @Shadow
    @Nullable
    protected abstract Level getLevel();

    @Shadow
    protected abstract @Nullable LevelChunk getClientChunk();

    @Shadow
    @Nullable
    protected abstract LevelChunk getServerChunk();

    @Shadow
    private boolean renderProfilerChart;

    @Shadow
    @Final
    private ProfilerPieChart profilerPieChart;

    @Shadow
    public abstract boolean showFpsCharts();

    @Shadow
    @Final
    private FpsDebugChart fpsChart;

    @Shadow
    @Final
    private LocalSampleLogger tickTimeLogger;

    @Shadow
    @Final
    private TpsDebugChart tpsChart;

    @Shadow
    public abstract boolean showNetworkCharts();

    @Shadow
    @Final
    private BandwidthDebugChart bandwidthChart;

    @Shadow
    @Final
    private PingDebugChart pingChart;

    @Shadow
    private boolean renderFpsCharts;

    @Shadow
    private boolean renderNetworkCharts;

    @Shadow
    @Final
    private Font font;

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
    public void vibrant_f3$skipVanillaRenderLines(DebugScreenOverlay instance, GuiGraphics guiGraphics, List<String> list, boolean bl, Operation<Void> original) {
        // do nothing, we inject our own code instead
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
        renderDebugEntries(graphics, collection);
    }

    @Unique
    private void renderDebugEntries(GuiGraphics graphics, Collection<Identifier> debugEntries) {
        Options options = minecraft.options;

        final List<Component> leftLines = new ArrayList<>();
        final List<Component> rightLines = new ArrayList<>();
        final Map<Identifier, Collection<Component>> map = new LinkedHashMap<>();
        final List<Component> lines = new ArrayList<>();
        Level level = getLevel();

        for(Identifier entryIdentifier : debugEntries) {
            DebugScreenEntry debugScreenEntry = DebugScreenEntries.getEntry(entryIdentifier);
            if (debugScreenEntry != null) {
                Color color = Config.getEntryColor(entryIdentifier);
                DebugScreenDisplayer vibrantDebugScreenDisplayer = new DebugScreenDisplayer() {
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

        if (minecraft.debugEntries.isOverlayVisible()) {
            leftLines.add(Component.literal(""));
            boolean bl = minecraft.getSingleplayerServer() != null;
            KeyMapping keyMapping = options.keyDebugModifier;
            String string = keyMapping.getTranslatedKeyMessage().getString();
            String var10000 = keyMapping.isUnbound() ? "" : string + "+";
            String string2 = "[" + var10000;
            String string3 = string2 + options.keyDebugPofilingChart.getTranslatedKeyMessage().getString() + "]";
            String string4 = string2 + options.keyDebugFpsCharts.getTranslatedKeyMessage().getString() + "]";
            String string5 = string2 + options.keyDebugNetworkCharts.getTranslatedKeyMessage().getString() + "]";
            leftLines.add(Component.literal(
                    "Debug charts: " + string3 + " Profiler " + (renderProfilerChart ? "visible" : "hidden") +
                            "; " + string4 + " " + (bl ? "FPS + TPS " : "FPS ") + (renderFpsCharts ? "visible" : "hidden") +
                            "; " + string5 + " " + (!minecraft.isLocalServer() ? "Bandwidth + Ping" : "Ping") +
                            (renderNetworkCharts ? " visible" : " hidden")
            ));
            String string6 = string2 + options.keyDebugDebugOptions.getTranslatedKeyMessage().getString() + "]";
            leftLines.add(Component.literal("To edit: press " + string6));
        }

        renderLines(graphics, leftLines, true);
        renderLines(graphics, rightLines, false);
    }

    @Unique
    private void renderLines(GuiGraphics graphics, List<Component> lines, boolean left) {
        Objects.requireNonNull(font);
        int i = 9;

        for(int j = 0; j < lines.size(); ++j) {
            Component text = lines.get(j);
            if (!Strings.isNullOrEmpty(text.getString())) {
                int k = font.width(text.getString());
                int l = left ? 2 : graphics.guiWidth() - 2 - k;
                int m = 2 + i * j;
                graphics.fill(l - 1, m - 1, l + k + 1, m + i - 1, 0x90505050);
            }
        }

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
