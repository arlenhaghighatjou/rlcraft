package lykrast.defiledlands.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHealingPad extends BlockCorrupted {
   protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

   public BlockHealingPad(float hardness, float resistance) {
      super(Material.ROCK, SoundType.STONE, hardness, resistance, "pickaxe", 1);
      this.fullBlock = false;
      this.setLightOpacity(255);
      this.setLightLevel(0.25F);
   }

   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      if (entityIn instanceof EntityLivingBase && entityIn.ticksExisted % 25 == 0) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 1));
      }

      super.onEntityWalk(worldIn, pos, entityIn);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB_BOTTOM_HALF;
   }

   public boolean isTopSolid(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
      if (ForgeModContainer.disableStairSlabCulling) {
         return super.doesSideBlockRendering(state, world, pos, face);
      } else {
         return face == EnumFacing.DOWN;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(blockState, blockAccess, pos, side) ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }
}
