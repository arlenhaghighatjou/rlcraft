package vazkii.quark.world.world.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import vazkii.quark.world.entity.EntityCrab;

public class RaveEventListener implements IWorldEventListener {
   private final World world;

   public RaveEventListener(World world) {
      this.world = world;
   }

   public void playEvent(@Nullable EntityPlayer player, int type, @Nonnull BlockPos pos, int data) {
      if (type == 1010) {
         for(EntityCrab living : this.world.getEntitiesWithinAABB(EntityCrab.class, (new AxisAlignedBB(pos)).grow(3.0D))) {
            living.party(pos, data != 0);
         }
      }

   }

   public void notifyBlockUpdate(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState, int flags) {
   }

   public void notifyLightSet(@Nonnull BlockPos pos) {
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void playRecord(@Nonnull SoundEvent soundIn, @Nonnull BlockPos pos) {
   }

   public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
   }

   public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
   }

   public void onEntityAdded(@Nonnull Entity entityIn) {
   }

   public void onEntityRemoved(@Nonnull Entity entityIn) {
   }

   public void broadcastSound(int soundID, @Nonnull BlockPos pos, int data) {
   }

   public void sendBlockBreakProgress(int breakerId, @Nonnull BlockPos pos, int progress) {
   }
}
