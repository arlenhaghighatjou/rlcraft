package svenhjol.meson.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ProxyRegistry {
   public static List<Item> items = new ArrayList();
   public static List<Block> blocks = new ArrayList();
   private static HashMap<Block, Item> itemBlockMap = new HashMap();
   private static Multimap<Class<?>, IForgeRegistryEntry<?>> entries = MultimapBuilder.hashKeys().arrayListValues().build();

   public ProxyRegistry() {
   }

   public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistryEntry<T> object) {
      entries.put(object.getRegistryType(), object);
      if (object instanceof ItemBlock) {
         ItemBlock itemBlock = (ItemBlock)object;
         Block block = itemBlock.getBlock();
         itemBlockMap.put(block, itemBlock);
      }

   }

   @SafeVarargs
   public static <T extends IForgeRegistryEntry<T>> void registerAll(IForgeRegistryEntry<T>... object) {
      for(IForgeRegistryEntry<T> entry : object) {
         entries.put(entry.getRegistryType(), entry);
      }

   }

   @SubscribeEvent
   public static void onRegister(Register event) {
      Class<?> type = event.getRegistry().getRegistrySuperType();
      if (entries.containsKey(type)) {
         for(IForgeRegistryEntry<?> entry : entries.get(type)) {
            event.getRegistry().register(entry);
         }
      }

   }

   public static Item getItemMapping(Block block) {
      Item item = Item.getItemFromBlock(block);
      return item == Items.AIR && itemBlockMap.containsKey(block) ? (Item)itemBlockMap.get(block) : item;
   }

   public static ItemStack newStack(Block block) {
      return newStack(block, 1);
   }

   public static ItemStack newStack(Block block, int size) {
      return newStack(block, size, 0);
   }

   public static ItemStack newStack(Block block, int size, int meta) {
      return newStack(getItemMapping(block), size, meta);
   }

   public static ItemStack newStack(Item item) {
      return newStack(item, 1);
   }

   public static ItemStack newStack(Item item, int size) {
      return newStack(item, size, 0);
   }

   public static ItemStack newStack(Item item, int size, int meta) {
      return new ItemStack(item, size, meta);
   }
}
