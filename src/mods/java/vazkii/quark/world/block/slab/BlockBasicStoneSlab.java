package vazkii.quark.world.block.slab;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockModSlab;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockBasicStoneSlab extends BlockQuarkSlab {
   public BlockBasicStoneSlab(String name, boolean doubleSlab) {
      super(name, Material.ROCK, doubleSlab);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static BlockModSlab initSlab(Block base, int meta, String name) {
      BlockModSlab slab = new BlockBasicStoneSlab(name, false);
      BlockModSlab.initSlab(base, meta, slab, new BlockBasicStoneSlab(name, true));
      return slab;
   }
}
