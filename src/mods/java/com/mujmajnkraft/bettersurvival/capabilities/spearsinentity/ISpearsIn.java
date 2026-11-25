package com.mujmajnkraft.bettersurvival.capabilities.spearsinentity;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public interface ISpearsIn {
   void addSpear(ItemStack var1);

   ArrayList<ItemStack> getSpearsIn();
}
