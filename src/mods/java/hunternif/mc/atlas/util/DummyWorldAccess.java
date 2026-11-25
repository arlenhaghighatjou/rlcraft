package hunternif.mc.atlas.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class DummyWorldAccess implements IWorldEventListener {
   public DummyWorldAccess() {
   }

   public void notifyLightSet(BlockPos pos) {
   }

   public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {
   }

   public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_180442_15_) {
   }

   public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
   }

   public void onEntityAdded(Entity entity) {
   }

   public void onEntityRemoved(Entity entity) {
   }

   public void broadcastSound(int p_82746_1_, BlockPos pos, int p_82746_5_) {
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
   }

   public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
   }

   public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void playRecord(SoundEvent soundIn, BlockPos pos) {
   }

   public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
   }
}
