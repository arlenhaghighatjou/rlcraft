package primetoxinz.coralreef;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCoral extends Block implements IPlantable {
   public static final int NTYPES = 6;
   public static final EnumPlantType CORAL = EnumPlantType.getPlantType("Coral");
   public static final PropertyInteger TYPES = PropertyInteger.create("types", 0, 5);
   protected static final AxisAlignedBB CORAL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

   public BlockCoral() {
      super(Material.WATER);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.MISC);
      this.setHardness(0.0F);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.getDefaultState().withProperty(TYPES, Integer.valueOf(0)).withProperty(BlockLiquid.LEVEL, Integer.valueOf(15)));
   }

   public boolean highVariant(int variant) {
      return variant > 3;
   }

   public boolean placeAt(World world, BlockPos bottom) {
      boolean placed = false;
      if (this.canPlaceBlockAt(world, bottom)) {
         int variant = world.rand.nextInt(6);
         if (this.highVariant(variant)) {
            int height = world.rand.nextInt(4);

            for(int i = 0; i < height; ++i) {
               BlockPos bp = bottom.up(i);
               if (world.getBlockState(bp.up()).getMaterial() != Material.WATER) {
                  break;
               }

               placed = world.setBlockState(bp, this.getDefaultState().withProperty(TYPES, Integer.valueOf(variant)));
            }
         } else {
            placed = world.setBlockState(bottom, this.getDefaultState().withProperty(TYPES, Integer.valueOf(variant)));
         }
      }

      return placed;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos.down());
      Block block = state.getBlock();
      if (worldIn.getBlockState(pos.up()).getMaterial() != Material.WATER) {
         return false;
      } else if (block.canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this)) {
         return true;
      } else if (block == this) {
         int variant = ((Integer)state.getValue(TYPES)).intValue();
         return this.highVariant(variant);
      } else {
         return false;
      }
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(int i = 0; i < 6; ++i) {
         list.add(new ItemStack(this, 1, i));
      }

   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this, 1, ((Integer)state.getValue(TYPES)).intValue());
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockLiquid.LEVEL, TYPES});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPES, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(TYPES)).intValue();
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return Block.NULL_AABB;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return CORAL_AABB;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkAndDropBlock(worldIn, pos, state);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      this.checkAndDropBlock(worldIn, pos, state);
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(TYPES)).intValue();
   }

   protected boolean checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.canBlockStay(worldIn, pos, state)) {
         return true;
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
         return false;
      }
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, EaglercraftRandom rand) {
      if (CoralReef.ConfigHandler.bubbles && world.getBlockState(pos.up()).getMaterial() == Material.WATER) {
         double offset = 0.0625D;

         for(int i = 0; i < 6; ++i) {
            double x1 = (double)pos.getX() + rand.nextDouble();
            double y1 = (double)pos.getY() + rand.nextDouble();
            double z1 = (double)pos.getZ() + rand.nextDouble();
            if (i == 0 && !world.getBlockState(pos.up()).isBlockNormalCube()) {
               y1 = (double)(pos.getY() + 1) + offset;
            }

            if (i == 1 && !world.getBlockState(pos.down()).isBlockNormalCube()) {
               y1 = (double)pos.getY() - offset;
            }

            if (i == 2 && !world.getBlockState(pos.offset(EnumFacing.SOUTH)).isBlockNormalCube()) {
               z1 = (double)(pos.getZ() + 1) + offset;
            }

            if (i == 3 && !world.getBlockState(pos.offset(EnumFacing.NORTH)).isBlockNormalCube()) {
               z1 = (double)pos.getZ() - offset;
            }

            if (i == 4 && !world.getBlockState(pos.offset(EnumFacing.EAST)).isBlockNormalCube()) {
               x1 = (double)(pos.getX() + 1) + offset;
            }

            if (i == 5 && !world.getBlockState(pos.offset(EnumFacing.WEST)).isBlockNormalCube()) {
               x1 = (double)pos.getX() - offset;
            }

            if (x1 < (double)pos.getX() || x1 > (double)(pos.getY() + 1) || y1 < 0.0D || y1 > (double)(pos.getY() + 1) || z1 < (double)pos.getZ() || z1 > (double)(pos.getZ() + 1)) {
               world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x1, y1, z1, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      return this.canPlaceBlockAt(world, pos);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return CoralReef.ConfigHandler.coralLightLevel;
   }

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return CORAL;
   }

   public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
      return world.getBlockState(pos);
   }
}
