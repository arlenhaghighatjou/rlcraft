package rustic.common.blocks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.tileentity.TileEntityCondenser;
import rustic.core.Rustic;

public class BlockCondenser extends BlockBase implements ITileEntityProvider {
   public static final int GUI_ID = 4;
   public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
   protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

   public BlockCondenser() {
      super(Material.ROCK, "condenser");
      this.setHardness(2.0F);
      this.setCreativeTab(Rustic.alchemyTab);
      this.setSoundType(SoundType.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BOTTOM, Boolean.valueOf(true)));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return !((Boolean)state.getValue(BOTTOM)).booleanValue() ? TOP_AABB : super.getBoundingBox(state, source, pos);
   }

   public boolean isFullCube(IBlockState state) {
      return ((Boolean)state.getValue(BOTTOM)).booleanValue();
   }

   public boolean isOpaqueCube(IBlockState state) {
      return ((Boolean)state.getValue(BOTTOM)).booleanValue();
   }

   public int getLightOpacity(IBlockState state) {
      return ((Boolean)state.getValue(BOTTOM)).booleanValue() ? super.getLightOpacity(state) : 0;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(BOTTOM, Boolean.valueOf(true));
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (((Boolean)state.getValue(BOTTOM)).booleanValue()) {
         world.setBlockState(pos.up(), state.withProperty(BOTTOM, Boolean.valueOf(false)));
      }

   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      if ((((Boolean)state.getValue(BOTTOM)).booleanValue() && world.getBlockState(pos.up()).getBlock() == this || !((Boolean)state.getValue(BOTTOM)).booleanValue() && world.getBlockState(pos.down()).getBlock() == this) && !world.isRemote && !player.capabilities.isCreativeMode) {
         world.spawnEntity(new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, new ItemStack(this, 1, 0)));
      }

      if (((Boolean)state.getValue(BOTTOM)).booleanValue()) {
         world.setBlockToAir(pos.up());
      } else {
         world.setBlockToAir(pos.down());
      }

   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      return new ArrayList();
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState().withProperty(BOTTOM, Boolean.valueOf((meta & 4) > 0));
      return state.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
   }

   public int getMetaFromState(IBlockState state) {
      int meta = 0;
      if (((Boolean)state.getValue(BOTTOM)).booleanValue()) {
         meta |= 4;
      }

      meta = meta | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
      return meta;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, BOTTOM});
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return ((Boolean)this.getStateFromMeta(meta).getValue(BOTTOM)).booleanValue() ? new TileEntityCondenser() : null;
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (((Boolean)state.getValue(BOTTOM)).booleanValue()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity != null && tileentity instanceof TileEntityCondenser) {
            ((TileEntityCondenser)tileentity).breakBlock(worldIn, pos, state);
            tileentity.invalidate();
            worldIn.removeTileEntity(pos);
         }
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (((Boolean)state.getValue(BOTTOM)).booleanValue()) {
         if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityCondenser && this.hasRetorts(world, pos, state)) {
            if (!((TileEntityCondenser)world.getTileEntity(pos)).activate(world, pos, state, player, hand, side, hitX, hitY, hitZ)) {
               if (world.isRemote) {
                  return true;
               }

               player.openGui(Rustic.instance, 4, world, pos.getX(), pos.getY(), pos.getZ());
               return true;
            }

            return true;
         }
      } else if (!((Boolean)state.getValue(BOTTOM)).booleanValue() && world.getTileEntity(pos.down()) != null && world.getTileEntity(pos.down()) instanceof TileEntityCondenser && this.hasRetorts(world, pos.down(), state)) {
         if (!((TileEntityCondenser)world.getTileEntity(pos.down())).activate(world, pos.down(), state, player, hand, side, hitX, hitY, hitZ)) {
            if (world.isRemote) {
               return true;
            }

            player.openGui(Rustic.instance, 4, world, pos.getX(), pos.down().getY(), pos.getZ());
            return true;
         }

         return true;
      }

      return false;
   }

   public boolean hasRetorts(World world, BlockPos pos, IBlockState state) {
      if (state.getBlock() != this) {
         return false;
      } else {
         switch((EnumFacing)state.getValue(FACING)) {
         case NORTH:
            if (world.getBlockState(pos.east()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.west()).getBlock() != ModBlocks.RETORT) {
               return false;
            }

            if (world.getBlockState(pos.east()).getValue(BlockRetort.FACING) != EnumFacing.EAST || world.getBlockState(pos.west()).getValue(BlockRetort.FACING) != EnumFacing.WEST) {
               return false;
            }
            break;
         case SOUTH:
            if (world.getBlockState(pos.east()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.west()).getBlock() != ModBlocks.RETORT) {
               return false;
            }

            if (world.getBlockState(pos.east()).getValue(BlockRetort.FACING) != EnumFacing.EAST || world.getBlockState(pos.west()).getValue(BlockRetort.FACING) != EnumFacing.WEST) {
               return false;
            }
            break;
         case WEST:
            if (world.getBlockState(pos.north()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.south()).getBlock() != ModBlocks.RETORT) {
               return false;
            }

            if (world.getBlockState(pos.north()).getValue(BlockRetort.FACING) != EnumFacing.NORTH || world.getBlockState(pos.south()).getValue(BlockRetort.FACING) != EnumFacing.SOUTH) {
               return false;
            }
            break;
         case EAST:
            if (world.getBlockState(pos.north()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.south()).getBlock() != ModBlocks.RETORT) {
               return false;
            }

            if (world.getBlockState(pos.north()).getValue(BlockRetort.FACING) != EnumFacing.NORTH || world.getBlockState(pos.south()).getValue(BlockRetort.FACING) != EnumFacing.SOUTH) {
               return false;
            }
            break;
         default:
            return false;
         }

         return true;
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return ((Boolean)state.getValue(BOTTOM)).booleanValue() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }
}
