package com.alcatrazescapee.alcatrazcore.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictionaryHelper {
   public static final Converter<String, String> UPPER_UNDERSCORE_TO_LOWER_CAMEL = CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);
   public static final Joiner JOINER_UNDERSCORE = Joiner.on('_').skipNulls();
   private static final Multimap<OreDictionaryHelper.Element, String> MAP = LinkedListMultimap.create();
   private static boolean pastInit = false;

   public static void init() {
      if (!pastInit) {
         for(Entry<OreDictionaryHelper.Element, String> entry : MAP.entries()) {
            OreDictionary.registerOre((String)entry.getValue(), ((OreDictionaryHelper.Element)entry.getKey()).toStack());
         }

         MAP.clear();
         pastInit = true;
      }

   }

   public static void register(Block element, Object... parts) {
      if (pastInit) {
         OreDictionary.registerOre(toString(parts), new ItemStack(element, 1, 32767));
      } else {
         MAP.put(new OreDictionaryHelper.Element(element), toString(parts));
      }

   }

   public static void register(Item element, Object... parts) {
      if (pastInit) {
         OreDictionary.registerOre(toString(parts), new ItemStack(element, 1, 32767));
      } else {
         MAP.put(new OreDictionaryHelper.Element(element), toString(parts));
      }

   }

   public static void register(ItemStack element, Object... parts) {
      if (pastInit) {
         OreDictionary.registerOre(toString(parts), element);
      } else {
         MAP.put(new OreDictionaryHelper.Element(element.getItem(), element.getMetadata()), toString(parts));
      }

   }

   private static String toString(Object... parts) {
      return (String)UPPER_UNDERSCORE_TO_LOWER_CAMEL.convert(JOINER_UNDERSCORE.join(parts));
   }

   private OreDictionaryHelper() {
   }

   private static class Element {
      private Block block;
      private Item item;
      private int meta;

      private Element(Item item, int meta) {
         this.item = item;
         this.meta = meta;
      }

      private Element(Item item) {
         this(item, -1);
      }

      private Element(Block block) {
         this.block = block;
      }

      private ItemStack toStack() {
         if (this.block != null) {
            return new ItemStack(this.block);
         } else if (this.item != null) {
            return this.meta == -1 ? new ItemStack(this.item, 1, 32767) : new ItemStack(this.item, this.meta);
         } else {
            return ItemStack.EMPTY;
         }
      }
   }
}
