package svenhjol.meson.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemHelper {
   public static List<ItemStack> availableTotems = new ArrayList<ItemStack>() {
      {
         new ItemStack(Items.TOTEM_OF_UNDYING);
      }
   };
   public static List<ItemStack> curativeItems = new ArrayList<ItemStack>() {
      {
         new ItemStack(Items.MILK_BUCKET);
      }
   };

   public ItemHelper() {
   }

   public static List<ItemStack> getCurativeItems() {
      return (List)curativeItems.stream().distinct().collect(Collectors.toList());
   }

   public static ItemStack getFilledWaterBottle() {
      return getFilledWaterBottle(1);
   }

   public static ItemStack getFilledWaterBottle(int amount) {
      return getPotionBottle(amount, PotionTypes.WATER);
   }

   public static ItemStack getPotionBottle(int amount, PotionType type) {
      ItemStack out = new ItemStack(Items.POTIONITEM, amount);
      PotionUtils.addPotionToItemStack(out, type);
      return out;
   }

   public static boolean compareStacks(ItemStack s1, ItemStack s2) {
      return s1.getItem() == s2.getItem() && (s1.getMetadata() == 32767 || s1.getMetadata() == s2.getMetadata());
   }

   public static String getMatchingItemKey(List<String> items, ItemStack item) {
      String name = getItemStringFromItemStack(item, true);
      if (items.contains(name)) {
         return name;
      } else if (items.contains(name + "[*]")) {
         return name + "[*]";
      } else {
         String meta = "";
         if (name.contains("[")) {
            meta = name.substring(name.indexOf(91) + 1, name.indexOf(93));
            name = name.substring(0, name.indexOf(91));
         }

         if (items.contains(name)) {
            return name;
         } else {
            return !meta.isEmpty() && items.contains(name + "[*]") ? name + "[*]" : "";
         }
      }
   }

   public static String getItemStringFromItemStack(ItemStack item, boolean withMeta) {
      String itemName = ((ResourceLocation)Objects.requireNonNull(item.getItem().getRegistryName())).toString();
      int meta = withMeta ? item.getItemDamage() : 42;
      itemName = itemName + "[" + meta + "]";
      return itemName;
   }

   public static ItemStack getItemStackFromItemString(String name) {
      return getItemStackFromItemString(name, 0);
   }

   public static ItemStack getItemStackFromItemString(String name, int defaultMeta) {
      ItemStack stack = null;
      String meta = "";
      if (name.contains("[")) {
         meta = name.substring(name.indexOf(91) + 1, name.indexOf(93));
         name = name.substring(0, name.indexOf(91));
      }

      Item item = Item.getByNameOrId(name);
      if (item != null) {
         if (!meta.equals("*") && !meta.isEmpty()) {
            stack = new ItemStack(item, 1, Integer.parseInt(meta));
         } else {
            stack = new ItemStack(item, 1, defaultMeta);
         }
      }

      return stack;
   }

   public static List<ItemStack> getItemStacksFromItemString(String name) {
      return getItemStacksFromItemString(name, 0);
   }

   public static List<ItemStack> getItemStacksFromItemString(String name, int defaultMeta) {
      ArrayList<ItemStack> stacks = new ArrayList();
      String meta = "";
      if (name.contains("[")) {
         meta = name.substring(name.indexOf(91) + 1, name.indexOf(93));
         name = name.substring(0, name.indexOf(91));
      }

      Item item = Item.getByNameOrId(name);
      if (item != null) {
         if (!meta.equals("*") && !meta.isEmpty()) {
            stacks.add(new ItemStack(item, 1, Integer.parseInt(meta)));
         } else if (item.getHasSubtypes()) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, subItems);
            stacks.addAll(subItems);
         } else {
            stacks.add(new ItemStack(item, 1, defaultMeta));
         }
      }

      return stacks;
   }
}
