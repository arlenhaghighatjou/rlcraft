package com.endertech.minecraft.forge.coremod.names;

public class FieldName extends ClassMemberName {
   protected FieldName(ClassName className, String normal, String obfuscated) {
      super(className, normal, obfuscated);
   }

   public static FieldName of(ClassName className, String name) {
      String obfuscated = (String)NotchMcp.getContentOf(className).FIELDS.getOrDefault(name, name);
      return new FieldName(className, name, obfuscated);
   }
}
