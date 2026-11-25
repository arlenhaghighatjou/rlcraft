package vazkii.quark.oddities.block;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.BlockQuarkWall;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.oddities.tile.TilePipe;

public class BlockPipe extends BlockModContainer implements IQuarkBlock {
   private static final AxisAlignedBB CENTER_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.3125D, 0.6875D, 0.6875D, 0.6875D);
   private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.6875D, 0.6875D);
   private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
   private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.0D, 0.6875D, 0.6875D, 0.6875D);
   private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.3125D, 0.6875D, 0.6875D, 1.0D);
   private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.3125D, 0.3125D, 0.6875D, 0.6875D, 0.6875D);
   private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.3125D, 0.3125D, 0.3125D, 1.0D, 0.6875D, 0.6875D);
   private static final AxisAlignedBB DOWN_FLARE_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.325D, 0.75D);
   private static final AxisAlignedBB UP_FLARE_AABB = new AxisAlignedBB(0.25D, 0.625D, 0.25D, 0.75D, 0.75D, 0.75D);
   private static final AxisAlignedBB NORTH_FLARE_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.325D);
   private static final AxisAlignedBB SOUTH_FLARE_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.625D, 0.75D, 0.75D, 0.75D);
   private static final AxisAlignedBB WEST_FLARE_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.325D, 0.75D, 0.75D);
   private static final AxisAlignedBB EAST_FLARE_AABB = new AxisAlignedBB(0.625D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
   private static final AxisAlignedBB DOWN_TERMINAL_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.125D, 0.75D);
   private static final AxisAlignedBB UP_TERMINAL_AABB = new AxisAlignedBB(0.25D, 0.875D, 0.25D, 0.75D, 1.0D, 0.75D);
   private static final AxisAlignedBB NORTH_TERMINAL_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.125D);
   private static final AxisAlignedBB SOUTH_TERMINAL_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.875D, 0.75D, 0.75D, 1.0D);
   private static final AxisAlignedBB WEST_TERMINAL_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.125D, 0.75D, 0.75D);
   private static final AxisAlignedBB EAST_TERMINAL_AABB = new AxisAlignedBB(0.875D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
   public static final PropertyEnum<BlockPipe.ConnectionType> DOWN = PropertyEnum.create("down", BlockPipe.ConnectionType.class);
   public static final PropertyEnum<BlockPipe.ConnectionType> UP = PropertyEnum.create("up", BlockPipe.ConnectionType.class);
   public static final PropertyEnum<BlockPipe.ConnectionType> NORTH = PropertyEnum.create("north", BlockPipe.ConnectionType.class);
   public static final PropertyEnum<BlockPipe.ConnectionType> SOUTH = PropertyEnum.create("south", BlockPipe.ConnectionType.class);
   public static final PropertyEnum<BlockPipe.ConnectionType> WEST = PropertyEnum.create("west", BlockPipe.ConnectionType.class);
   public static final PropertyEnum<BlockPipe.ConnectionType> EAST = PropertyEnum.create("east", BlockPipe.ConnectionType.class);
   public static final PropertyBool ENABLED = PropertyBool.create("enabled");
   private static final PropertyEnum<BlockPipe.ConnectionType>[] CONNECTIONS = new PropertyEnum[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   private static final AxisAlignedBB[] SIDE_BOXES = new AxisAlignedBB[]{DOWN_AABB, UP_AABB, NORTH_AABB, SOUTH_AABB, WEST_AABB, EAST_AABB};
   private static final AxisAlignedBB[] FLARE_BOXES = new AxisAlignedBB[]{DOWN_FLARE_AABB, UP_FLARE_AABB, NORTH_FLARE_AABB, SOUTH_FLARE_AABB, WEST_FLARE_AABB, EAST_FLARE_AABB};
   private static final AxisAlignedBB[] TERMINAL_BOXES = new AxisAlignedBB[]{DOWN_TERMINAL_AABB, UP_TERMINAL_AABB, NORTH_TERMINAL_AABB, SOUTH_TERMINAL_AABB, WEST_TERMINAL_AABB, EAST_TERMINAL_AABB};
   private static final Map<IBlockState, EnumFacing> FLARE_STATES = Maps.newHashMap();

   public BlockPipe() {
      super("pipe", Material.GLASS, new String[0]);
      this.setHardness(3.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.GLASS);
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setHarvestLevel("pickaxe", 1);
      this.setDefaultState(this.getDefaultState().withProperty(DOWN, BlockPipe.ConnectionType.NONE).withProperty(UP, BlockPipe.ConnectionType.NONE).withProperty(NORTH, BlockPipe.ConnectionType.NONE).withProperty(SOUTH, BlockPipe.ConnectionType.NONE).withProperty(WEST, BlockPipe.ConnectionType.NONE).withProperty(EAST, BlockPipe.ConnectionType.NONE).withProperty(ENABLED, Boolean.valueOf(true)));
      UnmodifiableIterator var1 = this.blockState.getValidStates().iterator();

      label30:
      while(var1.hasNext()) {
         IBlockState state = (IBlockState)var1.next();
         EnumFacing onlySide = null;

         for(EnumFacing facing : EnumFacing.VALUES) {
            if (state.getValue(CONNECTIONS[facing.getIndex()]) != BlockPipe.ConnectionType.NONE) {
               if (onlySide != null) {
                  continue label30;
               }

               onlySide = facing;
            }
         }

         if (onlySide != null) {
            FLARE_STATES.put(state, onlySide.getOpposite());
         }
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean flag = !worldIn.isBlockPowered(pos);
      if (flag != ((Boolean)state.getValue(ENABLED)).booleanValue()) {
         worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 6);
      }

   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = this.getActualState(state, source, pos);
      double minX = connectionHeight(state, EnumFacing.WEST);
      double minY = connectionHeight(state, EnumFacing.DOWN);
      double minZ = connectionHeight(state, EnumFacing.NORTH);
      double maxX = connectionHeight(state, EnumFacing.EAST);
      double maxY = connectionHeight(state, EnumFacing.UP);
      double maxZ = connectionHeight(state, EnumFacing.SOUTH);
      boolean downFlared = isFlared(state, EnumFacing.DOWN);
      boolean upFlared = isFlared(state, EnumFacing.UP);
      boolean northFlared = isFlared(state, EnumFacing.NORTH);
      boolean southFlared = isFlared(state, EnumFacing.SOUTH);
      boolean westFlared = isFlared(state, EnumFacing.WEST);
      boolean eastFlared = isFlared(state, EnumFacing.EAST);
      if (downFlared || upFlared || northFlared || southFlared) {
         maxX = Math.max(maxX, 0.75D);
         minX = Math.min(minX, 0.25D);
      }

      if (northFlared || southFlared || westFlared || eastFlared) {
         maxY = Math.max(maxY, 0.75D);
         minY = Math.min(minY, 0.25D);
      }

      if (downFlared || upFlared || westFlared || eastFlared) {
         maxZ = Math.max(maxZ, 0.75D);
         minZ = Math.min(minZ, 0.25D);
      }

      return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   @Nullable
   public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
      List<AxisAlignedBB> boxes = new ArrayList();
      this.addCollisionBoxToList(blockState, worldIn, pos, new AxisAlignedBB(pos), boxes, (Entity)null, false);

      for(AxisAlignedBB bb : boxes) {
         if (this.rayTrace(pos, start, end, bb.offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))) != null) {
            return super.collisionRayTrace(blockState, worldIn, pos, start, end);
         }
      }

      return null;
   }

   @Nonnull
   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
      return this.getBoundingBox(state, worldIn, pos).offset(pos);
   }

   public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
      if (!isActualState) {
         state = state.getActualState(worldIn, pos);
      }

      addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB);

      for(EnumFacing side : EnumFacing.VALUES) {
         BlockPipe.ConnectionType type = getType(state, side);
         if (type != null && type.isSolid) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_BOXES[side.ordinal()]);
         }

         if (type == null) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, FLARE_BOXES[side.ordinal()]);
         } else if (type == BlockPipe.ConnectionType.TERMINAL) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, TERMINAL_BOXES[side.ordinal()]);
         }
      }

   }

   public static double connectionHeight(IBlockState state, EnumFacing side) {
      BlockPipe.ConnectionType type = getType(state, side);
      int direction = side.getAxisDirection().getOffset();
      double base = 0.5D + 0.1875D * (double)direction;
      return base + (double)direction * (type != null && type.isSolid ? 0.3125D : (type != null && !type.isFlared ? 0.0D : 0.0625D));
   }

   public static boolean isFlared(IBlockState state, EnumFacing side) {
      BlockPipe.ConnectionType type = getType(state, side);
      return type == null || type.isFlared;
   }

   public static BlockPipe.ConnectionType getType(IBlockState state, EnumFacing side) {
      if (FLARE_STATES.containsKey(state) && FLARE_STATES.get(state) == side) {
         return null;
      } else {
         PropertyEnum<BlockPipe.ConnectionType> prop = CONNECTIONS[side.ordinal()];
         return (BlockPipe.ConnectionType)state.getValue(prop);
      }
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{ENABLED};
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{UP, DOWN, NORTH, SOUTH, WEST, EAST, ENABLED});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(ENABLED)).booleanValue() ? 0 : 1;
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(ENABLED, Boolean.valueOf((meta & 1) != 1));
   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      IBlockState actualState = state;

      for(EnumFacing facing : EnumFacing.VALUES) {
         PropertyEnum<BlockPipe.ConnectionType> prop = CONNECTIONS[facing.ordinal()];
         BlockPipe.ConnectionType type = this.getConnectionTo(worldIn, pos, facing);
         actualState = actualState.withProperty(prop, type);
      }

      return actualState;
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      TileEntity tile = worldIn.getTileEntity(pos);
      return tile instanceof TilePipe ? ((TilePipe)tile).getComparatorOutput() : 0;
   }

   public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TilePipe) {
         ((TilePipe)tileentity).dropAllItems();
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
      return new TilePipe();
   }

   private BlockPipe.ConnectionType getConnectionTo(IBlockAccess world, BlockPos pos, EnumFacing face) {
      pos = pos.offset(face);
      TileEntity tile = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
      if (tile != null) {
         if (tile instanceof TilePipe) {
            return BlockPipe.ConnectionType.PIPE;
         }

         if (tile instanceof IInventory || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) != null) {
            return BlockPipe.ConnectionType.TERMINAL;
         }
      }

      IBlockState stateAt = world.getBlockState(pos);
      Block blockAt = stateAt.getBlock();
      if ((face.getAxis() != Axis.Y || !(blockAt instanceof BlockWall) && !(blockAt instanceof BlockQuarkWall)) && (!(blockAt instanceof BlockPistonBase) && !(blockAt instanceof BlockPistonExtension) || stateAt.getValue(BlockDirectional.FACING) != face.getOpposite())) {
         return BlockPipe.ConnectionType.NONE;
      } else {
         return BlockPipe.ConnectionType.PROP;
      }
   }

   public static enum ConnectionType implements IStringSerializable {
      NONE(false, false, false),
      PIPE(true, true, false),
      TERMINAL(true, true, true),
      PROP(true, false, false);

      public final boolean isSolid;
      public final boolean allowsItems;
      public final boolean isFlared;

      private ConnectionType(boolean isSolid, boolean allowsItems, boolean isFlared) {
         this.isSolid = isSolid;
         this.allowsItems = allowsItems;
         this.isFlared = isFlared;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
