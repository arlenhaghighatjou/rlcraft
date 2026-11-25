package com.endertech.minecraft.forge.coremod.names;

public abstract class ClassMemberName extends Name {
   public final ClassName className;

   protected ClassMemberName(ClassName className, String normal, String obfuscated) {
      super(normal, obfuscated);
      this.className = className;
   }
}
