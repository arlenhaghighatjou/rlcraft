package vazkii.quark.automation.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.automation.tile.TileInductor;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockRedstoneInductor extends BlockMod implements IQuarkBlock, IBlockColorProvider {
   protected static final AxisAlignedBB REDSTONE_DIODE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool LOCKED = PropertyBool.create("locked");
   public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

   public BlockRedstoneInductor() {
      super("redstone_inductor", Material.CIRCUITS, new String[0]);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(LOCKED, Boolean.valueOf(false)));
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setSoundType(SoundType.WOOD);
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return (state, worldIn, pos, tintIndex) -> {
         return tintIndex == 1 ? BlockRedstoneWire.colorMultiplier(((Integer)state.getValue(POWER)).intValue()) : -1;
      };
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{POWER};
   }

   public IItemColor getItemColor() {
      return null;
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
      return new TileInductor();
   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(POWER, Integer.valueOf(this.getActiveSignal(worldIn, pos)));
   }

   private boolean shouldBeLocked(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing direction = (EnumFacing)state.getValue(FACING);
      EnumFacing side1 = direction.rotateY();
      EnumFacing side2 = direction.rotateYCCW();
      return this.getLockingPowerOnSide(worldIn, pos.offset(direction), direction) > 0 || this.getLockingPowerOnSide(worldIn, pos.offset(side1), side1) > 0 || this.getLockingPowerOnSide(worldIn, pos.offset(side2), side2) > 0;
   }

   protected int getLockingPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      return BlockRedstoneRepeater.isDiode(iblockstate) ? worldIn.getStrongPower(pos, side) : 0;
   }

   public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EaglercraftRandom random) {
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      boolean isLocked = this.isLocked(state);
      boolean willBeLocked = this.shouldBeLocked(worldIn, pos, state);
      EnumFacing side = (EnumFacing)state.getValue(FACING);
      IBlockState finalState = state.withProperty(LOCKED, Boolean.valueOf(willBeLocked));
      if (!isLocked || !willBeLocked) {
         int power = this.calculateInputStrength(worldIn, pos, state);
         TileEntity tile = worldIn.getTileEntity(pos);
         int prev = 0;
         if (tile instanceof TileInductor) {
            TileInductor inductor = (TileInductor)tile;
            prev = inductor.getOutputSignal();
            inductor.setOutputSignal(power);
         }

         state = state.withProperty(POWER, Integer.valueOf(power));
         finalState = finalState.withProperty(POWER, Integer.valueOf(power));
         if (prev != power) {
            boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
            boolean isPowered = this.isPowered(state);
            if (isPowered && !shouldBePowered) {
               finalState = finalState.withProperty(POWERED, Boolean.valueOf(false));
            } else if (!isPowered && shouldBePowered) {
               finalState = finalState.withProperty(POWERED, Boolean.valueOf(true));
            }
         }
      }

      worldIn.setBlockState(pos, finalState, 3);
      worldIn.notifyNeighborsOfStateExcept(pos.offset(side, -1), this, side);
   }

   protected int getActiveSignal(IBlockAccess world, BlockPos pos) {
      TileEntity tile = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
      return tile instanceof TileInductor ? ((TileInductor)tile).getOutputSignal() : 0;
   }

   protected void updateState(World world, BlockPos pos) {
      world.scheduleBlockUpdate(pos, this, 1, -1);
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, POWERED, LOCKED, POWER});
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex() + (((Boolean)state.getValue(POWERED)).booleanValue() ? 4 : 0) + (((Boolean)state.getValue(LOCKED)).booleanValue() ? 8 : 0);
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing face = EnumFacing.getHorizontal(meta & 3);
      boolean powered = (meta & 4) != 0;
      boolean locked = (meta & 8) != 0;
      return this.getDefaultState().withProperty(FACING, face).withProperty(POWERED, Boolean.valueOf(powered)).withProperty(LOCKED, Boolean.valueOf(locked));
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return REDSTONE_DIODE_AABB;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && super.canPlaceBlockAt(worldIn, pos);
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
   }

   protected boolean isLocked(IBlockState state) {
      return ((Boolean)state.getValue(LOCKED)).booleanValue();
   }

   protected boolean isPowered(IBlockState state) {
      return ((Boolean)state.getValue(POWERED)).booleanValue();
   }

   public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.getWeakPower(blockAccess, pos, side);
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      if (!this.isPowered(blockState)) {
         return 0;
      } else {
         return blockState.getValue(FACING) == side ? this.getActiveSignal(blockAccess, pos) : 0;
      }
   }

   public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
      return true;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (this.canBlockStay(worldIn, pos)) {
         this.updateState(worldIn, pos);
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);

         for(EnumFacing enumfacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
         }
      }

   }

   protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing direction = (EnumFacing)state.getValue(FACING);
      EnumFacing side1 = direction.rotateY();
      EnumFacing side2 = direction.rotateYCCW();
      return Math.min(15, this.calculateInputStrength(worldIn, pos, direction) + this.calculateInputStrength(worldIn, pos, side1) + this.calculateInputStrength(worldIn, pos, side2));
   }

   protected int calculateInputStrength(World worldIn, BlockPos pos, EnumFacing side) {
      BlockPos blockpos = pos.offset(side);
      IBlockState state = worldIn.getBlockState(blockpos);
      if (BlockRedstoneRepeater.isDiode(state)) {
         return 0;
      } else {
         int i = worldIn.getRedstonePower(blockpos, side);
         return i >= 15 ? i : Math.max(i, state.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer)state.getValue(BlockRedstoneWire.POWER)).intValue() : 0);
      }
   }

   protected boolean shouldBePowered(World world, BlockPos pos, IBlockState currState) {
      return this.calculateInputStrength(world, pos, currState) > 0;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.shouldBePowered(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, 1);
      }

   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      BlockRedstoneRandomizer.notify(this, worldIn, pos, state);
   }

   public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      BlockRedstoneRandomizer.notify(this, worldIn, pos, state);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
      if (super.rotateBlock(world, pos, axis)) {
         IBlockState state = world.getBlockState(pos);
         state = state.withProperty(POWERED, Boolean.valueOf(false));
         world.setBlockState(pos, state);
         world.scheduleUpdate(pos, this, 1);
         return true;
      } else {
         return false;
      }
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
