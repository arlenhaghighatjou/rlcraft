package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block {
   public BlockBase(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setHardness(hardness);
      this.setTickRandomly(tickRandom);
      if (creative) {
         this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      }

   }

   public BlockBase(Material material, String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
      this(material, name, hardness, creative, tickRandom);
      this.setResistance(resistance);
   }
}
