package com.endertech.minecraft.forge.materials;

import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitId;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;

public class RepairMatcher {
   public final ItemState materialState;
   public final int materialCount;

   protected RepairMatcher(ItemStack material) {
      this.materialState = ItemState.from(material);
      this.materialCount = material.getCount();
   }

   public static RepairMatcher from(ItemStack material) {
      return new RepairMatcher(material);
   }

   public static RepairMatcher from(AnvilUpdateEvent event) {
      ItemStack material = event.getRight();
      return from(material);
   }

   public boolean matches(UnitId id, int count) {
      return this.materialCount == count ? this.matches(id) : false;
   }

   public boolean matches(UnitId id) {
      for(ItemState state : id.getAllItemStates()) {
         if (state.matches(this.materialState)) {
            return true;
         }
      }

      return false;
   }
}
