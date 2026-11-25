package com.teamacronymcoders.contenttweaker.api.ctobjects.random;

import crafttweaker.annotations.ZenRegister;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.EaglercraftRandom")
public class CTRandom {
   private EaglercraftRandom random;

   public CTRandom(EaglercraftRandom random) {
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
