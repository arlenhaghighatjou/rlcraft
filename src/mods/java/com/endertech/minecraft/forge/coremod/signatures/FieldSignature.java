package com.endertech.minecraft.forge.coremod.signatures;

import com.endertech.minecraft.forge.coremod.descriptors.Descriptor;
import com.endertech.minecraft.forge.coremod.names.ClassName;
import com.endertech.minecraft.forge.coremod.names.FieldName;

public class FieldSignature extends Signature {
   protected FieldSignature(ClassName className, FieldName fieldName, Descriptor descriptor) {
      super(className, fieldName, descriptor);
   }

   protected FieldSignature(String className, String fieldName, Descriptor descriptor) {
      super(className, fieldName, descriptor);
   }

   public static FieldSignature of(ClassName className, FieldName fieldName, Descriptor descriptor) {
      return new FieldSignature(className, fieldName, descriptor);
   }

   public static FieldSignature of(String className, String fieldName, Descriptor descriptor) {
      return new FieldSignature(className, fieldName, descriptor);
   }
}
