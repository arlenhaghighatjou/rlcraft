package knightminer.inspirations.tweaks.client;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class PortalColorHandler implements IBlockColor, IWorldEventListener {
   private static final int DEFAULT_COLOR = 10037503;
   private static final Set<Block> BEACON_COLOR_BLACKLIST = new HashSet();
   public static final PortalColorHandler INSTANCE = new PortalColorHandler();

   private PortalColorHandler() {
   }

   public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
      if (world != null && pos != null) {
         for(pos = pos.down(); world.getBlockState(pos).getBlock() == Blocks.PORTAL; pos = pos.down()) {
            ;
         }

         return getColorValue(world, pos.down());
      } else {
         return 10037503;
      }
   }

   private static int getColorValue(IBlockAccess access, BlockPos pos) {
      IBlockState state = access.getBlockState(pos);
      Block block = state.getBlock();
      if (block == Blocks.STAINED_GLASS) {
         return ((EnumDyeColor)state.getValue(BlockStainedGlass.COLOR)).colorValue;
      } else if (block == Blocks.STAINED_GLASS_PANE) {
         return ((EnumDyeColor)state.getValue(BlockStainedGlassPane.COLOR)).colorValue;
      } else {
         if (!BEACON_COLOR_BLACKLIST.contains(block)) {
            World world = null;
            if (access instanceof World) {
               world = (World)access;
            } else if (access instanceof ChunkCache) {
               world = ((ChunkCache)access).world;
            }

            if (world != null) {
               try {
                  float[] color = block.getBeaconColorMultiplier(state, world, pos, pos);
                  if (color != null && color.length == 3) {
                     return Util.getColorInteger(color);
                  }
               } catch (ClassCastException var6) {
                  Inspirations.log.error("Error getting beacon color for block", var6);
                  BEACON_COLOR_BLACKLIST.add(block);
               }
            }
         }

         return 10037503;
      }
   }

   public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
      if (Minecraft.getMinecraft().renderGlobal.viewFrustum != null) {
         pos = pos.up(2);
         if (world.getBlockState(pos).getBlock() == Blocks.PORTAL) {
            this.updatePortal(world, pos, flags);
         }

      }
   }

   private void updatePortal(World world, BlockPos pos, int flags) {
      RenderGlobal render = Minecraft.getMinecraft().renderGlobal;
      int relative = pos.getY() & 15;
      if (relative == 0) {
         render.notifyBlockUpdate((World)null, pos, (IBlockState)null, (IBlockState)null, flags);
      }

      BlockPos update = pos.up(16 - relative);
      if (world.getBlockState(update).getBlock() == Blocks.PORTAL) {
         render.notifyBlockUpdate((World)null, update, (IBlockState)null, (IBlockState)null, flags);
         if (relative >= 12) {
            update = update.up(16);
            if (world.getBlockState(update).getBlock() == Blocks.PORTAL) {
               render.notifyBlockUpdate((World)null, update, (IBlockState)null, (IBlockState)null, flags);
            }
         }
      }

   }

   public void notifyLightSet(BlockPos pos) {
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void playRecord(SoundEvent soundIn, BlockPos pos) {
   }

   public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
   }

   public void spawnParticle(int id, boolean ignoreRange, boolean b, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
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
