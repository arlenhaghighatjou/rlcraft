package vazkii.quark.automation.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.automation.tile.TileRainDetector;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockRainDetector extends BlockModContainer implements IQuarkBlock {
   protected static final AxisAlignedBB RAIN_DETECTOR_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);
   private static final PropertyBool POWER = PropertyBool.create("power");
   private static final PropertyBool INVERTED = PropertyBool.create("inverted");

   public BlockRainDetector() {
      super("rain_detector", Material.ROCK, new String[0]);
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setHardness(1.5F);
      this.setSoundType(SoundType.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, Boolean.valueOf(false)).withProperty(INVERTED, Boolean.valueOf(false)));
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return RAIN_DETECTOR_AABB;
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((Boolean)blockState.getValue(POWER)).booleanValue() ? 15 : 0;
   }

   public void updatePower(World worldIn, BlockPos pos) {
      if (worldIn.provider.hasSkyLight()) {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         boolean raining = worldIn.isRaining();
         worldIn.getCelestialAngleRadians(1.0F);
         if (((Boolean)iblockstate.getValue(INVERTED)).booleanValue()) {
            raining = !raining;
         }

         if (((Boolean)iblockstate.getValue(POWER)).booleanValue() != raining) {
            worldIn.setBlockState(pos, iblockstate.withProperty(POWER, Boolean.valueOf(raining)), 3);
         }
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (playerIn.isAllowEdit()) {
         if (worldIn.isRemote) {
            return true;
         } else {
            worldIn.setBlockState(pos, state.withProperty(INVERTED, Boolean.valueOf(!((Boolean)state.getValue(INVERTED)).booleanValue())), 4);
            this.updatePower(worldIn, pos);
            return true;
         }
      } else {
         return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
      return new TileRainDetector();
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{POWER, INVERTED});
   }

   public int getMetaFromState(IBlockState state) {
      return (((Boolean)state.getValue(POWER)).booleanValue() ? 1 : 0) | (((Boolean)state.getValue(INVERTED)).booleanValue() ? 2 : 0);
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, Boolean.valueOf((meta & 1) != 0)).withProperty(INVERTED, Boolean.valueOf((meta & 2) != 0));
   }
}
