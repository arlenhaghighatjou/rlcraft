package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileBasicContainer;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileWeaponRack;

public class BlockWeaponRack extends BlockBasicTrigger {
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 6);
   public static final PropertyBool IS_TOP = PropertyBool.create("istop");
   public static final AxisAlignedBB AABB0 = new AxisAlignedBB(0.0D, 0.0D, 0.699999988079071D, 1.0D, 1.7999999523162842D, 1.0D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.30000001192092896D, 1.7999999523162842D, 1.0D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.7999999523162842D, 0.30000001192092896D);
   public static final AxisAlignedBB AABB3 = new AxisAlignedBB(0.699999988079071D, 0.0D, 0.0D, 1.0D, 1.7999999523162842D, 1.0D);
   public static final AxisAlignedBB AABBT0 = new AxisAlignedBB(0.0D, -1.0D, 0.699999988079071D, 1.0D, 0.800000011920929D, 1.0D);
   public static final AxisAlignedBB AABBT1 = new AxisAlignedBB(0.0D, -1.0D, 0.0D, 0.30000001192092896D, 0.800000011920929D, 1.0D);
   public static final AxisAlignedBB AABBT2 = new AxisAlignedBB(0.0D, -1.0D, 0.0D, 1.0D, 0.800000011920929D, 0.30000001192092896D);
   public static final AxisAlignedBB AABBT3 = new AxisAlignedBB(0.699999988079071D, -1.0D, 0.0D, 1.0D, 0.800000011920929D, 1.0D);

   public BlockWeaponRack() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(IS_TOP, Boolean.valueOf(false)));
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!par1World.isRemote && hand == EnumHand.MAIN_HAND) {
         if (((Boolean)state.getValue(IS_TOP)).booleanValue()) {
            pos = pos.down();
         }

         TileWeaponRack tile = (TileWeaponRack)par1World.getTileEntity(pos);
         float hit = hitX;
         if (tile.rotation == 2) {
            hit = 1.0F - hitX;
         }

         if (tile.rotation == 3) {
            hit = 1.0F - hitZ;
         }

         if (tile.rotation == 1) {
            hit = hitZ;
         }

         int selected = 2 - (int)((double)hit / 0.34D);
         ItemStack weapon = tile.getStackInSlot(selected);
         ItemStack item = player.inventory.getCurrentItem();
         if (item.isEmpty() && !weapon.isEmpty()) {
            tile.setInventorySlotContents(selected, ItemStack.EMPTY);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, weapon);
            tile.markDirty();
            par1World.notifyBlockUpdate(pos, state, state, 3);
            this.updateSurrounding(par1World, pos);
         } else {
            if (item.isEmpty() || item.getItem() instanceof ItemBlock) {
               return true;
            }

            if (!item.isEmpty() && weapon.isEmpty()) {
               tile.setInventorySlotContents(selected, item);
               player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
               tile.markDirty();
               par1World.notifyBlockUpdate(pos, state, state, 3);
               this.updateSurrounding(par1World, pos);
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
      par3List.add(new ItemStack(this, 1, 3));
      par3List.add(new ItemStack(this, 1, 4));
      par3List.add(new ItemStack(this, 1, 5));
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
         world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())).withProperty(IS_TOP, Boolean.valueOf(false)), 2);
         world.setBlockState(pos.up(), state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())).withProperty(IS_TOP, Boolean.valueOf(true)), 2);
         int l = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
         l = l % 4;
         TileBasicRotation tile = (TileBasicRotation)world.getTileEntity(pos);
         tile.rotation = l;
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      boolean isTop = false;

      try {
         isTop = ((Boolean)state.getValue(IS_TOP)).booleanValue();
      } catch (IllegalArgumentException var7) {
         ;
      }

      if (isTop) {
         pos = pos.down();
      }

      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileBasicRotation)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileBasicRotation tile = (TileBasicRotation)tileentity;
         if (tile.rotation == 0) {
            return isTop ? AABBT0 : AABB0;
         } else if (tile.rotation == 2) {
            return isTop ? AABBT2 : AABB2;
         } else if (tile.rotation == 3) {
            return isTop ? AABBT3 : AABB3;
         } else if (tile.rotation == 1) {
            return isTop ? AABBT1 : AABB1;
         } else {
            return super.getBoundingBox(state, world, pos);
         }
      }
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return var2 < 7 ? new TileWeaponRack() : null;
   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (((Boolean)state.getValue(IS_TOP)).booleanValue() && world.getBlockState(pos.down()).getBlock() == this) {
         world.setBlockToAir(pos.down());
      } else if (!((Boolean)state.getValue(IS_TOP)).booleanValue() && world.getBlockState(pos.up()).getBlock() == this) {
         world.setBlockToAir(pos.up());
      }

   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileBasicContainer tile = (TileBasicContainer)world.getTileEntity(pos);
      if (tile != null) {
         tile.dropItems(world, pos);
         world.updateComparatorOutputLevel(pos, state.getBlock());
         super.breakBlock(world, pos, state);
      }
   }
}
