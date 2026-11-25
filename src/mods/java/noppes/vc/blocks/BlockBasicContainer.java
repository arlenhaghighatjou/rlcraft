package noppes.vc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import noppes.vc.VCTabs;

public abstract class BlockBasicContainer extends BlockContainer {
   protected BlockBasicContainer(Material materialIn) {
      super(materialIn);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setCreativeTab(VCTabs.BLOCKS);
   }

   public Block register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName("variedcommodities", name);
      return this;
   }

   public boolean seperateMeta() {
      return false;
   }
}
