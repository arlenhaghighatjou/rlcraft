package com.teamacronymcoders.base.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.items.ItemBaseNoModel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public class TinkersUtils {
   public TinkersUtils() {
   }

   private static boolean canBreakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
      if (world.isAirBlock(pos)) {
         return false;
      } else {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         if (!isToolEffective(stack, state)) {
            return false;
         } else {
            IBlockState refState = world.getBlockState(refPos);
            float refStrength = ForgeHooks.blockStrength(refState, player, world, refPos);
            float strength = ForgeHooks.blockStrength(state, player, world, pos);
            if (ForgeHooks.canHarvestBlock(block, player, world, pos) && refStrength / strength <= 10.0F) {
               if (player.capabilities.isCreativeMode) {
                  block.onBlockHarvested(world, pos, state, player);
                  if (block.removedByPlayer(state, world, pos, player, false)) {
                     block.onBlockDestroyedByPlayer(world, pos, state);
                  }

                  if (!world.isRemote) {
                     world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 2);
                  }

                  return false;
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public static void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
      if (canBreakExtraBlock(stack, world, player, pos, refPos)) {
         Base.instance.getLogger().devInfo("Could break block");
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         stack.onBlockDestroyed(world, state, pos, player);
         if (!world.isRemote) {
            int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP)player).interactionManager.getGameType(), (EntityPlayerMP)player, pos);
            if (xp == -1) {
               return;
            }

            TileEntity tileEntity = world.getTileEntity(pos);
            if (block.removedByPlayer(state, world, pos, player, true)) {
               block.onBlockDestroyedByPlayer(world, pos, state);
               block.harvestBlock(world, player, pos, state, tileEntity, stack);
               block.dropXpOnBlockBreak(world, pos, xp);
            }

            world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 2);
         } else {
            world.playBroadcastSound(2001, pos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, pos, player, true)) {
               block.onBlockDestroyedByPlayer(world, pos, state);
            }

            stack.onBlockDestroyed(world, state, pos, player);
            if (stack.getCount() == 0 && stack == player.getHeldItemMainhand()) {
               ForgeEventFactory.onPlayerDestroyItem(player, stack, EnumHand.MAIN_HAND);
               player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }

            NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getConnection();

            assert netHandlerPlayClient != null;

            netHandlerPlayClient.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
         }

      }
   }

   public static boolean isToolEffective(ItemStack stack, IBlockState state) {
      for(String type : stack.getItem().getToolClasses(stack)) {
         if (state.getBlock().isToolEffective(type, state)) {
            return true;
         }
      }

      return false;
   }

   public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin, int width, int height, int depth, int distance) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemBaseNoModel) {
         IBlockState state = world.getBlockState(origin);
         if (!isToolEffective(stack, state)) {
            return ImmutableList.of();
         } else {
            RayTraceResult mop = ((ItemBaseNoModel)stack.getItem()).rayTrace(world, player, true);
            if (mop == null || !origin.equals(mop.getBlockPos())) {
               mop = ((ItemBaseNoModel)stack.getItem()).rayTrace(world, player, false);
               if (mop == null || !origin.equals(mop.getBlockPos())) {
                  return ImmutableList.of();
               }
            }

            BlockPos start = origin;
            int x;
            int y;
            int z;
            switch(mop.sideHit) {
            case DOWN:
            case UP:
               Vec3i vec = player.getHorizontalFacing().getDirectionVec();
               x = vec.getX() * height + vec.getZ() * width;
               y = mop.sideHit.getAxisDirection().getOffset() * -depth;
               z = vec.getX() * width + vec.getZ() * height;
               start = origin.add(-x / 2, 0, -z / 2);
               if (x % 2 == 0) {
                  if (x > 0 && mop.hitVec.x - (double)mop.getBlockPos().getX() > 0.5D) {
                     start = start.add(1, 0, 0);
                  } else if (x < 0 && mop.hitVec.x - (double)mop.getBlockPos().getX() < 0.5D) {
                     start = start.add(-1, 0, 0);
                  }
               }

               if (z % 2 == 0) {
                  if (z > 0 && mop.hitVec.z - (double)mop.getBlockPos().getZ() > 0.5D) {
                     start = start.add(0, 0, 1);
                  } else if (z < 0 && mop.hitVec.z - (double)mop.getBlockPos().getZ() < 0.5D) {
                     start = start.add(0, 0, -1);
                  }
               }
               break;
            case NORTH:
            case SOUTH:
               x = width;
               y = height;
               z = mop.sideHit.getAxisDirection().getOffset() * -depth;
               start = origin.add(-width / 2, -height / 2, 0);
               if (width % 2 == 0 && mop.hitVec.x - (double)mop.getBlockPos().getX() > 0.5D) {
                  start = start.add(1, 0, 0);
               }

               if (height % 2 == 0 && mop.hitVec.y - (double)mop.getBlockPos().getY() > 0.5D) {
                  start = start.add(0, 1, 0);
               }
               break;
            case WEST:
            case EAST:
               x = mop.sideHit.getAxisDirection().getOffset() * -depth;
               y = height;
               z = width;
               start = origin.add(0, -height / 2, -width / 2);
               if (height % 2 == 0 && mop.hitVec.y - (double)mop.getBlockPos().getY() > 0.5D) {
                  start = start.add(0, 1, 0);
               }

               if (width % 2 == 0 && mop.hitVec.z - (double)mop.getBlockPos().getZ() > 0.5D) {
                  start = start.add(0, 0, 1);
               }
               break;
            default:
               z = 0;
               y = 0;
               x = 0;
            }

            Builder<BlockPos> builder = ImmutableList.builder();

            for(int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
               for(int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
                  for(int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
                     if ((xp != origin.getX() || yp != origin.getY() || zp != origin.getZ()) && (distance <= 0 || MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(zp - origin.getZ()) <= distance)) {
                        BlockPos pos = new BlockPos(xp, yp, zp);
                        if (isToolEffective(stack, world.getBlockState(pos))) {
                           builder.add(pos);
                        }
                     }
                  }
               }
            }

            return builder.build();
         }
      } else {
         return ImmutableList.of();
      }
   }
}
