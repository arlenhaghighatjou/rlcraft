package com.ferreusveritas.dynamictrees.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;

public class RandomXOR extends EaglercraftRandom {
   private static final long serialVersionUID = -3477272122511092632L;
   int xor = 0;

   public RandomXOR() {
   }

   public RandomXOR(long seed) {
      super(seed);
   }

   public void setXOR(BlockPos pos) {
      this.setXOR((pos.getX() * 674365771 ^ pos.getZ() * 254326997) >> 4);
   }

   public void setXOR(int xor) {
      this.xor = xor;
   }

   protected int next(int bits) {
      return super.next(bits) ^ this.xor & (1 << bits) - 1;
   }
}
