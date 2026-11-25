package com.fantasticsource.tiamatitems.api;

import java.util.HashSet;
import net.minecraft.item.ItemStack;

public interface IPartSlot {
   String getSlotType();

   void setSlotType(String var1);

   boolean getRequired();

   void setRequired(boolean var1);

   ItemStack getPart();

   void setPart(ItemStack var1);

   HashSet<String> getValidItemTypes();

   boolean partIsValidForSlot(ItemStack var1);
}
