package com.jamieswhiteshirt.trumpetskeleton.common;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import com.jamieswhiteshirt.trumpetskeleton.common.item.ItemTrumpet;

public class TrumpetSkeletonItems {
   public static ItemTrumpet TRUMPET = null;

   public TrumpetSkeletonItems() {
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("trumpetskeleton:TRUMPET"), v -> TRUMPET = (ItemTrumpet)v);
   }
}
