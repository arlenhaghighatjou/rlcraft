package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileTable;

public class BlockTable extends BlockBasicRotated {
   public BlockTable() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
      this.setLightOpacity(-1);
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

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      super.onBlockPlacedBy(world, pos, state, entity, stack);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileTable();
   }
}
