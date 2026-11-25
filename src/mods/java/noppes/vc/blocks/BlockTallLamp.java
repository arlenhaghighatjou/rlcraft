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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.CommonUtils;
import noppes.vc.blocks.tiles.TileTallLamp;

public class BlockTallLamp extends BlockBasicContainer {
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.0D, -1.0D, 0.0D, 1.0D, 1.0D, 1.0D);
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 6);
   public static final PropertyBool IS_TOP = PropertyBool.create("istop");

   public BlockTallLamp() {
      super(Material.WOOD);
      this.setSoundType(SoundType.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(IS_TOP, Boolean.valueOf(false)));
      this.setLightLevel(1.0F);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack item = player.inventory.getCurrentItem();
      if (item != null && item.getItem() == Items.DYE && hand == EnumHand.MAIN_HAND) {
         if (((Boolean)state.getValue(IS_TOP)).booleanValue()) {
            pos = pos.down();
         }

         TileTallLamp tile = (TileTallLamp)par1World.getTileEntity(pos);
         int color = EnumDyeColor.byMetadata(item.getItemDamage()).getDyeDamage();
         if (tile.color != color) {
            CommonUtils.ConsumeItemStack(1, player);
            tile.color = color;
            tile.markDirty();
            par1World.notifyBlockUpdate(pos, state, state, 3);
         }

         return true;
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
      return ((Integer)state.getValue(DAMAGE)).intValue() + (((Boolean)state.getValue(IS_TOP)).booleanValue() ? 7 : 0);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DAMAGE, Integer.valueOf(meta % 7)).withProperty(IS_TOP, Boolean.valueOf(meta >= 7));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{DAMAGE, IS_TOP});
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      if (!world.isAirBlock(pos.up())) {
         world.setBlockToAir(pos);
      } else {
         int l = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
         l = l % 4;
         world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())).withProperty(IS_TOP, Boolean.valueOf(false)), 2);
         world.setBlockState(pos.up(), state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())).withProperty(IS_TOP, Boolean.valueOf(true)), 2);
         TileTallLamp tile = (TileTallLamp)world.getTileEntity(pos);
         tile.rotation = l;
         tile.color = 15 - stack.getItemDamage();
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      try {
         if (((Boolean)world.getBlockState(pos).getValue(IS_TOP)).booleanValue()) {
            return AABB2;
         }
      } catch (IllegalArgumentException var5) {
         ;
      }

      return AABB1;
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
      return var2 < 7 ? new TileTallLamp() : null;
   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (((Boolean)state.getValue(IS_TOP)).booleanValue() && world.getBlockState(pos.down()).getBlock() == this) {
         world.setBlockToAir(pos.down());
      } else if (!((Boolean)state.getValue(IS_TOP)).booleanValue() && world.getBlockState(pos.up()).getBlock() == this) {
         world.setBlockToAir(pos.up());
      }

   }
}
