package mods.octarinecore.client.resource;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.common.Int3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraft.world.storage.WorldInfo;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0019\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0086\u0002J\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0086\u0002J\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000fH\u0086\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b¨\u0006\u0014"},
   d2 = {"Lmods/octarinecore/client/resource/SimplexNoise;", "Lmods/octarinecore/client/resource/IWorldLoadListener;", "()V", "noise", "Lnet/minecraft/world/gen/NoiseGeneratorSimplex;", "getNoise", "()Lnet/minecraft/world/gen/NoiseGeneratorSimplex;", "setNoise", "(Lnet/minecraft/world/gen/NoiseGeneratorSimplex;)V", "get", "", "x", "z", "pos", "Lmods/octarinecore/common/Int3;", "Lnet/minecraft/util/math/BlockPos;", "onWorldLoad", "", "world", "Lnet/minecraft/world/World;", "BetterFoliage-MC1.12"}
)
public final class SimplexNoise implements IWorldLoadListener {
   @NotNull
   private NoiseGeneratorSimplex noise = new NoiseGeneratorSimplex();

   @NotNull
   public final NoiseGeneratorSimplex getNoise() {
      return this.noise;
   }

   public final void setNoise(@NotNull NoiseGeneratorSimplex var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.noise = <set-?>;
   }

   public void onWorldLoad(@NotNull World world) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      WorldInfo var10005 = world.getWorldInfo();
      Intrinsics.checkExpressionValueIsNotNull(var10005, "world.worldInfo");
      this.noise = new NoiseGeneratorSimplex(new EaglercraftRandom(var10005.getSeed()));
   }

   public final int get(int x, int z) {
      return MathHelper.floor((this.noise.getValue((double)x, (double)z) + 1.0D) * 32.0D);
   }

   public final int get(@NotNull Int3 pos) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      return this.get(pos.getX(), pos.getZ());
   }

   public final int get(@NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      return this.get(pos.getX(), pos.getZ());
   }

   public SimplexNoise() {
   }
}
