package rustic.common.blocks.crops;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ItemStakeCropSeed;
import rustic.core.Rustic;

public class BlockCropStake extends BlockBase {
   public static final Material CROP_STAKE = new Material(MapColor.WOOD) {
      public boolean isSolid() {
         return false;
      }
   };
   protected static final AxisAlignedBB STAKE_SELECTION_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
   protected static final AxisAlignedBB STAKE_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D);

   public BlockCropStake() {
      super(CROP_STAKE, "crop_stake");
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      this.setCreativeTab(Rustic.farmingTab);
      this.setSoundType(SoundType.WOOD);
      Blocks.FIRE.setFireInfo(this, 5, 20);
   }

   public boolean isToolEffective(String type, IBlockState state) {
      return type.equals("axe");
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack stack = player.getHeldItem(hand);
      if (!stack.isEmpty() && stack.getItem() instanceof ItemStakeCropSeed) {
         if (world.getBlockState(pos.down()).getBlock().canSustainPlant(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP, ((ItemStakeCropSeed)stack.getItem()).getCrop())) {
            world.setBlockState(pos, ((ItemStakeCropSeed)stack.getItem()).getCropState(), 3);
            if (!player.capabilities.isCreativeMode) {
               player.getHeldItem(hand).shrink(1);
            }

            return true;
         }
      } else if (!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(ModBlocks.ROPE)) {
         world.setBlockState(pos, ModBlocks.STAKE_TIED.getDefaultState(), 2);
         world.scheduleUpdate(pos, ModBlocks.STAKE_TIED, 1);
         if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
         }

         return true;
      }

      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return STAKE_SELECTION_AABB;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return STAKE_AABB;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side != EnumFacing.UP && side != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }
}
