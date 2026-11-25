package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSandyBricks extends BlockMod implements IQuarkBlock {
   public BlockSandyBricks() {
      super("sandy_bricks", Material.ROCK, new String[0]);
      this.setHardness(2.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
