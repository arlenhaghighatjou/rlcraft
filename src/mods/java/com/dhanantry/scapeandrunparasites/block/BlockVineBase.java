package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.BlockVine;

public class BlockVineBase extends BlockVine {
   public BlockVineBase(String name, float hardness, boolean creative, boolean tickRandom) {
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setHardness(hardness);
      this.setTickRandomly(tickRandom);
      if (creative) {
         this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      }

   }
}
