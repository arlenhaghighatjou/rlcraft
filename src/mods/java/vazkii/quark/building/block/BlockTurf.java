package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockMod;
import vazkii.quark.building.feature.Turf;

public class BlockTurf extends BlockMod implements Turf.ITurfBlock {
   public BlockTurf() {
      super("turf", Material.GRASS, new String[0]);
      this.setHardness(0.6F);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
