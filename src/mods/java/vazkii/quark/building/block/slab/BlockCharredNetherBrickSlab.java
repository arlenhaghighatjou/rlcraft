package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockCharredNetherBrickSlab extends BlockQuarkSlab {
   public BlockCharredNetherBrickSlab(boolean doubleSlab) {
      super("charred_nether_brick_slab", Material.ROCK, doubleSlab);
      this.setHardness(2.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
