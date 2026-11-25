package vazkii.quark.decoration.block;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockGrate extends BlockMod implements IQuarkBlock {
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
   private static final AxisAlignedBB AABB_SPAWN_BLOCK = new AxisAlignedBB(0.0D, 1.0D, 0.0D, 1.0D, 1.0625D, 1.0D);

   public BlockGrate() {
      super("grate", Material.IRON, new String[0]);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setLightOpacity(0);
      this.setSoundType(SoundType.METAL);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
      if (entityIn != null && !(entityIn instanceof EntityItem)) {
         super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
         if (!(entityIn instanceof EntityPlayer)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_SPAWN_BLOCK);
         }

         if (entityIn instanceof EntityAnimal) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, this.getBoundingBox(state, worldIn, pos).expand(0.0D, (double)entityIn.stepHeight + 0.125D, 0.0D));
         }
      }

   }

   @Nullable
   public PathNodeType getAiPathNodeType(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EntityLiving entity) {
      return entity instanceof EntityAnimal ? PathNodeType.DAMAGE_OTHER : null;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isFullBlock(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Nonnull
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      if (side.getAxis() == Axis.Y) {
         return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
      } else {
         IBlockState state = blockAccess.getBlockState(pos.offset(side));
         Block block = state.getBlock();
         return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
      }
   }
}
