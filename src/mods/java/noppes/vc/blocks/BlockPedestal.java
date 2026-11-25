package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileBasicContainer;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TilePedestal;

public class BlockPedestal extends BlockBasicTrigger {
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.0D, 0.20000000298023224D, 1.0D, 0.5D, 0.800000011920929D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.20000000298023224D, 0.0D, 0.0D, 0.800000011920929D, 0.5D, 1.0D);

   public BlockPedestal() {
      super(Blocks.STONE);
      this.setSoundType(SoundType.STONE);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!par1World.isRemote && hand == EnumHand.MAIN_HAND) {
         TilePedestal tile = (TilePedestal)par1World.getTileEntity(pos);
         ItemStack item = player.inventory.getCurrentItem();
         ItemStack weapon = tile.getStackInSlot(0);
         if (item.isEmpty() && !weapon.isEmpty()) {
            tile.setInventorySlotContents(0, ItemStack.EMPTY);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, weapon);
            tile.markDirty();
            par1World.notifyBlockUpdate(pos, state, state, 3);
            this.updateSurrounding(par1World, pos);
         } else {
            if (item.isEmpty() || !(item.getItem() instanceof ItemSword)) {
               return true;
            }

            if (!item.isEmpty() && weapon.isEmpty()) {
               tile.setInventorySlotContents(0, item);
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
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileBasicRotation)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileBasicRotation tile = (TileBasicRotation)tileentity;
         return tile.rotation % 2 == 0 ? AABB1 : AABB2;
      }
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      super.onBlockPlacedBy(world, pos, state, entity, stack);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TilePedestal();
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
