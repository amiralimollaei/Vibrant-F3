package io.github.amitalimollaei.mods.vibrantf3.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.amitalimollaei.mods.vibrantf3.debug.DebugLine;
import io.github.amitalimollaei.mods.vibrantf3.debug.VibrantDebugScreenDisplayer;
import io.github.amitalimollaei.mods.vibrantf3.storage.Config;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.*;
import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class MDebugScreenOverlay {
    @Shadow @Final private Font font;

    @Unique
    private List<DebugLine> leftLines;
    @Unique
    private List<DebugLine> rightLines;
    @Unique
    private Map<Identifier, Collection<DebugLine>> groupedLines;
    @Unique
    private List<DebugLine> lines;
    @Unique
    private VibrantDebugScreenDisplayer vibrantDebugScreenDisplayer;

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;display(Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
            )
    )
    public DebugScreenDisplayer vibrant_f3$replaceDebugScreenDisplayer(DebugScreenDisplayer original) {
        return vibrantDebugScreenDisplayer;
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntries;getEntry(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;"
            )
    )
    public DebugScreenEntry vibrant_f3$setEntryColor(Identifier entryIdentifier, Operation<DebugScreenEntry> original) {
        vibrantDebugScreenDisplayer.setColor(Config.getEntryColor(entryIdentifier));
        if (Config.debugEnabled) {
            vibrantDebugScreenDisplayer.setDebugIdentifier(entryIdentifier);
        }
        return original.call(entryIdentifier);
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"
            )
    )
    public void vibrant_f3$skipVanillaRenderLines(DebugScreenOverlay instance, GuiGraphics graphics, List<String> lines, boolean left, Operation<Void> original) {
        ArrayList<DebugLine> renderingLines = new ArrayList<>();
        if (left) {
            renderingLines.addAll(leftLines);
        } else {
            renderingLines.addAll(rightLines);
        }
        // this adds the lines that we didn't account for
        for (String line: lines) {
            renderingLines.add(new DebugLine(line));
        }
        vibrant_f3$renderLines(graphics, renderingLines, left);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;getLevel()Lnet/minecraft/world/level/Level;"
            )
    )
    public void vibrant_f3$resetRenderStates(GuiGraphics graphics, CallbackInfo ci) {
        leftLines = new ArrayList<>();
        rightLines = new ArrayList<>();
        groupedLines = new LinkedHashMap<>();
        lines = new ArrayList<>();
        vibrantDebugScreenDisplayer = new VibrantDebugScreenDisplayer() {
            public void vaddPriorityLine(DebugLine debugLine) {
                if (leftLines.size() > rightLines.size()) {
                    rightLines.add(debugLine);
                } else {
                    leftLines.add(debugLine);
                }

            }

            public void vaddLine(DebugLine debugLine) {
                lines.add(debugLine);
            }

            public void vaddToGroup(Identifier identifier, Collection<DebugLine> collection) {
                groupedLines.computeIfAbsent(identifier, x -> new ArrayList<>()).addAll(collection);
            }

            public void vaddToGroup(Identifier identifier, DebugLine debugLine) {
                groupedLines.computeIfAbsent(identifier, x -> new ArrayList<>()).add(debugLine);
            }
        };
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            )
    )
    private void vibrant_f3$spreadLines(GuiGraphics graphics, CallbackInfo ci) {
        if (!leftLines.isEmpty()) {
            leftLines.add(new DebugLine(""));
        }

        if (!rightLines.isEmpty()) {
            rightLines.add(new DebugLine(""));
        }

        if (!lines.isEmpty()) {
            int i = (lines.size() + 1) / 2;
            leftLines.addAll(lines.subList(0, i));
            rightLines.addAll(lines.subList(i, lines.size()));
            leftLines.add(new DebugLine(""));
            if (i < lines.size()) {
                rightLines.add(new DebugLine(""));
            }
        }

        List<Collection<DebugLine>> list4 = new ArrayList<>(groupedLines.values());
        if (!list4.isEmpty()) {
            int j = (list4.size() + 1) / 2;

            for(int k = 0; k < list4.size(); ++k) {
                Collection<DebugLine> collection2 = list4.get(k);
                if (!collection2.isEmpty()) {
                    if (k < j) {
                        leftLines.addAll(collection2);
                        leftLines.add(new DebugLine(""));
                    } else {
                        rightLines.addAll(collection2);
                        rightLines.add(new DebugLine(""));
                    }
                }
            }
        }
    }

    @Unique
    private void vibrant_f3$renderLines(GuiGraphics graphics, @NonNull List<DebugLine> lines, boolean left) {
        Objects.requireNonNull(font);

        for(int lineIdx = 0; lineIdx < lines.size(); ++lineIdx) {
            DebugLine debugLine = lines.get(lineIdx);
            int lineY = 2 + DebugLine.LINE_HEIGHT * lineIdx;
            debugLine.renderLine(graphics, lineY, left);
        }
    }
}
