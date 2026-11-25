package com.daposeidonguy.pigstep.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ItemHandler {
   public static ItemDiscPigstep discPigstep;

   public ItemHandler() {
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{discPigstep = new ItemDiscPigstep("pigstep")});
   }

   @SubscribeEvent
   public static void registerRender(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(discPigstep, 0, new ModelResourceLocation(discPigstep.getRegistryName(), "inventory"));
   }
}
