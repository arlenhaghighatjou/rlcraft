package mods.betterfoliage.client.render;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.column.ColumnRenderLayer;
import mods.betterfoliage.client.render.column.ColumnTextureInfo;
import mods.octarinecore.client.resource.ModelRenderRegistry;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002R \u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\u00068VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\u00068VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000bR\u0014\u0010\u000e\u001a\u00020\u00068VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u000bR\u001a\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00118VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R \u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"},
   d2 = {"Lmods/betterfoliage/client/render/RoundLogOverlayLayer;", "Lmods/betterfoliage/client/render/column/ColumnRenderLayer;", "()V", "blockPredicate", "Lkotlin/Function1;", "Lnet/minecraft/block/state/IBlockState;", "", "getBlockPredicate", "()Lkotlin/jvm/functions/Function1;", "connectSolids", "getConnectSolids", "()Z", "defaultToY", "getDefaultToY", "lenientConnect", "getLenientConnect", "registry", "Lmods/octarinecore/client/resource/ModelRenderRegistry;", "Lmods/betterfoliage/client/render/column/ColumnTextureInfo;", "getRegistry", "()Lmods/octarinecore/client/resource/ModelRenderRegistry;", "surroundPredicate", "getSurroundPredicate", "BetterFoliage-MC1.12"}
)
public final class RoundLogOverlayLayer extends ColumnRenderLayer {
   @NotNull
   private final Function1<IBlockState, Boolean> blockPredicate = (Function1)null.INSTANCE;
   @NotNull
   private final Function1<IBlockState, Boolean> surroundPredicate = (Function1)null.INSTANCE;

   @NotNull
   public ModelRenderRegistry<ColumnTextureInfo> getRegistry() {
      return (ModelRenderRegistry)LogRegistry.INSTANCE;
   }

   @NotNull
   public Function1<IBlockState, Boolean> getBlockPredicate() {
      return this.blockPredicate;
   }

   @NotNull
   public Function1<IBlockState, Boolean> getSurroundPredicate() {
      return this.surroundPredicate;
   }

   public boolean getConnectSolids() {
      return Config.roundLogs.INSTANCE.getConnectSolids();
   }

   public boolean getLenientConnect() {
      return Config.roundLogs.INSTANCE.getLenientConnect();
   }

   public boolean getDefaultToY() {
      return Config.roundLogs.INSTANCE.getDefaultY();
   }

   public RoundLogOverlayLayer() {
   }
}
