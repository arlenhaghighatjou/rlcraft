package com.endertech.minecraft.forge.coremod.descriptors;

import com.endertech.minecraft.forge.coremod.names.Name;

public class ArrayDescriptor extends Descriptor {
   private static final String PREFIX = "[";

   protected ArrayDescriptor(Name name) {
      super("[", name, "");
   }

   public static ArrayDescriptor of(Descriptor descriptor) {
      return new ArrayDescriptor(descriptor);
   }

   public static ArrayDescriptor of(Classes clazz) {
      return of(clazz.descriptor);
   }

   public static ArrayDescriptor of(Types type) {
      return of(type.descriptor);
   }
}
