package com.chaosthedude.realistictorches.blocks;

import com.chaosthedude.realistictorches.config.ConfigHandler;
import com.chaosthedude.realistictorches.items.RealisticTorchesItems;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRealisticTorch extends BlockTorch {
   private boolean isLit;

   public BlockRealisticTorch() {
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float side, float hitX, float hitY) {
      return this.lightTorch(world, pos, player, player.getHeldItem(hand));
   }

   public void extinguish(World world, BlockPos pos, boolean extinguishFully) {
   }

   public boolean lightTorch(World world, BlockPos pos, EntityPlayer player, ItemStack heldItem) {
      if (world.getBlockState(pos).getBlock() instanceof BlockRealisticTorch && !heldItem.isEmpty() && (heldItem.getItem() == Items.FLINT_AND_STEEL || ConfigHandler.matchboxCreatesFire && heldItem.getItem() == RealisticTorchesItems.matchbox) && (!ConfigHandler.noRelightEnabled || !this.isLit())) {
         heldItem.damageItem(1, player);
         this.playIgniteSound(world, pos);
         if (!world.isRainingAt(pos)) {
            world.setBlockState(pos, this.getState(world, pos, RealisticTorchesBlocks.torchLit), 2);
         }

         return true;
      } else {
         return false;
      }
   }

   public void updateTorch(World world, BlockPos pos) {
      if (this.canBurnout() && world.isRainingAt(pos) && this.isLit()) {
         this.extinguish(world, pos, true);
      }

   }

   public IBlockState getState(World world, BlockPos pos, BlockTorch torch) {
      return torch.getStateFromMeta(torch.getMetaFromState(world.getBlockState(pos)));
   }

   public void playIgniteSound(World world, BlockPos pos) {
      world.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public void playExtinguishSound(World world, BlockPos pos) {
      world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public BlockRealisticTorch setLit(boolean lit) {
      this.isLit = lit;
      return this;
   }

   public boolean isLit() {
      return this.isLit;
   }

   public boolean canBurnout() {
      return ConfigHandler.torchBurnout > 0;
   }
}
