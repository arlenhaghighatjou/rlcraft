package de.Whitedraco.switchbow.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMiniLight extends Block {
   public BlockMiniLight(String Name) {
      super(Material.CIRCUITS, MapColor.AIR);
      this.setTickRandomly(true);
      this.setUnlocalizedName(Name);
      this.setRegistryName(Name);
      this.setLightLevel(1.0F);
      this.setLightOpacity(0);
   }

   public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      return false;
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nullable
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.7D;
      double d2 = (double)pos.getZ() + 0.5D;
      worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
      worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public int quantityDropped(Random random) {
      return 0;
   }
}
