package lykrast.defiledlands.common.util;

import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.DungeonHooks.DungeonMob;

public class DungeonDefiledList {
   private static ArrayList<DungeonMob> dungeonMobs = new ArrayList();

   public DungeonDefiledList() {
   }

   public static float addDungeonMob(ResourceLocation name, int rarity) {
      if (rarity <= 0) {
         throw new IllegalArgumentException("Rarity must be greater then zero");
      } else {
         for(DungeonMob mob : dungeonMobs) {
            if (name.equals(mob.type)) {
               return (float)(mob.itemWeight += rarity);
            }
         }

         dungeonMobs.add(new DungeonMob(rarity, name));
         return (float)rarity;
      }
   }

   public static int removeDungeonMob(ResourceLocation name) {
      for(DungeonMob mob : dungeonMobs) {
         if (name.equals(mob.type)) {
            dungeonMobs.remove(mob);
            return mob.itemWeight;
         }
      }

      return 0;
   }

   public static ResourceLocation getRandomDungeonMob(EaglercraftRandom rand) {
      DungeonMob mob = (DungeonMob)WeightedRandom.getRandomItem(rand, dungeonMobs);
      return mob.type;
   }

   public static float addDungeonMob(String name, int rarity) {
      return addDungeonMob(new ResourceLocation("defiledlands", name), rarity);
   }
}
