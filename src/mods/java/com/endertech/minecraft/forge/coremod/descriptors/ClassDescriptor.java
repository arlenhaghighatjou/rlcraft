package com.endertech.minecraft.forge.coremod.descriptors;

import com.endertech.minecraft.forge.coremod.names.ClassName;

public class ClassDescriptor extends Descriptor {
   private static final String PREFIX = "L";
   private static final String SUFFIX = ";";
   public final ClassName name;

   protected ClassDescriptor(ClassName name) {
      super("L", name, ";");
      this.name = name;
   }

   public static ClassDescriptor from(String normalName) {
      return new ClassDescriptor(ClassName.from(normalName));
   }

   public static ClassDescriptor from(ClassName name) {
      return new ClassDescriptor(name);
   }
}
