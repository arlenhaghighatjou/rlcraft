package com.oblivioussp.spartanshields.util;

import net.minecraft.item.Item;

public class MappingConverterEntry {
   protected String name;
   protected Item itemToConvertTo;

   public MappingConverterEntry(String missingName, Item itemConvert) {
      this.name = missingName;
      this.itemToConvertTo = itemConvert;
   }

   public String getMappingName() {
      return this.name;
   }

   public Item getItemToConvertTo() {
      return this.itemToConvertTo;
   }
}
