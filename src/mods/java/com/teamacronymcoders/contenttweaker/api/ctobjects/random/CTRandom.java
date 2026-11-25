package com.teamacronymcoders.contenttweaker.api.ctobjects.random;

import crafttweaker.annotations.ZenRegister;
import java.util.Random;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.Random")
public class CTRandom {
   private Random random;

   public CTRandom(Random random) {
      this.random = random;
   }

   @ZenMethod
   public boolean nextBoolean() {
      return this.random.nextBoolean();
   }

   @ZenMethod
   public int nextInt(int upperBound) {
      return this.random.nextInt(upperBound);
   }
}
