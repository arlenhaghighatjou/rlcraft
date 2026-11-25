package com.endertech.minecraft.forge.coremod.descriptors;

public enum Types {
   VOID("V"),
   BOOLEAN("Z"),
   BYTE("B"),
   CHAR("C"),
   SHORT("S"),
   INT("I"),
   LONG("J"),
   FLOAT("F"),
   DOUBLE("D");

   public final TypeDescriptor descriptor;

   private Types(String name) {
      this.descriptor = new TypeDescriptor(name);
   }
}
