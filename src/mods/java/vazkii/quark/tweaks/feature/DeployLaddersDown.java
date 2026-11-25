package vazkii.quark.tweaks.feature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.decoration.feature.IronLadders;

public class DeployLaddersDown extends Feature {
   private static Method canAttachTo;

   public DeployLaddersDown() {
   }

   private static boolean canAttachTo(Block ladder, World world, BlockPos pos, EnumFacing facing) {
      if (ladder == IronLadders.iron_ladder) {
         return IronLadders.iron_ladder.canBlockStay(world, pos, facing);
      } else {
         if (ladder instanceof BlockLadder) {
            BlockPos attachPos = pos.offset(facing, -1);
            if (canAttachTo == null) {
               canAttachTo = ObfuscationReflectionHelper.findMethod(BlockLadder.class, "canAttachTo", Boolean.TYPE, new Class[]{World.class, BlockPos.class, EnumFacing.class});
            }

            try {
               return ((Boolean)canAttachTo.invoke(ladder, world, attachPos, facing)).booleanValue();
            } catch (InvocationTargetException | IllegalAccessException var6) {
               ;
            }
         }

         return false;
      }
   }

   @SubscribeEvent
   public void onInteract(RightClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      EnumHand hand = event.getHand();
      ItemStack stack = player.getHeldItem(hand);
      List<Item> items = new ArrayList();
      items.add(Item.getItemFromBlock(Blocks.LADDER));
      if (ModuleLoader.isFeatureEnabled(IronLadders.class)) {
         items.add(Item.getItemFromBlock(IronLadders.iron_ladder));
      }

      if (!stack.isEmpty() && items.contains(stack.getItem())) {
         Block block = Block.getBlockFromItem(stack.getItem());
         World world = event.getWorld();

         BlockPos posDown;
         for(BlockPos pos = event.getPos(); world.getBlockState(pos).getBlock() == block; pos = posDown) {
            event.setCanceled(true);
            posDown = pos.down();
            if (world.isOutsideBuildHeight(posDown)) {
               break;
            }

            IBlockState stateDown = world.getBlockState(posDown);
            if (stateDown.getBlock() != block) {
               if (stateDown.getBlock().isAir(stateDown, world, posDown)) {
                  IBlockState copyState = world.getBlockState(pos);
                  EnumFacing facing = (EnumFacing)copyState.getValue(BlockLadder.FACING);
                  if (canAttachTo(block, world, posDown, facing)) {
                     world.setBlockState(posDown, copyState);
                     world.playSound((EntityPlayer)null, (double)posDown.getX(), (double)posDown.getY(), (double)posDown.getZ(), SoundEvents.BLOCK_LADDER_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                     if (world.isRemote) {
                        player.swingArm(hand);
                     }

                     if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                        if (stack.getCount() <= 0) {
                           player.setHeldItem(hand, ItemStack.EMPTY);
                        }
                     }
                  }
               }
               break;
            }
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
