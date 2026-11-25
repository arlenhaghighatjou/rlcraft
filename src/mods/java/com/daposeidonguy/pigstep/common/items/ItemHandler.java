package com.daposeidonguy.pigstep.common.items;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;

public class ItemHandler {
   public static ItemDiscPigstep discPigstep;

   public ItemHandler() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, ItemHandler::registerItems);
      bus.register(ModelRegistryEvent.class, ItemHandler::registerRender);
   }

   public static void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{discPigstep = new ItemDiscPigstep("pigstep")});
   }

   public static void registerRender(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(discPigstep, 0, new ModelResourceLocation(discPigstep.getRegistryName(), "inventory"));
   }
}
