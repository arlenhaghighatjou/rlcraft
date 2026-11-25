package mods.octarinecore.client.resource;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.common.Int3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J\u0018\u0010\u0003\u001a\u0004\u0018\u00018\u00002\u0006\u0010\u0004\u001a\u00020\u0005H\u0096\u0002\u00a2\u0006\u0002\u0010\u0006J(\u0010\u0003\u001a\u0004\u0018\u00018\u00002\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u00a6\u0002\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000e"},
   d2 = {"Lmods/octarinecore/client/resource/ModelRenderRegistry;", "T", "", "get", "ctx", "Lmods/octarinecore/client/render/BlockContext;", "(Lmods/octarinecore/client/render/BlockContext;)Ljava/lang/Object;", "state", "Lnet/minecraft/block/state/IBlockState;", "world", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Object;", "BetterFoliage-MC1.12"}
)
public interface ModelRenderRegistry<T> {
   @Nullable
   T get(@NotNull BlockContext var1);

   @Nullable
   T get(@NotNull IBlockState var1, @NotNull IBlockAccess var2, @NotNull BlockPos var3);

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      @Nullable
      public static <T> T get(ModelRenderRegistry<T> $this, @NotNull BlockContext ctx) {
         Intrinsics.checkParameterIsNotNull(ctx, "ctx");
         IBlockState var10001 = ctx.blockState(Int3.Companion.getZero());
         Intrinsics.checkExpressionValueIsNotNull(var10001, "ctx.blockState(Int3.zero)");
         IBlockAccess var10002 = ctx.getWorld();
         if (var10002 == null) {
            Intrinsics.throwNpe();
         }

         return (T)$this.get(var10001, var10002, ctx.getPos());
      }
   }
}
