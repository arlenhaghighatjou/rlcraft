package vazkii.arl.util;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.arl.interf.IVariantHolder;
import vazkii.arl.item.ItemMod;

public class ProxyRegistry {
   private static Multimap<Class<?>, IForgeRegistryEntry<?>> entries = MultimapBuilder.hashKeys().arrayListValues().build();
   private static HashMap<Block, Item> temporaryItemBlockMap = new HashMap();

   public ProxyRegistry() {
   }

   public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistryEntry<T> obj) {
      if (obj != null) {
         entries.put(obj.getRegistryType(), obj);
         if (obj instanceof IVariantHolder) {
            ItemMod.variantHolders.add((IVariantHolder)obj);
         }

         if (obj instanceof ItemBlock) {
            ItemBlock iblock = (ItemBlock)obj;
            Block block = iblock.getBlock();
            temporaryItemBlockMap.put(block, iblock);
         }

      }
   }

   public static Item getItemMapping(Block block) {
      Item i = Item.getItemFromBlock(block);
      return i == Items.AIR && temporaryItemBlockMap.containsKey(block) ? (Item)temporaryItemBlockMap.get(block) : i;
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

   public static void register(EventBus bus) {
      bus.register(Register.class, ProxyRegistry::onRegistryEvent);
   }

   public static void onRegistryEvent(Register event) {
      Class<?> type = event.getRegistry().getRegistrySuperType();
      if (entries.containsKey(type)) {
         for(IForgeRegistryEntry<?> entry : entries.get(type)) {
            event.getRegistry().register(entry);
         }
      }

   }
}
