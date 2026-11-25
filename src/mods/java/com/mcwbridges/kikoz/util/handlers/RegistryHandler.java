package com.mcwbridges.kikoz.util.handlers;

import com.mcwbridges.kikoz.Mcwbridges;
import com.mcwbridges.kikoz.init.BlockInit;
import com.mcwbridges.kikoz.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber
public class RegistryHandler {
   public RegistryHandler() {
   }

   @SubscribeEvent
   public static void onItemRegister(Register<Item> event) {
      event.getRegistry().registerAll((IForgeRegistryEntry[])ItemInit.ITEMS.toArray(new Item[0]));
   }

   @SubscribeEvent
   public static void onModelRegister(ModelRegistryEvent event) {
      for(Item item : ItemInit.ITEMS) {
         Mcwbridges.proxy.registerItemRenderer(item, 0, "inventory");
      }

      for(Block block : BlockInit.BLOCKS) {
         Mcwbridges.proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");
      }

   }

   @SubscribeEvent
   public static void onBlockRegister(Register<Block> event) {
      event.getRegistry().registerAll((IForgeRegistryEntry[])BlockInit.BLOCKS.toArray(new Block[0]));
   }

   public static void preInitRegistries() {
   }

   public static void initRegistries() {
   }

   public static void postInitRegistries() {
   }

   public static void serverRegistries(FMLServerStartingEvent event) {
   }
}
