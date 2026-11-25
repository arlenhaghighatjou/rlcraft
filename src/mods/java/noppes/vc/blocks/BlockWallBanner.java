package noppes.vc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.CommonUtils;
import noppes.vc.blocks.tiles.TileWallBanner;

public class BlockWallBanner extends BlockBasicContainer {
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 6);
   public int renderId = -1;
   public static final AxisAlignedBB AABB0 = new AxisAlignedBB(0.0D, 0.0D, 0.699999988079071D, 1.0D, 1.0D, 1.0D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.30000001192092896D, 1.0D, 1.0D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.30000001192092896D);
   public static final AxisAlignedBB AABB3 = new AxisAlignedBB(0.699999988079071D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

   public BlockWallBanner() {
      super(Material.ROCK);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack item = player.inventory.getCurrentItem();
      if (!item.isEmpty() && !par1World.isRemote && hand == EnumHand.MAIN_HAND) {
         TileWallBanner tile = (TileWallBanner)par1World.getTileEntity(pos);
         if (tile.canEdit()) {
            return true;
         } else if (item.getItem() != Items.DYE) {
            return false;
         } else {
            int color = EnumDyeColor.byMetadata(item.getItemDamage()).getDyeDamage();
            if (tile.color != color) {
               CommonUtils.ConsumeItemStack(1, player);
               tile.color = color;
               tile.markDirty();
               par1World.notifyBlockUpdate(pos, state, state, 3);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileWallBanner)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileWallBanner tile = (TileWallBanner)tileentity;
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

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      int l = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      l = l % 4;
      TileWallBanner tile = (TileWallBanner)world.getTileEntity(pos);
      tile.rotation = l;
      tile.color = 15 - stack.getItemDamage();
      tile.time = System.currentTimeMillis();
      if (entity instanceof EntityPlayer && world.isRemote) {
         ((EntityPlayer)entity).sendStatusMessage(new TextComponentTranslation("block.editIcon", new Object[0]), true);
      }

   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
      return new AxisAlignedBB(pos, pos);
   }

   public boolean isPassable(IBlockAccess world, BlockPos pos) {
      return true;
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
      par3List.add(new ItemStack(this, 1, 3));
      par3List.add(new ItemStack(this, 1, 4));
   }

   public int getMetaFromState(IBlockState state) {
      return this.damageDropped(state);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DAMAGE, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{DAMAGE});
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
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

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileWallBanner();
   }
}
