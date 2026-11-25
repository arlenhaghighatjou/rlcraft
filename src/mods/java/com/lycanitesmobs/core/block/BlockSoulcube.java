package com.lycanitesmobs.core.block;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockSoulcube extends BlockBase {
   public BlockSoulcube(ModInfo group, String name) {
      super(Material.IRON);
      this.setCreativeTab(LycanitesMobs.blocksTab);
      this.group = group;
      this.blockName = name;
      this.setup();
      this.setHardness(5.0F);
      this.setResistance(2000.0F);
      this.setHarvestLevel("pickaxe", 2);
      this.setSoundType(SoundType.GLASS);
   }
}
