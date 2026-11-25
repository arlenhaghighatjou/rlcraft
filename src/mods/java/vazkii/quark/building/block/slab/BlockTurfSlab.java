package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.building.feature.Turf;

public class BlockTurfSlab extends BlockQuarkSlab implements Turf.ITurfBlock {
   public BlockTurfSlab(boolean doubleSlab) {
      super("turf_slab", Material.GRASS, doubleSlab);
      this.setHardness(0.6F);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
