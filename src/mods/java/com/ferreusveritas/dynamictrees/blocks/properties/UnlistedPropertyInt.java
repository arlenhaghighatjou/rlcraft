package com.ferreusveritas.dynamictrees.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyInt implements IUnlistedProperty<Integer> {
   protected final String name;

   public UnlistedPropertyInt(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(Integer value) {
      return true;
   }

   public Class<Integer> getType() {
      return Integer.class;
   }

   public String valueToString(Integer value) {
      return value.toString();
   }
}
