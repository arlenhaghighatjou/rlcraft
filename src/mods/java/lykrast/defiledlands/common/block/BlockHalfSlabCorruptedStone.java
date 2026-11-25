package lykrast.defiledlands.common.block;

import lykrast.defiledlands.common.init.ModBlocks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHalfSlabCorruptedStone extends BlockSlabCorruptedStone implements ICustomModel {
   public BlockHalfSlabCorruptedStone() {
   }

   public boolean isDouble() {
      return false;
   }

   public ItemBlock getItemBlock() {
      return new ItemSlab(this, this, (BlockSlab)ModBlocks.slabDoubleStone);
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      for(BlockSlabCorruptedStone.Variant v : BlockSlabCorruptedStone.Variant.values()) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), this.getMetaFromState(this.blockState.getBaseState().withProperty(HALF, EnumBlockHalf.BOTTOM).withProperty(VARIANT, v)), new ModelResourceLocation(this.getRegistryName() + "_" + v, "inventory"));
      }

   }
}
