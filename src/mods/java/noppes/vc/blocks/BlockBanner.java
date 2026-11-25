package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
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
import noppes.vc.blocks.tiles.TileBanner;

public class BlockBanner extends BlockBasicContainer {
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.0D, -1.0D, 0.0D, 1.0D, 1.0D, 1.0D);
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 5);
   public static final PropertyBool TOP = PropertyBool.create("top");

   public BlockBanner() {
      super(Material.ROCK);
      this.setSoundType(SoundType.STONE);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack item = player.inventory.getCurrentItem();
      if (!item.isEmpty() && !par1World.isRemote && hand == EnumHand.MAIN_HAND) {
         if (((Boolean)state.getValue(TOP)).booleanValue()) {
            pos = pos.down();
         }

         TileBanner tile = (TileBanner)par1World.getTileEntity(pos);
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

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
      par3List.add(new ItemStack(this, 1, 3));
      par3List.add(new ItemStack(this, 1, 4));
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue() + (((Boolean)state.getValue(TOP)).booleanValue() ? 7 : 0);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DAMAGE, Integer.valueOf(meta % 7)).withProperty(TOP, Boolean.valueOf(meta >= 7));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{DAMAGE, TOP});
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      try {
         if (((Boolean)world.getBlockState(pos).getValue(TOP)).booleanValue()) {
            return AABB2;
         }
      } catch (IllegalArgumentException var5) {
         ;
      }

      return AABB1;
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      if (!world.isAirBlock(pos.up())) {
         world.setBlockToAir(pos);
      } else {
         world.setBlockState(pos, this.getDefaultState().withProperty(TOP, Boolean.valueOf(false)).withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
         world.setBlockState(pos.up(), this.getDefaultState().withProperty(TOP, Boolean.valueOf(true)).withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
         int l = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
         l = l % 4;
         TileBanner tile = (TileBanner)world.getTileEntity(pos);
         tile.rotation = l;
         tile.color = 15 - stack.getItemDamage();
         tile.time = System.currentTimeMillis();
         if (entity instanceof EntityPlayer && world.isRemote) {
            ((EntityPlayer)entity).sendStatusMessage(new TextComponentTranslation("block.editIcon", new Object[0]), true);
         }
      }

   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return var2 < 7 ? new TileBanner() : null;
   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (((Boolean)state.getValue(TOP)).booleanValue() && world.getBlockState(pos.down()).getBlock() == this) {
         world.setBlockToAir(pos.down());
      } else if (!((Boolean)state.getValue(TOP)).booleanValue() && world.getBlockState(pos.up()).getBlock() == this) {
         world.setBlockToAir(pos.up());
      }

   }
}
