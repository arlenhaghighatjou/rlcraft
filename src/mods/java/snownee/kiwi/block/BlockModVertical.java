package snownee.kiwi.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockModVertical extends BlockMod {
   public BlockModVertical(String name, Material materialIn) {
      super(name, materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.values()[meta & 1]);
   }

   public int getMetaFromState(IBlockState state) {
      return state.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP ? 0 : 1;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BlockSlab.HALF});
   }
}
