package com.elenai.elenaidodge.init;

import com.elenai.elenaidodge.item.ItemBase;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemInit {
   public static ItemBase GOLDEN_FEATHER;
   public static ItemBase IRON_FEATHER;

   public ItemInit() {
   }

   public static void init() {
      GOLDEN_FEATHER = new ItemBase("golden_feather");
      IRON_FEATHER = new ItemBase("iron_feather");
   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{GOLDEN_FEATHER, IRON_FEATHER});
   }
}
