package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.VariedCommodities;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileTrading;
import noppes.vc.constants.GuiType;

public class BlockTrading extends BlockBasicContainer {
   public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4000000059604645D, 1.0D);

   public BlockTrading() {
      super(Material.WOOD);
      this.setSoundType(SoundType.STONE);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return AABB;
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      int l = MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      l = l % 4;
      TileBasicRotation tile = (TileBasicRotation)world.getTileEntity(pos);
      tile.rotation = l;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         TileTrading tile = (TileTrading)world.getTileEntity(pos);
         if (tile.isFull()) {
            player.sendStatusMessage(new TextComponentTranslation("trader.busy", new Object[0]), true);
            return false;
         } else {
            player.openGui(VariedCommodities.Instance, GuiType.TRADING_BLOCK.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
            tile.addTrader(player);
            return true;
         }
      }
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
      return AABB.offset(pos);
   }

   public boolean isPassable(IBlockAccess world, BlockPos pos) {
      return true;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileTrading();
   }
}
