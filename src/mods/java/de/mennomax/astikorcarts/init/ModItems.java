package de.mennomax.astikorcarts.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import de.mennomax.astikorcarts.item.ItemCargoCart;
import de.mennomax.astikorcarts.item.ItemMobCart;
import de.mennomax.astikorcarts.item.ItemPlowCart;
import de.mennomax.astikorcarts.item.ItemWheel;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModItems {
   public static Item WHEEL = null;
   public static Item CARGOCART = null;
   public static Item PLOWCART = null;
   public static Item MOBCART = null;

   public ModItems() {
   }

   public static class ItemRegistrationHandler {
      public static final Set<Item> ITEMSET = new HashSet();

      public ItemRegistrationHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Item.class, ItemRegistrationHandler::registerItems);
         bus.register(ModelRegistryEvent.class, ItemRegistrationHandler::registerItemModels);
      }

      public static void registerItems(Register<Item> event) {
         Item[] ITEMS = new Item[]{new ItemWheel(), new ItemCargoCart(), new ItemPlowCart(), new ItemMobCart()};

         for(Item item : ITEMS) {
            event.getRegistry().register(item);
            ITEMSET.add(item);
         }

      }

      public static void registerItemModels(ModelRegistryEvent event) {
         for(Item item : ITEMSET) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
         }

      }
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("astikorcarts:WHEEL"), v -> WHEEL = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("astikorcarts:CARGOCART"), v -> CARGOCART = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("astikorcarts:PLOWCART"), v -> PLOWCART = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("astikorcarts:MOBCART"), v -> MOBCART = v);
   }
}
