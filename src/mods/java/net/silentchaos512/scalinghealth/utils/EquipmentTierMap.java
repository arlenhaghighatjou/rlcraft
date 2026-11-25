package net.silentchaos512.scalinghealth.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.silentchaos512.scalinghealth.ScalingHealth;

public class EquipmentTierMap {
   public final int tierCount;
   public final EntityEquipmentSlot slot;
   List<List<StackProducer>> sets;

   public EquipmentTierMap(int tierCount, EntityEquipmentSlot slot) {
      this.tierCount = tierCount;
      this.slot = slot;
      this.sets = new ArrayList();

      for(int i = 0; i < tierCount; ++i) {
         this.sets.add(new ArrayList());
      }

   }

   public void put(ItemStack stack, int tier) {
      this.put(new StackProducer(stack), tier);
   }

   public void put(StackProducer producer, int tier) {
      if (tier >= 0 && tier < this.tierCount) {
         ((List)this.sets.get(tier)).add(producer);
      } else {
         throw new IllegalArgumentException("tier must be between 0 and " + this.tierCount);
      }
   }

   public ItemStack getRandom(int tier) {
      if (tier >= 0 && tier < this.tierCount) {
         List<StackProducer> list = (List)this.sets.get(tier);
         if (list.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            Random rand = ScalingHealth.random;
            return ((StackProducer)list.get(rand.nextInt(list.size()))).get(rand);
         }
      } else {
         throw new IllegalArgumentException("tier must be between 0 and " + this.tierCount);
      }
   }

   public ItemStack get(int tier, int index) {
      if (tier >= 0 && tier < this.tierCount) {
         List<StackProducer> list = (List)this.sets.get(tier);
         if (list.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (index >= 0 && index < list.size()) {
            return ((StackProducer)list.get(index)).get(ScalingHealth.random);
         } else {
            throw new IllegalArgumentException("index must be between 0 and " + list.size());
         }
      } else {
         throw new IllegalArgumentException("tier must be between 0 and " + this.tierCount);
      }
   }
}
