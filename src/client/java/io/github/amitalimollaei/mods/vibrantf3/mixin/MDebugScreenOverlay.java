package io.github.amitalimollaei.mods.vibrantf3.mixin;

import com.google.common.base.Strings;
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
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

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

    /**
     * @author amiralimollaei
     * @reason we need to change all instances of lines being stored as String to lines being stored as Component
     */
    @Overwrite
    public void render(GuiGraphics guiGraphics) {
        Options options = minecraft.options;
        if (minecraft.isGameLoadFinished() && (!options.hideGui || minecraft.screen != null)) {
            Collection<Identifier> collection = minecraft.debugEntries.getCurrentlyEnabled();
            if (!collection.isEmpty()) {
                guiGraphics.nextStratum();
                ProfilerFiller profilerFiller = Profiler.get();
                profilerFiller.push("debug");
                ChunkPos chunkPos;
                if (minecraft.getCameraEntity() != null && minecraft.level != null) {
                    BlockPos blockPos = minecraft.getCameraEntity().blockPosition();
                    chunkPos = new ChunkPos(blockPos);
                } else {
                    chunkPos = null;
                }

                if (!Objects.equals(lastPos, chunkPos)) {
                    lastPos = chunkPos;
                    clearChunkCache();
                }


                // this runs the only part of the function's code that we actually change
                // TODO: only redirect to this function, don't overwrite `render`
                renderDebugEntries(guiGraphics, collection);

                guiGraphics.nextStratum();
                profilerPieChart.setBottomOffset(10);
                if (showFpsCharts()) {
                    int j = guiGraphics.guiWidth();
                    int k = j / 2;
                    fpsChart.drawChart(guiGraphics, 0, fpsChart.getWidth(k));
                    if (tickTimeLogger.size() > 0) {
                        int l = tpsChart.getWidth(k);
                        tpsChart.drawChart(guiGraphics, j - l, l);
                    }

                    profilerPieChart.setBottomOffset(tpsChart.getFullHeight());
                }

                if (showNetworkCharts() && minecraft.getConnection() != null) {
                    int j = guiGraphics.guiWidth();
                    int k = j / 2;
                    if (!minecraft.isLocalServer()) {
                        bandwidthChart.drawChart(guiGraphics, 0, bandwidthChart.getWidth(k));
                    }

                    int l = pingChart.getWidth(k);
                    pingChart.drawChart(guiGraphics, j - l, l);
                    profilerPieChart.setBottomOffset(pingChart.getFullHeight());
                }

                if (minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
                    IntegratedServer integratedServer = minecraft.getSingleplayerServer();
                    if (integratedServer != null && minecraft.player != null) {
                        ChunkLoadStatusView chunkLoadStatusView = integratedServer.createChunkLoadStatusView(16 + ChunkLevel.RADIUS_AROUND_FULL_CHUNK);
                        chunkLoadStatusView.moveTo(minecraft.player.level().dimension(), minecraft.player.chunkPosition());
                        LevelLoadingScreen.renderChunks(guiGraphics, guiGraphics.guiWidth() / 2, guiGraphics.guiHeight() / 2, 4, 1, chunkLoadStatusView);
                    }
                }

                try (Zone zone = profilerFiller.zone("profilerPie")) {
                    profilerPieChart.render(guiGraphics);
                }

                profilerFiller.pop();
            }
        }
    }

    @Unique
    private void renderDebugEntries(GuiGraphics guiGraphics, Collection<Identifier> collection) {
        Options options = minecraft.options;

        final List<Component> leftLines = new ArrayList<>();
        final List<Component> rightLines = new ArrayList<>();
        final Map<Identifier, Collection<Component>> map = new LinkedHashMap<>();
        final List<Component> lines = new ArrayList<>();
        Level level = getLevel();

        for(Identifier entryIdentifier : collection) {
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

        renderLines(guiGraphics, leftLines, true);
        renderLines(guiGraphics, rightLines, false);
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
