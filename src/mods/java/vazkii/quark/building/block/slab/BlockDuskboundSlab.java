package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockDuskboundSlab extends BlockQuarkSlab {
   public BlockDuskboundSlab(boolean doubleSlab) {
      super("duskbound_block_slab", Material.ROCK, doubleSlab);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
