package mods.betterfoliage.client.render;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.chunk.ChunkOverlayManager;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.column.AbstractRenderColumn;
import mods.octarinecore.client.render.BlockContext;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u0015H\u0016R\u0014\u0010\u0003\u001a\u00020\u00048VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\nX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\u000e8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0010\u00a8\u0006\u0016"},
   d2 = {"Lmods/betterfoliage/client/render/RenderLog;", "Lmods/betterfoliage/client/render/column/AbstractRenderColumn;", "()V", "addToCutout", "", "getAddToCutout", "()Z", "connectPerpendicular", "getConnectPerpendicular", "overlayLayer", "Lmods/betterfoliage/client/render/RoundLogOverlayLayer;", "getOverlayLayer", "()Lmods/betterfoliage/client/render/RoundLogOverlayLayer;", "radiusLarge", "", "getRadiusLarge", "()D", "radiusSmall", "getRadiusSmall", "isEligible", "ctx", "Lmods/octarinecore/client/render/BlockContext;", "BetterFoliage-MC1.12"}
)
public final class RenderLog extends AbstractRenderColumn {
   @NotNull
   private final RoundLogOverlayLayer overlayLayer = new RoundLogOverlayLayer();

   public boolean getAddToCutout() {
      return false;
   }

   public boolean isEligible(@NotNull BlockContext ctx) {
      Intrinsics.checkParameterIsNotNull(ctx, "ctx");
      return Config.INSTANCE.getEnabled() && Config.roundLogs.INSTANCE.getEnabled() && Config.blocks.INSTANCE.getLogClasses().matchesClass(ctx.getBlock());
   }

   @NotNull
   public RoundLogOverlayLayer getOverlayLayer() {
      return this.overlayLayer;
   }

   public boolean getConnectPerpendicular() {
      return Config.roundLogs.INSTANCE.getConnectPerpendicular();
   }

   public double getRadiusSmall() {
      return Config.roundLogs.INSTANCE.getRadiusSmall();
   }

   public double getRadiusLarge() {
      return Config.roundLogs.INSTANCE.getRadiusLarge();
   }

   public RenderLog() {
      super("betterfoliage");
      ChunkOverlayManager.INSTANCE.getLayers().add(this.getOverlayLayer());
   }
}
