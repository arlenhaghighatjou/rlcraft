package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockIronPlateSlab extends BlockQuarkSlab {
   public BlockIronPlateSlab(boolean doubleSlab) {
      super("iron_plate_slab", Material.ROCK, doubleSlab);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.METAL);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
