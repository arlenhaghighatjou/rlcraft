package net.silentchaos512.scalinghealth.init;

import net.minecraft.item.Item;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.scalinghealth.item.ItemDifficultyChanger;
import net.silentchaos512.scalinghealth.item.ItemHealing;
import net.silentchaos512.scalinghealth.item.ItemHeartContainer;

public class ModItems {
   public static final ItemHeartContainer heart = new ItemHeartContainer();
   public static final Item crystalShard = new Item();
   public static final Item heartDust = new Item();
   public static final ItemHealing healingItem = new ItemHealing();
   public static final ItemDifficultyChanger difficultyChanger = new ItemDifficultyChanger();

   public ModItems() {
   }

   public static void registerAll(SRegistry reg) {
      reg.registerItem(heart, "heartcontainer");
      reg.registerItem(crystalShard, "crystalshard");
      reg.registerItem(heartDust, "heartdust");
      reg.registerItem(healingItem, "healingitem");
      reg.registerItem(difficultyChanger, "difficultychanger");
   }
}
