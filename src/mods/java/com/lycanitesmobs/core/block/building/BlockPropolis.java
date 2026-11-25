package com.lycanitesmobs.core.block.building;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.entity.creature.EntityVespidQueen;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPropolis extends BlockBase {
   public BlockPropolis() {
      super(Material.CLAY);
      this.setCreativeTab(LycanitesMobs.blocksTab);
      this.group = LycanitesMobs.modInfo;
      this.blockName = "propolis";
      this.setup();
      this.setHardness(0.6F);
      this.setHarvestLevel("shovel", 0);
      this.setSoundType(SoundType.GROUND);
      this.tickRate = 100;
      this.removeOnTick = true;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BlockVeswax.HIVE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(BlockVeswax.HIVE)).intValue();
   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockVeswax.HIVE});
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack) {
      int orientationMeta = placer.getHorizontalFacing().getOpposite().getIndex();
      orientationMeta = orientationMeta + 8;
      world.setBlockState(pos, state.withProperty(BlockVeswax.HIVE, Integer.valueOf(orientationMeta)), 2);
      super.onBlockPlacedBy(world, pos, state, placer, itemStack);
   }

   public int tickRate(World world) {
      return this.tickRate + world.rand.nextInt(100);
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom random) {
      if (!world.isRemote) {
         if (this.getMetaFromState(state) < 8) {
            double range = 32.0D;
            if (world.getEntitiesWithinAABB(EntityVespidQueen.class, new AxisAlignedBB((double)pos.getX() - range, (double)pos.getY() - range, (double)pos.getZ() - range, (double)pos.getX() + range, (double)pos.getY() + range, (double)pos.getZ() + range)).isEmpty()) {
               super.updateTick(world, pos, state, random);
            }
         }
      }
   }
}
