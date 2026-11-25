package svenhjol.charm.world.block;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import svenhjol.charm.world.feature.EndPortalRunes;
import svenhjol.charm.world.tile.TileRunePortalFrame;
import svenhjol.meson.MesonBlock;
import svenhjol.meson.MesonBlockTE;
import svenhjol.meson.iface.IMesonBlock;
import vazkii.quark.api.IRotationLockHandler;

@Interface(
   iface = "vazkii.quark.api.IRotationLockHandler",
   modid = "quark"
)
public class BlockRunePortalFrame extends MesonBlockTE<TileRunePortalFrame> implements IMesonBlock, IRotationLockHandler {
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyEnum<MesonBlock.ColorVariant> VARIANT = PropertyEnum.create("variant", MesonBlock.ColorVariant.class);
   protected static final AxisAlignedBB AABB_BLOCK = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
   protected static final AxisAlignedBB AABB_RUNE = new AxisAlignedBB(0.3125D, 0.8125D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
   private static BlockPattern portalShape;

   public BlockRunePortalFrame() {
      super(Material.ROCK, "rune_portal_frame");
      this.setHardness(-1.0F);
      this.setBlockUnbreakable();
      this.setResistance(6000000.0F);
      this.setLightLevel(0.125F);
      this.setSoundType(SoundType.GLASS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VARIANT, MesonBlock.ColorVariant.WHITE));
   }

   public String getModId() {
      return "charm";
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BLOCK);
      addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_RUNE);
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.AIR;
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      EndPortalRunes.deactivate(worldIn, pos);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileRunePortalFrame portalFrame = (TileRunePortalFrame)this.getTileEntity(worldIn, pos);
      if (this.validTileEntity(portalFrame)) {
         EnumFacing facing = placer.getHorizontalFacing().getOpposite();
         portalFrame.setFacing(facing);
         IBlockState changed = state.withProperty(FACING, facing);
         worldIn.setBlockState(pos, changed, 2);
      }

      EndPortalRunes.activate(worldIn, pos);
   }

   public IBlockState setRotation(World world, BlockPos pos, IBlockState state, EnumFacing facing, boolean hasHalf, boolean topHalf) {
      TileRunePortalFrame portalFrame = (TileRunePortalFrame)this.getTileEntity(world, pos);
      if (this.validTileEntity(portalFrame)) {
         portalFrame.setFacing(facing);
      }

      state = state.withProperty(FACING, facing);
      world.setBlockState(pos, state, 2);
      return state;
   }

   public String[] getVariants() {
      List<String> variants = new ArrayList();

      for(MesonBlock.ColorVariant variant : MesonBlock.ColorVariant.values()) {
         variants.add(variant.toString().toLowerCase());
      }

      return (String[])variants.toArray(new String[0]);
   }

   public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
      return false;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(VARIANT, MesonBlock.ColorVariant.byMetadata(meta));
   }

   public static BlockPattern getOrCreatePortalShape() {
      if (portalShape == null) {
         portalShape = FactoryBlockPattern.start().aisle(new String[]{"?vvv?", ">???<", ">???<", ">???<", "?^^^?"}).where('?', BlockWorldState.hasState(BlockStateMatcher.ANY)).where('^', BlockWorldState.hasState(BlockStateMatcher.forBlock(EndPortalRunes.frame).where(FACING, (enumFacing) -> {
            return Objects.equals(enumFacing, EnumFacing.SOUTH);
         }))).where('>', BlockWorldState.hasState(BlockStateMatcher.forBlock(EndPortalRunes.frame).where(FACING, (enumFacing) -> {
            return Objects.equals(enumFacing, EnumFacing.WEST);
         }))).where('v', BlockWorldState.hasState(BlockStateMatcher.forBlock(EndPortalRunes.frame).where(FACING, (enumFacing) -> {
            return Objects.equals(enumFacing, EnumFacing.NORTH);
         }))).where('<', BlockWorldState.hasState(BlockStateMatcher.forBlock(EndPortalRunes.frame).where(FACING, (enumFacing) -> {
            return Objects.equals(enumFacing, EnumFacing.EAST);
         }))).build();
      }

      return portalShape;
   }

   public int getMetaFromState(IBlockState state) {
      return ((MesonBlock.ColorVariant)state.getValue(VARIANT)).ordinal();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, MesonBlock.ColorVariant.byMetadata(meta));
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      TileRunePortalFrame portal = (TileRunePortalFrame)worldIn.getTileEntity(pos);
      if (portal != null && this.validTileEntity(portal)) {
         EnumFacing facing = portal.getFacing();
         state = state.withProperty(FACING, facing);
      }

      return state;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, VARIANT});
   }

   public TileRunePortalFrame createTileEntity(World world, IBlockState state) {
      return new TileRunePortalFrame();
   }

   public Class<TileRunePortalFrame> getTileEntityClass() {
      return TileRunePortalFrame.class;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
   }
}
