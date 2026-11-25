package net.silentchaos512.scalinghealth.utils;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.potion.Potion;

public class MobPotionMap {
   List<MobPotionMap.PotionEntry> list = new ArrayList();
   List<MobPotionMap.PotionEntry> temp = new ArrayList();

   public MobPotionMap() {
   }

   public void put(Potion potion, int weight, int amplifier) {
      this.list.add(new MobPotionMap.PotionEntry(potion, weight, amplifier));
   }

   public void clear() {
      this.list.clear();
   }

   public MobPotionMap.PotionEntry getRandom(EaglercraftRandom rand, int maxWeight) {
      this.temp.clear();

      for(MobPotionMap.PotionEntry pot : this.list) {
         if (pot.cost <= maxWeight) {
            this.temp.add(pot);
         }
      }

      if (this.temp.isEmpty()) {
         return null;
      } else {
         return (MobPotionMap.PotionEntry)this.temp.get(rand.nextInt(this.temp.size()));
      }
   }

   public static class PotionEntry {
      public final Potion potion;
      public final int cost;
      public final int amplifier;

      public PotionEntry(Potion potion, int cost, int amplifier) {
         this.potion = potion;
         this.cost = cost;
         this.amplifier = amplifier;
      }
   }
}
