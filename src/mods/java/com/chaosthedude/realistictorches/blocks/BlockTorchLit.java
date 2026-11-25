package com.chaosthedude.realistictorches.blocks;

import com.chaosthedude.realistictorches.config.ConfigHandler;
import com.chaosthedude.realistictorches.items.RealisticTorchesItems;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTorchLit extends BlockRealisticTorch {
   public static final String NAME = "torch_lit";

   public BlockTorchLit() {
      this.setUnlocalizedName("realistictorches.torch_lit");
      this.setLightLevel(0.9375F);
      this.setTickRandomly(true);
      this.setLit(true);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float side, float hitX, float hitY) {
      if (player.getHeldItem(hand).getItem() == RealisticTorchesItems.glowstonePaste) {
         world.setBlockState(pos, this.getState(world, pos, (BlockTorch)Blocks.TORCH));
         if (!player.isCreative()) {
            player.getHeldItem(hand).shrink(1);
         }

         return true;
      } else {
         return super.onBlockActivated(world, pos, state, player, hand, facing, side, hitX, hitY);
      }
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (this.canBurnout()) {
         if (world.isRainingAt(pos)) {
            this.extinguish(world, pos, true);
         } else {
            world.scheduleUpdate(pos, this, (int)((double)ConfigHandler.torchBurnout * 0.9D));
         }
      }

   }

   public void randomTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom random) {
      this.updateTorch(world, pos);
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      this.extinguish(world, pos, false);
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom random, int fortune) {
      return !ConfigHandler.noRelightEnabled ? Item.getItemFromBlock(RealisticTorchesBlocks.torchUnlit) : null;
   }

   public void extinguish(World world, BlockPos pos, boolean extinguishFully) {
      this.playExtinguishSound(world, pos);
      if (!extinguishFully) {
         world.setBlockState(pos, this.getState(world, pos, RealisticTorchesBlocks.torchSmoldering), 2);
      } else if (!ConfigHandler.noRelightEnabled) {
         world.setBlockState(pos, this.getState(world, pos, RealisticTorchesBlocks.torchUnlit), 2);
      } else {
         world.setBlockToAir(pos);
      }

   }
}
