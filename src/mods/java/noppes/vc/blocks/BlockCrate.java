package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.vc.VariedCommodities;
import noppes.vc.blocks.tiles.TileBasicContainer;
import noppes.vc.blocks.tiles.TileCrate;
import noppes.vc.constants.GuiType;

public class BlockCrate extends BlockBasicRotated {
   public BlockCrate() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (par1World.isRemote) {
         return true;
      } else {
         par1World.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, par1World.rand.nextFloat() * 0.1F + 0.9F);
         player.openGui(VariedCommodities.Instance, GuiType.CRATE.ordinal(), par1World, pos.getX(), pos.getY(), pos.getZ());
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

   public boolean isTopSolid(IBlockState state) {
      return true;
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      super.onBlockPlacedBy(world, pos, state, entity, stack);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileCrate();
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
