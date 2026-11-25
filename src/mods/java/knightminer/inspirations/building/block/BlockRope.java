package knightminer.inspirations.building.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlock.IEnumMeta;
import slimeknights.mantle.client.CreativeTab;

public class BlockRope extends EnumBlock<BlockRope.RopeType> {
   public static final PropertyEnum<BlockRope.Rungs> RUNGS = PropertyEnum.create("rungs", BlockRope.Rungs.class);
   public static final PropertyEnum<BlockRope.RopeType> TYPE = PropertyEnum.create("type", BlockRope.RopeType.class);
   public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
   protected static final AxisAlignedBB[] BOUNDS = new AxisAlignedBB[]{new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0625D, 0.0D, 0.375D, 0.9375D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0625D, 0.625D, 1.0D, 0.9375D)};
   protected static final AxisAlignedBB[] BOUNDS_BOTTOM = new AxisAlignedBB[]{new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0625D, 0.25D, 0.375D, 0.9375D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.25D, 0.0625D, 0.625D, 1.0D, 0.9375D)};

   public BlockRope() {
      super(Material.CARPET, TYPE, BlockRope.RopeType.class);
      this.setCreativeTab(CreativeTab.DECORATIONS);
      this.setHardness(0.5F);
      this.setDefaultState(this.getBlockState().getBaseState().withProperty(TYPE, BlockRope.RopeType.ROPE).withProperty(RUNGS, BlockRope.Rungs.NONE));
      IBlockState chain = this.getDefaultState().withProperty(TYPE, BlockRope.RopeType.CHAIN);

      for(BlockRope.Rungs rungs : BlockRope.Rungs.values()) {
         this.setHarvestLevel("pickaxe", 0, chain.withProperty(RUNGS, rungs));
      }

   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE, RUNGS, BOTTOM});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      BlockPos down = pos.down();
      return state.withProperty(BOTTOM, Boolean.valueOf(!this.canConnectTo(world.getBlockState(down), world, down, EnumFacing.UP)));
   }

   private boolean canConnectTo(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      if (state.getBlock() == this) {
         return true;
      } else {
         BlockFaceShape shape = state.getBlockFaceShape(world, pos, side);
         return shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG || shape == BlockFaceShape.SOLID;
      }
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(this.prop, this.fromMeta(meta & 3)).withProperty(RUNGS, BlockRope.Rungs.fromMeta(meta >> 2));
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockRope.RopeType)state.getValue(this.prop)).getMeta() | ((BlockRope.Rungs)state.getValue(RUNGS)).getMeta() << 2;
   }

   public int damageDropped(IBlockState state) {
      return ((BlockRope.RopeType)state.getValue(this.prop)).getMeta();
   }

   /** @deprecated */
   @Deprecated
   public Material getMaterial(IBlockState state) {
      return state.getValue(TYPE) == BlockRope.RopeType.CHAIN ? Material.IRON : super.getMaterial(state);
   }

   public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
      switch((BlockRope.RopeType)state.getValue(TYPE)) {
      case ROPE:
         return SoundType.CLOTH;
      case CHAIN:
         return SoundType.METAL;
      case VINE:
         return SoundType.PLANT;
      default:
         return super.getSoundType(state, world, pos, entity);
      }
   }

   /** @deprecated */
   @Deprecated
   public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
      return state.getValue(TYPE) == BlockRope.RopeType.CHAIN ? 5.0F : super.getBlockHardness(state, worldIn, pos);
   }

   public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
      return world.getBlockState(pos).getValue(TYPE) == BlockRope.RopeType.CHAIN ? 30.0F : super.getExplosionResistance(world, pos, exploder, explosion);
   }

   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      return super.canPlaceBlockAt(world, pos) && this.isValidRope(world, pos);
   }

   private boolean isValidRope(World world, BlockPos pos) {
      BlockPos up = pos.up();
      IBlockState state = world.getBlockState(up);
      return this.canConnectTo(state, world, pos, EnumFacing.DOWN);
   }

   /** @deprecated */
   @Deprecated
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.isValidRope(world, pos)) {
         this.dropBlockAsItem(world, pos, state, 0);
         world.setBlockToAir(pos);
      }

      super.neighborChanged(state, world, pos, blockIn, fromPos);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
      ItemStack stack = player.getHeldItem(hand);
      return Block.getBlockFromItem(stack.getItem()) == this ? this.extendRope(world, pos, state, side, player, stack, clickX, clickY, clickZ) : false;
   }

   private boolean extendRope(World world, BlockPos pos, IBlockState state, EnumFacing side, EntityPlayer player, ItemStack stack, float clickX, float clickY, float clickZ) {
      BlockRope.RopeType type = (BlockRope.RopeType)state.getValue(TYPE);
      if (stack.getMetadata() != type.getMeta()) {
         return false;
      } else {
         BlockPos next = pos.down();

         for(IBlockState below = world.getBlockState(next); below.getBlock() == this && below.getValue(TYPE) == type; below = world.getBlockState(next)) {
            next = next.down();
         }

         if (this.canPlaceBlockAt(world, next)) {
            ItemBlock itemBlock = (ItemBlock)stack.getItem();
            IBlockState newState = this.getDefaultState().withProperty(TYPE, type);
            if (itemBlock.placeBlockAt(stack, player, world, next, side, clickX, clickY, clickZ, newState)) {
               SoundType soundtype = newState.getBlock().getSoundType(newState, world, pos, player);
               world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }
            }
         }

         return true;
      }
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      super.getDrops(drops, world, pos, state, fortune);
      if (state.getValue(RUNGS) != BlockRope.Rungs.NONE) {
         drops.add(new ItemStack(((BlockRope.RopeType)state.getValue(TYPE)).getItem(), 4));
      }

   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockRope.RopeType type = (BlockRope.RopeType)state.getValue(TYPE);
      BlockPos next = pos.down();
      IBlockState below = world.getBlockState(next);
      int count = 0;

      int extra;
      for(extra = 0; below.getBlock() == this && below.getValue(TYPE) == type; below = world.getBlockState(next)) {
         ++count;
         if (below.getValue(RUNGS) != BlockRope.Rungs.NONE) {
            ++extra;
         }

         next = next.down();
      }

      for(int i = 0; i < count; ++i) {
         next = next.up();
         world.destroyBlock(next, false);
      }

      ItemStack drops = new ItemStack(this, count, type.getMeta());
      spawnAsEntity(world, pos, drops);
      if (extra > 0) {
         ItemStack extraDrop = new ItemStack(type.getItem(), extra * 4);
         spawnAsEntity(world, pos, extraDrop);
      }

   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   /** @deprecated */
   @Deprecated
   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      int rungs = ((BlockRope.Rungs)state.getValue(RUNGS)).getMeta();
      return ((Boolean)state.getActualState(source, pos).getValue(BOTTOM)).booleanValue() ? BOUNDS_BOTTOM[rungs] : BOUNDS[rungs];
   }

   public static enum RopeType implements IStringSerializable, IEnumMeta {
      ROPE,
      CHAIN,
      VINE;

      private int meta = this.ordinal();

      private RopeType() {
      }

      public int getMeta() {
         return this.meta;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public Item getItem() {
         return this == CHAIN ? Items.IRON_NUGGET : Items.STICK;
      }
   }

   public static enum Rungs implements IStringSerializable, IEnumMeta {
      NONE,
      X,
      Z;

      private int meta = this.ordinal();

      private Rungs() {
      }

      public int getMeta() {
         return this.meta;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public static BlockRope.Rungs fromMeta(int meta) {
         return meta >= 0 && meta <= values().length ? values()[meta] : NONE;
      }

      public static BlockRope.Rungs fromAxis(Axis axis) {
         switch(axis) {
         case X:
            return X;
         case Z:
            return Z;
         default:
            return NONE;
         }
      }
   }
}
