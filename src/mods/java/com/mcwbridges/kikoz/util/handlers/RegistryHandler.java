package com.mcwbridges.kikoz.util.handlers;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.mcwbridges.kikoz.Mcwbridges;
import com.mcwbridges.kikoz.init.BlockInit;
import com.mcwbridges.kikoz.init.ItemInit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class RegistryHandler {
   public RegistryHandler() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, RegistryHandler::onItemRegister);
      bus.register(ModelRegistryEvent.class, RegistryHandler::onModelRegister);
      bus.registerGeneric(Register.class, Block.class, RegistryHandler::onBlockRegister);
   }

   public static void onItemRegister(Register<Item> event) {
      event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
   }

   public static void onModelRegister(ModelRegistryEvent event) {
      for(Item item : ItemInit.ITEMS) {
         Mcwbridges.proxy.registerItemRenderer(item, 0, "inventory");
      }

      for(Block block : BlockInit.BLOCKS) {
         Mcwbridges.proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");
      }

   }

   public static void onBlockRegister(Register<Block> event) {
      event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
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
