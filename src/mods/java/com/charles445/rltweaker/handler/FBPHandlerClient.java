package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.FBPClientReflect;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPHandlerClient {
   FBPClientReflect reflector;
   @Nullable
   FBPHandlerClient.FBPGhostListener listener = null;

   public FBPHandlerClient() {
      try {
         this.reflector = new FBPClientReflect();
         if (ModConfig.client.fbp.fixPlacementGhostBlocks) {
            this.listener = new FBPHandlerClient.FBPGhostListener();
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup FBPHandlerClient!", var2);
         ErrorUtil.logSilent("FBP Client Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public class FBPGhostListener implements IWorldEventListener {
      public FBPGhostListener() {
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public void onWorldLoadEvent(Load event) {
         event.getWorld().addEventListener(this);
      }

      public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
         if (newState.getBlock() == FBPHandlerClient.this.reflector.o_FBPBlock && oldState.getBlock() == Blocks.AIR) {
            try {
               Object blockNode = FBPHandlerClient.this.reflector.getBlockNode(pos);
               if (blockNode != null) {
                  FBPHandlerClient.this.reflector.setBlockNodeToAir(blockNode);
               }
            } catch (IllegalAccessException | IllegalArgumentException var7) {
               ErrorUtil.logSilent("FBP BlockNodeToAir Invocation");
            }
         }

      }

      public void notifyLightSet(BlockPos pos) {
      }

      public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      }

      public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
      }

      public void playRecord(SoundEvent soundIn, BlockPos pos) {
      }

      public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      }

      public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      }

      public void onEntityAdded(Entity entityIn) {
      }

      public void onEntityRemoved(Entity entityIn) {
      }

      public void broadcastSound(int soundID, BlockPos pos, int data) {
      }

      public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
      }

      public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      }
   }
}
