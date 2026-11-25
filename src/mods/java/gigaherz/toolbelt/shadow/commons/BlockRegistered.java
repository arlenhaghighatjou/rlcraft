package gigaherz.toolbelt.shadow.commons;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public class BlockRegistered extends Block {
   public BlockRegistered(String name, Material blockMaterialIn, MapColor blockMapColorIn) {
      super(blockMaterialIn, blockMapColorIn);
      this.setup(name);
   }

   public BlockRegistered(String name, Material materialIn) {
      super(materialIn);
      this.setup(name);
   }

   private void setup(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
   }

   public ItemBlock createItemBlock() {
      return (ItemBlock)(new ItemBlock(this)).setRegistryName(this.getRegistryName());
   }
}
