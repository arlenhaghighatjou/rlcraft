package svenhjol.charm.crafting.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.crafting.feature.Lantern;
import svenhjol.meson.MesonBlock;

public class BlockLantern extends MesonBlock {
   public static final PropertyBool HANGING = PropertyBool.create("hanging");

   public BlockLantern(String variant) {
      super(Material.IRON, variant + "_lantern");
      this.setHardness(Lantern.hardness);
      this.setResistance(Lantern.resistance);
      this.setLightLevel(Lantern.lightLevel);
      this.setSoundType(SoundType.METAL);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(HANGING, Boolean.valueOf(false)));
   }

   @Nonnull
   public Block setSoundType(@Nonnull SoundType sound) {
      return super.setSoundType(sound);
   }

   public String getModId() {
      return "charm";
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullBlock(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      float hpad = 0.3125F;
      float vpad = 0.4375F;
      float voff = 0.06F;
      AxisAlignedBB aabb;
      if (((Boolean)state.getValue(HANGING)).booleanValue()) {
         aabb = new AxisAlignedBB((double)hpad, (double)voff, (double)hpad, (double)(1.0F - hpad), (double)(1.0F - vpad + voff), (double)(1.0F - hpad));
      } else {
         aabb = new AxisAlignedBB((double)hpad, 0.0D, (double)hpad, (double)(1.0F - hpad), (double)(1.0F - vpad), (double)(1.0F - hpad));
      }

      return aabb;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return this.getBoundingBox(blockState, worldIn, pos);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (Lantern.playSound && rand.nextFloat() <= 0.04F) {
         worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, CharmSounds.LITTLE_FIRE, SoundCategory.BLOCKS, 0.24F, 1.0F, false);
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (Lantern.falling) {
         this.checkFallable(worldIn, pos);
      }

   }

   public int tickRate(World worldIn) {
      return 2;
   }

   private void checkFallable(World worldIn, BlockPos pos) {
      boolean hanging = ((Boolean)worldIn.getBlockState(pos).getValue(HANGING)).booleanValue();
      if (!hanging || !this.canPlaceBelow(worldIn, pos)) {
         if (hanging || !this.canPlaceAbove(worldIn, pos)) {
            if ((worldIn.isAirBlock(pos.down()) || BlockFalling.canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0) {
               int i = 32;
               if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                  if (!worldIn.isRemote) {
                     EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos).withProperty(HANGING, Boolean.valueOf(false)));
                     worldIn.spawnEntity(entityfallingblock);
                  }
               } else {
                  IBlockState state = worldIn.getBlockState(pos);
                  worldIn.setBlockToAir(pos);

                  BlockPos blockpos;
                  for(blockpos = pos.down(); (worldIn.isAirBlock(blockpos) || BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {
                     ;
                  }

                  if (blockpos.getY() > 0) {
                     worldIn.setBlockState(blockpos.up(), state);
                  }
               }
            }

         }
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(HANGING)).booleanValue() ? 1 : 0;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(HANGING, Boolean.valueOf((meta & 1) > 0));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{HANGING});
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.canPlaceBelow(world, pos) ? this.getDefaultState().withProperty(HANGING, Boolean.valueOf(true)) : this.getDefaultState().withProperty(HANGING, Boolean.valueOf(false));
   }

   public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos) {
      return super.canPlaceBlockAt(world, pos) && (this.canPlaceAbove(world, pos) || this.canPlaceBelow(world, pos));
   }

   private boolean canPlaceAbove(World world, BlockPos pos) {
      IBlockState stateBelow = world.getBlockState(pos.down());
      Block blockBelow = stateBelow.getBlock();
      return blockBelow == Blocks.LIT_PUMPKIN || blockBelow != Blocks.AIR && blockBelow.canPlaceTorchOnTop(stateBelow, world, pos.down());
   }

   private boolean canPlaceBelow(World world, BlockPos pos) {
      IBlockState stateAbove = world.getBlockState(pos.up());
      Block blockAbove = stateAbove.getBlock();
      if (stateAbove == Blocks.AIR) {
         return false;
      } else if (!stateAbove.isSideSolid(world, pos.up(), EnumFacing.DOWN) && !(blockAbove instanceof BlockFence) && blockAbove != Blocks.GLASS && blockAbove != Blocks.COBBLESTONE_WALL && blockAbove != Blocks.STAINED_GLASS) {
         BlockFaceShape shape = stateAbove.getBlockFaceShape(world, pos.up(), EnumFacing.DOWN);
         return (shape == BlockFaceShape.SOLID || shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG) && !isExceptionBlockForAttaching(blockAbove);
      } else {
         return true;
      }
   }

   @ParametersAreNonnullByDefault
   public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.CENTER;
   }
}
