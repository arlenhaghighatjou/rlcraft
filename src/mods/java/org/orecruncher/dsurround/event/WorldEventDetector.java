package org.orecruncher.dsurround.event;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

@SideOnly(Side.CLIENT)
public class WorldEventDetector implements IWorldEventListener {
   protected final World world;

   public WorldEventDetector(@Nonnull World world) {
      this.world = world;
   }

   public void notifyBlockUpdate(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState, int flags) {
      if (worldIn.provider.getDimension() == EnvironStateHandler.EnvironState.getDimensionId()) {
         BlockUpdateEvent event = new BlockUpdateEvent(worldIn, pos, oldState, newState, flags);
         MinecraftForge.EVENT_BUS.post(event);
      }

   }

   public void notifyLightSet(@Nonnull BlockPos pos) {
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   public void playSoundToAllNearExcept(EntityPlayer player, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void playRecord(@Nonnull SoundEvent soundIn, @Nonnull BlockPos pos) {
   }

   public void spawnParticle(int particleID, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
   }

   public void onEntityAdded(@Nonnull Entity entityIn) {
   }

   public void onEntityRemoved(@Nonnull Entity entityIn) {
   }

   public void broadcastSound(int soundID, @Nonnull BlockPos pos, int data) {
   }

   public void playEvent(@Nonnull EntityPlayer player, int type, @Nonnull BlockPos blockPosIn, int data) {
   }

   public void sendBlockBreakProgress(int breakerId, @Nonnull BlockPos pos, int progress) {
   }

   public void spawnParticle(int p_190570_1_, boolean p_190570_2_, boolean p_190570_3_, double p_190570_4_, double p_190570_6_, double p_190570_8_, double p_190570_10_, double p_190570_12_, double p_190570_14_, @Nonnull int... p_190570_16_) {
   }

   @SubscribeEvent
   public static void onWorldLoad(Load event) {
      if (event.getWorld().isRemote) {
         try {
            event.getWorld().addEventListener(new WorldEventDetector(event.getWorld()));
         } catch (Throwable var2) {
            ModBase.log().warn("Unable to add world listener - is world fake? [%s]", new Object[]{event.getWorld().getClass().getName()});
         }

      }
   }
}
