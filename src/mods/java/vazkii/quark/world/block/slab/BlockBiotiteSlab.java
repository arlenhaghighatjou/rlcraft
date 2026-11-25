package vazkii.quark.world.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockBiotiteSlab extends BlockQuarkSlab {
   public BlockBiotiteSlab(boolean doubleSlab) {
      super("biotite_slab", Material.ROCK, doubleSlab);
      this.setSoundType(SoundType.STONE);
      this.setHardness(0.8F);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
