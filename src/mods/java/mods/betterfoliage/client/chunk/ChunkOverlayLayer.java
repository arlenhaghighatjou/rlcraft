package mods.betterfoliage.client.chunk;

import kotlin.Metadata;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\bf\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J\u001d\u0010\u0003\u001a\u00028\u00002\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&¢\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&¨\u0006\u000b"},
   d2 = {"Lmods/betterfoliage/client/chunk/ChunkOverlayLayer;", "T", "", "calculate", "world", "Lnet/minecraft/world/IBlockAccess;", "pos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Object;", "onBlockUpdate", "", "BetterFoliage-MC1.12"}
)
public interface ChunkOverlayLayer<T> {
   T calculate(@NotNull IBlockAccess var1, @NotNull BlockPos var2);

   void onBlockUpdate(@NotNull IBlockAccess var1, @NotNull BlockPos var2);
}
