package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.model.FBPModelHelper;
import com.TominoCZ.FBP.node.BlockNode;
import com.TominoCZ.FBP.node.BlockPosNode;
import com.TominoCZ.FBP.particle.FBPParticleBlock;
import com.TominoCZ.FBP.particle.FBPParticleManager;
import com.TominoCZ.FBP.renderer.FBPWeatherRenderer;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging.Factory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FBPEventHandler {
   Minecraft mc = Minecraft.getMinecraft();
   IWorldEventListener listener = new IWorldEventListener() {
      public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      }

      public void broadcastSound(int soundID, BlockPos pos, int data) {
      }

      public void onEntityAdded(Entity entityIn) {
      }

      public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      }

      public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
      }

      public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      }

      public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
      }

      public void playRecord(SoundEvent soundIn, BlockPos pos) {
      }

      public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
      }

      public void onEntityRemoved(Entity entityIn) {
      }

      public void notifyLightSet(BlockPos pos) {
      }

      public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
         if (FBP.enabled && FBP.fancyPlaceAnim && (flags == 11 || flags == 3) && !oldState.equals(newState)) {
            BlockPosNode node = FBPEventHandler.this.getNodeWithPos(pos);
            if (node != null && !node.checked) {
               if (newState.getBlock() == FBP.FBPBlock || newState.getBlock() == Blocks.AIR || oldState.getBlock() == newState.getBlock()) {
                  FBPEventHandler.this.removePosEntry(pos);
                  return;
               }

               IBlockState state = newState.getActualState(worldIn, pos);
               if (state.getBlock() instanceof BlockDoublePlant || !FBPModelHelper.isModelValid(state)) {
                  FBPEventHandler.this.removePosEntry(pos);
                  return;
               }

               long seed = MathHelper.getPositionRandom(pos);
               boolean isNotFalling = true;
               if (state.getBlock() instanceof BlockFalling) {
                  BlockFalling bf = (BlockFalling)state.getBlock();
                  if (BlockFalling.canFallThrough(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)))) {
                     isNotFalling = false;
                  }
               }

               if (!FBP.INSTANCE.isBlacklisted(state.getBlock(), false) && isNotFalling) {
                  node.checked = true;
                  FBPParticleBlock p = new FBPParticleBlock(worldIn, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), state, seed);
                  FBPEventHandler.this.mc.effectRenderer.addEffect(p);
                  FBP.FBPBlock.copyState(worldIn, pos, state, p);
               }
            }
         }

      }
   };
   ConcurrentSet<BlockPosNode> list = new ConcurrentSet();

   public FBPEventHandler() {
   }

   @SubscribeEvent
   public void onInteractionEvent(RightClickBlock e) {
      if (e.getHitVec() != null && e.getItemStack() != null && e.getWorld().isRemote && e.getItemStack().getItem() instanceof ItemBlock) {
         BlockPos pos = e.getPos();
         BlockPos pos_o = e.getPos().offset(e.getFace());
         Block inHand = null;
         IBlockState atPos = e.getWorld().getBlockState(pos);
         IBlockState offset = e.getWorld().getBlockState(pos_o);
         boolean bool = false;
         float f = (float)(e.getHitVec().x - (double)pos.getX());
         float f1 = (float)(e.getHitVec().y - (double)pos.getY());
         float f2 = (float)(e.getHitVec().z - (double)pos.getZ());
         if (atPos.getBlock() == FBP.FBPBlock) {
            BlockNode n = (BlockNode)FBP.FBPBlock.blockNodes.get(pos);
            if (n != null && n.state.getBlock() != null) {
               boolean activated = n.originalBlock.onBlockActivated(e.getWorld(), pos, n.state, this.mc.player, e.getHand(), e.getFace(), f, f1, f2);
               if (activated) {
                  return;
               }

               atPos = n.state;
            }

            if (atPos.getBlock() instanceof BlockSlab) {
               EnumBlockHalf half = (EnumBlockHalf)atPos.getValue(BlockSlab.HALF);
               if (e.getFace() == EnumFacing.UP) {
                  if (half == EnumBlockHalf.BOTTOM) {
                     bool = true;
                  }
               } else if (e.getFace() == EnumFacing.DOWN && half == EnumBlockHalf.TOP) {
                  bool = true;
               }
            }
         }

         if (offset.getBlock() == FBP.FBPBlock) {
            BlockNode n = (BlockNode)FBP.FBPBlock.blockNodes.get(pos_o);
            if (n != null && n.state.getBlock() != null) {
               offset = n.state;
            }
         }

         if (e.getItemStack() != null && e.getItemStack().getItem() != null) {
            inHand = Block.getBlockFromItem(e.getItemStack().getItem());
         }

         boolean addedOffset = false;
         BlockPosNode node = new BlockPosNode();

         try {
            if (!bool && inHand != null && offset.getMaterial().isReplaceable() && !atPos.getBlock().isReplaceable(e.getWorld(), pos) && inHand.canPlaceBlockAt(e.getWorld(), pos_o)) {
               node.add(pos_o);
               addedOffset = true;
            } else {
               node.add(pos);
            }

            boolean okToAdd = inHand != null && inHand != Blocks.AIR && inHand.canPlaceBlockAt(e.getWorld(), addedOffset ? pos_o : pos);
            if (inHand != null && inHand instanceof BlockTorch) {
               BlockTorch bt = (BlockTorch)inHand;
               if (!bt.canPlaceBlockAt(e.getWorld(), pos_o)) {
                  okToAdd = false;
               }

               if (atPos.getBlock() == Blocks.TORCH) {
                  for(EnumFacing fc : EnumFacing.VALUES) {
                     BlockPos p = pos_o.offset(fc);
                     Block bl = e.getWorld().getBlockState(p).getBlock();
                     if (bl != Blocks.TORCH && bl != FBP.FBPBlock && bl.isSideSolid(bl.getDefaultState(), e.getWorld(), p, fc)) {
                        okToAdd = true;
                        break;
                     }

                     okToAdd = false;
                  }
               }
            }

            BlockPosNode last = this.getNodeWithPos(pos);
            BlockPosNode last_o = this.getNodeWithPos(pos_o);
            if (okToAdd) {
               boolean replaceable = (addedOffset ? offset : atPos).getBlock().isReplaceable(e.getWorld(), addedOffset ? pos_o : pos);
               if (last != null && !addedOffset && last.checked) {
                  return;
               }

               if (last_o != null && addedOffset && (last_o.checked || replaceable)) {
                  return;
               }

               Chunk c = this.mc.world.getChunkFromBlockCoords(addedOffset ? pos_o : pos);
               c.resetRelightChecks();
               c.setLightPopulated(true);
               this.list.add(node);
            }
         } catch (Throwable var21) {
            this.list.clear();
         }

      }
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent e) {
      if (!this.mc.isGamePaused() && this.mc.world != null && this.mc.world.provider.getWeatherRenderer() == FBP.fancyWeatherRenderer && FBP.enabled) {
         ((FBPWeatherRenderer)FBP.fancyWeatherRenderer).onUpdate();
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onWorldLoadEvent(Load e) {
      FBPConfigHandler.init();
      e.getWorld().addEventListener(this.listener);
      this.list.clear();
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onEntityJoinWorldEvent(EntityJoinWorldEvent e) {
      if (e.getEntity() == this.mc.player) {
         FBP.fancyEffectRenderer = new FBPParticleManager(e.getWorld(), this.mc.renderEngine, new Factory());
         FBP.fancyWeatherRenderer = new FBPWeatherRenderer();
         IRenderHandler currentWeatherRenderer = this.mc.world.provider.getCloudRenderer();
         if (FBP.originalWeatherRenderer == null || FBP.originalWeatherRenderer != currentWeatherRenderer && currentWeatherRenderer != FBP.fancyWeatherRenderer) {
            FBP.originalWeatherRenderer = currentWeatherRenderer;
         }

         if (FBP.originalEffectRenderer == null || FBP.originalEffectRenderer != this.mc.effectRenderer && FBP.originalEffectRenderer != FBP.fancyEffectRenderer) {
            FBP.originalEffectRenderer = this.mc.effectRenderer;
         }

         if (FBP.enabled) {
            this.mc.effectRenderer = FBP.fancyEffectRenderer;
            if (FBP.fancyRain || FBP.fancySnow) {
               this.mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onPlayerPlaceBlockEvent(PlaceEvent e) {
      IBlockState bs = e.getPlacedBlock();
      Block placed = bs.getBlock();
      if (placed == FBP.FBPBlock) {
         e.setCanceled(true);
      }

   }

   BlockPosNode getNodeWithPos(BlockPos pos) {
      for(BlockPosNode n : this.list) {
         if (n.hasPos(pos)) {
            return n;
         }
      }

      return null;
   }

   public void removePosEntry(BlockPos pos) {
      for(int i = 0; i < this.list.size(); ++i) {
         BlockPosNode n = this.getNodeWithPos(pos);
         if (n != null) {
            this.list.remove(n);
         }
      }

   }
}
