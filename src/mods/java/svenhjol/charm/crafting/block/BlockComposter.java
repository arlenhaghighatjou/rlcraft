package svenhjol.charm.crafting.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.crafting.tile.TileComposter;
import svenhjol.meson.MesonBlockTE;
import svenhjol.meson.helper.SoundHelper;
import svenhjol.meson.iface.IMesonBlock;

public class BlockComposter extends MesonBlockTE<TileComposter> implements IMesonBlock {
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 8);
   protected static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
   protected static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
   protected static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
   protected static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

   public BlockComposter() {
      super(Material.WOOD, "composter");
      this.setHardness(2.0F);
      this.setSoundType(SoundType.WOOD);
      this.setHarvestLevel("axe", 1);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
      this.useNeighborBrightness = true;
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
      addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
      addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
      addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
   }

   public String getModId() {
      return "charm";
   }

   public Class<TileComposter> getTileEntityClass() {
      return TileComposter.class;
   }

   public TileComposter createTileEntity(World world, IBlockState state) {
      return new TileComposter();
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      boolean added = false;
      TileComposter composter = (TileComposter)this.getTileEntity(world, pos);
      if (this.validTileEntity(composter)) {
         added = composter.addItem(player.getHeldItem(hand), player.isCreative());
         if (added && world.isRemote) {
            this.playAddItemSound(world, pos);
            this.spawnParticles(world, pos);
         }
      }

      return added;
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
      return ((Integer)blockState.getValue(LEVEL)).intValue();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LEVEL});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(LEVEL)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(World world, BlockPos pos) {
      for(int i = 0; i < 8; ++i) {
         double d0 = world.rand.nextGaussian() * 0.02D;
         double d1 = world.rand.nextGaussian() * 0.02D;
         double d2 = world.rand.nextGaussian() * 0.02D;
         double dx = (double)((float)pos.getX() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
         double dy = (double)((float)pos.getY() + 1.05F);
         double dz = (double)((float)pos.getZ() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
         world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, dx, dy, dz, d0, d1, d2, new int[0]);
      }

   }

   @SideOnly(Side.CLIENT)
   public void playAddItemSound(World world, BlockPos pos) {
      SoundHelper.playSoundAtPos(world, pos, SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.0F);
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.SOLID;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }
}
