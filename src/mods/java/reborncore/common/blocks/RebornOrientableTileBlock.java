package reborncore.common.blocks;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.api.ToolManager;
import reborncore.api.tile.IWrenchable;
import reborncore.common.BaseTileBlock;
import reborncore.common.items.WrenchHelper;

public abstract class RebornOrientableTileBlock extends BaseTileBlock implements IWrenchable {
   public static PropertyDirection FACING = BlockDirectional.FACING;

   protected RebornOrientableTileBlock() {
      super(Material.IRON);
      this.setHardness(2.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      BlockWrenchEventHandler.wrenableBlocks.add(this);
   }

   protected EnumFacing getFacingForPlacement(World worldIn, BlockPos pos, EntityLivingBase placer) {
      if (placer == null) {
         return EnumFacing.NORTH;
      } else {
         EnumFacing facing = placer.getHorizontalFacing().getOpposite();
         if (placer.rotationPitch < -50.0F) {
            facing = EnumFacing.DOWN;
         } else if (placer.rotationPitch > 50.0F) {
            facing = EnumFacing.UP;
         }

         return facing;
      }
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return null;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      EnumFacing newFacing = this.getFacingForPlacement(worldIn, pos, placer);
      if (this.canSetFacing(worldIn, pos, newFacing, (EntityPlayer)null)) {
         this.setFacing(worldIn, pos, newFacing, (EntityPlayer)null);
      }
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity == null) {
         return false;
      } else {
         return !stack.isEmpty() && ToolManager.INSTANCE.canHandleTool(stack) && WrenchHelper.handleWrench(stack, worldIn, pos, playerIn, side) ? true : super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
      }
   }

   public int getMetaFromState(IBlockState state) {
      int facingInt = this.getSideFromEnum((EnumFacing)state.getValue(FACING));
      return facingInt;
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing facing = this.getSideFromint(meta);
      return this.getDefaultState().withProperty(FACING, facing);
   }

   public EnumFacing getFacing(World world, BlockPos pos) {
      return (EnumFacing)world.getBlockState(pos).getValue(FACING);
   }

   public abstract EnumFacing getSideFromint(int var1);

   public abstract int getSideFromEnum(EnumFacing var1);

   public boolean setFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
      return world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, newFacing));
   }
}
