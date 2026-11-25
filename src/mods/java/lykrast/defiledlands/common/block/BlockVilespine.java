package lykrast.defiledlands.common.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.init.ModBlocks;
import lykrast.defiledlands.common.util.PlantUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVilespine extends BlockGeneric implements IPlantable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   public static final PropertyBool TOPMOST = PropertyBool.create("topmost");
   protected static final AxisAlignedBB REED_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

   public BlockVilespine() {
      super(Material.PLANTS, SoundType.PLANT, 0.1F, 0.5F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(TOPMOST, Boolean.valueOf(false)));
      this.setTickRandomly(true);
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (PlantUtils.vulnerableToVilespine(entityIn)) {
         entityIn.attackEntityFrom(DamageSource.CACTUS, 3.0F);
      }

   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.up()).getBlock();
      return state.withProperty(TOPMOST, Boolean.valueOf(block.isAir(worldIn.getBlockState(pos.up()), worldIn, pos.up())));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return REED_AABB;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if ((worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.vilespine || this.checkForDrop(worldIn, pos, state)) && worldIn.isAirBlock(pos.up())) {
         int i;
         for(i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
            ;
         }

         if (i < 7) {
            int j = ((Integer)state.getValue(AGE)).intValue();
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
               if (j == 15) {
                  worldIn.setBlockState(pos.up(), this.getDefaultState());
                  worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 4);
               } else {
                  worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
               }

               ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
         }
      }

   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos.down());
      Block block = state.getBlock();
      if (block == this) {
         return true;
      } else {
         return block == ModBlocks.dirtDefiled || block == ModBlocks.grassDefiled || block == ModBlocks.sandDefiled;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkForDrop(worldIn, pos, state);
   }

   protected final boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (this.canBlockStay(worldIn, pos)) {
         return true;
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return false;
      }
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return this.canPlaceBlockAt(worldIn, pos);
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

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue();
   }

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return EnumPlantType.Desert;
   }

   public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
      return this.getDefaultState();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE, TOPMOST});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
