package knightminer.inspirations.utility.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.utility.tileentity.TileCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.BlockInventory;

public class BlockCollector extends BlockInventory {
   public static final PropertyDirection FACING = BlockDirectional.FACING;
   public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

   public BlockCollector() {
      super(Material.ROCK);
      this.setHardness(3.5F);
      this.setSoundType(SoundType.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, Boolean.valueOf(false)));
      this.setCreativeTab(CreativeTabs.REDSTONE);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, TRIGGERED});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(TRIGGERED, Boolean.valueOf((meta & 8) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      int meta = ((EnumFacing)state.getValue(FACING)).getIndex();
      if (((Boolean)state.getValue(TRIGGERED)).booleanValue()) {
         meta |= 8;
      }

      return meta;
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withMirror(IBlockState state, Mirror mirror) {
      return state.withRotation(mirror.toRotation((EnumFacing)state.getValue(FACING)));
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
      if (placer.isSneaking()) {
         facing = facing.getOpposite();
      }

      return this.getDefaultState().withProperty(FACING, facing);
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileCollector();
   }

   protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
      player.openGui(Inspirations.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
      return true;
   }

   /** @deprecated */
   @Deprecated
   public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
         IItemHandler handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
         if (handler != null) {
            return ItemHandlerHelper.calcRedstoneFromInventory(handler);
         }
      }

      return 0;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean powered = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());
      boolean triggered = ((Boolean)state.getValue(TRIGGERED)).booleanValue();
      if (powered && !triggered) {
         world.scheduleUpdate(pos, this, this.tickRate(world));
         world.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
      } else if (!powered && triggered) {
         world.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
      }

   }

   public int tickRate(World world) {
      return 4;
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!world.isRemote) {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileCollector) {
            ((TileCollector)te).collect((EnumFacing)state.getValue(FACING));
         }

      }
   }
}
