package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.CommonUtils;
import noppes.vc.blocks.tiles.TileBigSign;
import noppes.vc.constants.GuiType;

public class BlockBigSign extends BlockBasicContainer {
   public static final AxisAlignedBB AABB0 = new AxisAlignedBB(0.0D, 0.0D, 0.8700000047683716D, 1.0D, 1.0D, 1.0D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.12999999523162842D, 1.0D, 1.0D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.12999999523162842D);
   public static final AxisAlignedBB AABB3 = new AxisAlignedBB(0.8700000047683716D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

   public BlockBigSign() {
      super(Material.WOOD);
      this.setSoundType(SoundType.WOOD);
   }

   public int getMetaFromState(IBlockState state) {
      return this.damageDropped(state);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BlockBasicRotated.DAMAGE, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockBasicRotated.DAMAGE});
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(BlockBasicRotated.DAMAGE)).intValue();
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileBigSign)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileBigSign tile = (TileBigSign)tileentity;
         if (tile.rotation == 0) {
            return AABB0;
         } else if (tile.rotation == 2) {
            return AABB2;
         } else if (tile.rotation == 3) {
            return AABB3;
         } else {
            return tile.rotation == 1 ? AABB1 : super.getBoundingBox(state, world, pos);
         }
      }
   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
      return this.getBoundingBox(state, world, pos).offset(pos);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (par1World.isRemote) {
         return false;
      } else {
         ItemStack currentItem = player.inventory.getCurrentItem();
         if (currentItem.getItem().getUnlocalizedName().equals("item.npcwand")) {
            TileBigSign tile = (TileBigSign)par1World.getTileEntity(pos);
            tile.canEdit = true;
            CommonUtils.OpenGui(player, GuiType.BIG_SIGN, pos.getX(), pos.getY(), pos.getZ());
            return true;
         } else {
            return false;
         }
      }
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(BlockBasicRotated.DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      int l = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      l = l % 4;
      TileBigSign tile = (TileBigSign)world.getTileEntity(pos);
      tile.rotation = l;
      if (entity instanceof EntityPlayer && !world.isRemote) {
         CommonUtils.OpenGui((EntityPlayer)entity, GuiType.BIG_SIGN, pos.getX(), pos.getY(), pos.getZ());
      }

   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileBigSign();
   }
}
