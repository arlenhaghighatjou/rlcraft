package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockBlazeLantern extends BlockMod implements IQuarkBlock {
   public BlockBlazeLantern() {
      super("blaze_lantern", Material.GLASS, new String[0]);
      this.setHardness(0.3F);
      this.setSoundType(SoundType.GLASS);
      this.setLightLevel(1.0F);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }
}
