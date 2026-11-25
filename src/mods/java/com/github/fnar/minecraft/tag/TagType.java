package com.github.fnar.minecraft.tag;

public enum TagType {
   INT,
   STRING,
   COMPOUND,
   LIST;

   private TagType() {
   }

   public int getTypeId() {
      return this.ordinal();
   }
}
