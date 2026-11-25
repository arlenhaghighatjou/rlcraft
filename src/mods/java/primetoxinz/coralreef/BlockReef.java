package primetoxinz.coralreef;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;

public class BlockReef extends Block {
   public static final PropertyInteger TYPES = PropertyInteger.create("types", 0, 1);

   public BlockReef() {
      super(Material.ROCK);
      this.setCreativeTab(CreativeTabs.MISC);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      list.add(new ItemStack(this, 1, 0));
      list.add(new ItemStack(this, 1, 1));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPES});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPES, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(TYPES)).intValue();
   }

   public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
      return plantable.getPlantType(world, pos.offset(direction)) == BlockCoral.CORAL;
   }
}
