package com.chaosthedude.realistictorches.blocks;

import com.chaosthedude.realistictorches.config.ConfigHandler;
import com.chaosthedude.realistictorches.items.RealisticTorchesItems;
import java.util.Random;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTorchSmoldering extends BlockRealisticTorch {
   public static final String NAME = "torch_smoldering";

   public BlockTorchSmoldering() {
      this.setUnlocalizedName("realistictorches.torch_smoldering");
      this.setLightLevel(0.65F);
      this.setTickRandomly(true);
      this.setCreativeTab((CreativeTabs)null);
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
            world.scheduleUpdate(pos, this, ConfigHandler.torchBurnout / 10);
         }
      }

   }

   public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
      this.updateTorch(world, pos);
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      this.extinguish(world, pos, false);
   }

   public Item getItemDropped(IBlockState state, Random random, int fortune) {
      return !ConfigHandler.noRelightEnabled ? Item.getItemFromBlock(RealisticTorchesBlocks.torchUnlit) : null;
   }

   public void extinguish(World world, BlockPos pos, boolean extinguishFully) {
      this.playExtinguishSound(world, pos);
      if (!ConfigHandler.noRelightEnabled) {
         world.setBlockState(pos, this.getState(world, pos, RealisticTorchesBlocks.torchUnlit), 2);
      } else {
         world.setBlockToAir(pos);
      }

   }

   public boolean isLit() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
      EnumFacing facing = (EnumFacing)state.getValue(FACING);
      double x = (double)pos.getX() + 0.5D;
      double y = (double)pos.getY() + 0.7D;
      double z = (double)pos.getZ() + 0.5D;
      double mod1 = 0.22D;
      double mod2 = 0.27D;
      int r = rand.nextInt(4);
      if (facing.getAxis().isHorizontal()) {
         EnumFacing opposite = facing.getOpposite();
         world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.27D * (double)opposite.getFrontOffsetX(), y + 0.22D, z + 0.27D * (double)opposite.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
         if (r == 2) {
            world.spawnParticle(EnumParticleTypes.FLAME, x + 0.27D * (double)opposite.getFrontOffsetX(), y + 0.22D, z + 0.27D * (double)opposite.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
         }
      } else {
         if (r == 2) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
