package com.endertech.minecraft.forge.coremod.names;

import com.endertech.common.Args;

public class Name {
   public final String normal;
   public final String obfuscated;

   public Name(String normal, String obfuscated) {
      this.normal = normal;
      this.obfuscated = obfuscated;
   }

   public boolean complyWith(String string) {
      return this.normal.equals(string) || this.obfuscated.equals(string);
   }

   public String toString() {
      return Name.class.getSimpleName() + Args.group(Args.get("normal", this.normal), Args.get("obfuscated", this.obfuscated));
   }

   public String toString(boolean obfuscated) {
      return obfuscated ? this.obfuscated : this.normal;
   }
}
