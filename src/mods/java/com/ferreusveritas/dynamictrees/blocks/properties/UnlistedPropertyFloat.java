package com.ferreusveritas.dynamictrees.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyFloat implements IUnlistedProperty<Float> {
   protected final String name;

   public UnlistedPropertyFloat(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(Float value) {
      return true;
   }

   public Class<Float> getType() {
      return Float.class;
   }

   public String valueToString(Float value) {
      return value.toString();
   }
}
