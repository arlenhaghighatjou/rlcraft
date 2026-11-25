package mods.betterfoliage.client.chunk;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0015\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0016J8\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0016J\u0010\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J*\u0010\u0015\u001a\u00020\u00032\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0016J\u001a\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u0006\u001a\u00020\u0007H\u0016JJ\u0010\u001d\u001a\u00020\u00032\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\u0006\u0010#\u001a\u00020!2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%H\u0016J \u0010'\u001a\u00020\u00032\u0006\u0010(\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010)\u001a\u00020\u0005H\u0016J\\\u0010*\u001a\u00020\u00032\u0006\u0010+\u001a\u00020\u00052\u0006\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020-2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\u0006\u0010#\u001a\u00020!2\u0006\u0010/\u001a\u00020!2\u0006\u00100\u001a\u00020!2\u0006\u00101\u001a\u00020!2\n\u00102\u001a\u000203\"\u00020\u0005H\u0016JT\u0010*\u001a\u00020\u00032\u0006\u00104\u001a\u00020\u00052\u0006\u0010,\u001a\u00020-2\u0006\u00105\u001a\u00020!2\u0006\u00106\u001a\u00020!2\u0006\u00107\u001a\u00020!2\u0006\u0010/\u001a\u00020!2\u0006\u00100\u001a\u00020!2\u0006\u00101\u001a\u00020!2\n\u00102\u001a\u000203\"\u00020\u0005H\u0016\u00a8\u00068"},
   d2 = {"Lmods/betterfoliage/client/chunk/IBlockUpdateListener;", "Lnet/minecraft/world/IWorldEventListener;", "broadcastSound", "", "soundID", "", "pos", "Lnet/minecraft/util/math/BlockPos;", "data", "markBlockRangeForRenderUpdate", "x1", "y1", "z1", "x2", "y2", "z2", "notifyLightSet", "onEntityAdded", "entityIn", "Lnet/minecraft/entity/Entity;", "onEntityRemoved", "playEvent", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "type", "blockPosIn", "playRecord", "soundIn", "Lnet/minecraft/util/SoundEvent;", "playSoundToAllNearExcept", "category", "Lnet/minecraft/util/SoundCategory;", "x", "", "y", "z", "volume", "", "pitch", "sendBlockBreakProgress", "breakerId", "progress", "spawnParticle", "id", "ignoreRange", "", "minimiseParticleLevel", "xSpeed", "ySpeed", "zSpeed", "parameters", "", "particleID", "xCoord", "yCoord", "zCoord", "BetterFoliage-MC1.12"}
)
public interface IBlockUpdateListener extends IWorldEventListener {
   void playSoundToAllNearExcept(@Nullable EntityPlayer var1, @NotNull SoundEvent var2, @NotNull SoundCategory var3, double var4, double var6, double var8, float var10, float var11);

   void onEntityAdded(@NotNull Entity var1);

   void broadcastSound(int var1, @NotNull BlockPos var2, int var3);

   void playEvent(@Nullable EntityPlayer var1, int var2, @NotNull BlockPos var3, int var4);

   void onEntityRemoved(@NotNull Entity var1);

   void notifyLightSet(@NotNull BlockPos var1);

   void spawnParticle(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, @NotNull int... var15);

   void spawnParticle(int var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14, @NotNull int... var16);

   void playRecord(@Nullable SoundEvent var1, @NotNull BlockPos var2);

   void sendBlockBreakProgress(int var1, @NotNull BlockPos var2, int var3);

   void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6);

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      public static void playSoundToAllNearExcept(IBlockUpdateListener $this, @Nullable EntityPlayer player, @NotNull SoundEvent soundIn, @NotNull SoundCategory category, double x, double y, double z, float volume, float pitch) {
         Intrinsics.checkParameterIsNotNull(soundIn, "soundIn");
         Intrinsics.checkParameterIsNotNull(category, "category");
      }

      public static void onEntityAdded(IBlockUpdateListener $this, @NotNull Entity entityIn) {
         Intrinsics.checkParameterIsNotNull(entityIn, "entityIn");
      }

      public static void broadcastSound(IBlockUpdateListener $this, int soundID, @NotNull BlockPos pos, int data) {
         Intrinsics.checkParameterIsNotNull(pos, "pos");
      }

      public static void playEvent(IBlockUpdateListener $this, @Nullable EntityPlayer player, int type, @NotNull BlockPos blockPosIn, int data) {
         Intrinsics.checkParameterIsNotNull(blockPosIn, "blockPosIn");
      }

      public static void onEntityRemoved(IBlockUpdateListener $this, @NotNull Entity entityIn) {
         Intrinsics.checkParameterIsNotNull(entityIn, "entityIn");
      }

      public static void notifyLightSet(IBlockUpdateListener $this, @NotNull BlockPos pos) {
         Intrinsics.checkParameterIsNotNull(pos, "pos");
      }

      public static void spawnParticle(IBlockUpdateListener $this, int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, @NotNull int... parameters) {
         Intrinsics.checkParameterIsNotNull(parameters, "parameters");
      }

      public static void spawnParticle(IBlockUpdateListener $this, int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @NotNull int... parameters) {
         Intrinsics.checkParameterIsNotNull(parameters, "parameters");
      }

      public static void playRecord(IBlockUpdateListener $this, @Nullable SoundEvent soundIn, @NotNull BlockPos pos) {
         Intrinsics.checkParameterIsNotNull(pos, "pos");
      }

      public static void sendBlockBreakProgress(IBlockUpdateListener $this, int breakerId, @NotNull BlockPos pos, int progress) {
         Intrinsics.checkParameterIsNotNull(pos, "pos");
      }

      public static void markBlockRangeForRenderUpdate(IBlockUpdateListener $this, int x1, int y1, int z1, int x2, int y2, int z2) {
      }
   }
}
