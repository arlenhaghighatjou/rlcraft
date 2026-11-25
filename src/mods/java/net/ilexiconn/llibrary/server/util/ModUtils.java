package net.ilexiconn.llibrary.server.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

public class ModUtils {
   private static final ForgeRegistry<Item> ITEM_REGISTRY = RegistryManager.ACTIVE.getRegistry(GameData.ITEMS);
   private static final Map<String, ModContainer> resourceIDToContainerMap = new HashMap();

   public ModUtils() {
   }

   public static String getModNameForStack(ItemStack stack) {
      return getModNameForItem(stack.getItem());
   }

   public static String getModNameForBlock(Block block) {
      return getModNameForItem(Item.getItemFromBlock(block));
   }

   public static String getModNameForItem(Item item) {
      ResourceLocation resourceLocation = ITEM_REGISTRY.getKey(item);
      if (resourceLocation != null) {
         String modID = resourceLocation.getResourceDomain();
         String resourceID = modID.toLowerCase(Locale.ENGLISH);
         return getNameForResourceID(resourceID);
      } else {
         return null;
      }
   }

   public static String getNameForResourceID(String resourceID) {
      return ((ModContainer)resourceIDToContainerMap.get(resourceID)).getName();
   }

   public static ModContainer getContainerForStack(ItemStack stack) {
      return getContainerForItem(stack.getItem());
   }

   public static ModContainer getContainerForBlock(Block block) {
      return getContainerForItem(Item.getItemFromBlock(block));
   }

   public static ModContainer getContainerForItem(Item item) {
      ResourceLocation resourceLocation = ITEM_REGISTRY.getKey(item);
      if (resourceLocation != null) {
         String modID = resourceLocation.getResourceDomain();
         String resourceID = modID.toLowerCase(Locale.ENGLISH);
         return getContainerForResourceID(resourceID);
      } else {
         return null;
      }
   }

   public static ModContainer getContainerForResourceID(String resourceID) {
      return (ModContainer)resourceIDToContainerMap.get(resourceID);
   }

   static {
      resourceIDToContainerMap.put("minecraft", Loader.instance().getMinecraftModContainer());
      Map<String, ModContainer> map = Loader.instance().getIndexedModList();

      for(Entry<String, ModContainer> modEntry : map.entrySet()) {
         String resourceID = ((String)modEntry.getKey()).toLowerCase(Locale.ENGLISH);
         ModContainer modContainer = (ModContainer)modEntry.getValue();
         resourceIDToContainerMap.put(resourceID, modContainer);
      }

   }
}
