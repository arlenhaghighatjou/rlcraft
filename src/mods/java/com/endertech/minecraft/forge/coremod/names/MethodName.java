package com.endertech.minecraft.forge.coremod.names;

import com.endertech.common.Args;

public class MethodName extends ClassMemberName {
   public final String srg;

   protected MethodName(ClassName className, String normal, String obfuscated, String srg) {
      super(className, normal, obfuscated);
      this.srg = srg;
   }

   public static MethodName of(ClassName className, String normalName) {
      return of(className, normalName, "");
   }

   public static MethodName of(ClassName className, String normalName, String srgName) {
      NotchMcp.ClassContent content = NotchMcp.getContentOf(className);
      String obfuscated = (String)content.METHODS.get(normalName);
      if (obfuscated == null) {
         obfuscated = normalName;
      }

      return new MethodName(className, normalName, obfuscated, srgName);
   }

   public boolean complyWith(String string) {
      return !this.srg.isEmpty() && this.srg.equals(string) || super.complyWith(string);
   }

   public String toString() {
      return MethodName.class.getSimpleName() + Args.group(Args.get("normal", this.normal), Args.get("obfuscated", this.obfuscated), Args.get("srg", this.srg));
   }
}
