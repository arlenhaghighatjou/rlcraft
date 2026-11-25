package com.fantasticsource.tiamatitems.api;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public interface ITiamatItemsNatives {
   ArrayList<IPartSlot> getPartSlots(ItemStack var1);

   boolean isUsable(ItemStack var1);
}
