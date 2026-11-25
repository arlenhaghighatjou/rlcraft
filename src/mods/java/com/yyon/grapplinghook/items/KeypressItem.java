package com.yyon.grapplinghook.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface KeypressItem {
   void onCustomKeyDown(ItemStack var1, EntityPlayer var2, KeypressItem.Keys var3, boolean var4);

   void onCustomKeyUp(ItemStack var1, EntityPlayer var2, KeypressItem.Keys var3, boolean var4);

   public static enum Keys {
      LAUNCHER,
      THROWLEFT,
      THROWRIGHT,
      THROWBOTH,
      ROCKET;

      private Keys() {
      }
   }
}
