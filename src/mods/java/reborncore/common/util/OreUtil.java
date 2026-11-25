package reborncore.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.RebornCore;

public class OreUtil {
   public static ArrayList<String> oreNames = new ArrayList();

   public OreUtil() {
   }

   public static boolean doesOreExistAndValid(String name) {
      if (OreDictionary.doesOreNameExist(name)) {
         NonNullList<ItemStack> oreList = OreDictionary.getOres(name);
         return !oreList.isEmpty();
      } else {
         return false;
      }
   }

   public static ItemStack getStackFromName(String name) {
      return getStackFromName(name, 1);
   }

   public static ItemStack getStackFromName(String name, int stackSize) {
      NonNullList<ItemStack> oreList = OreDictionary.getOres(name);
      if (oreList.isEmpty()) {
         throw new RuntimeException("Failed to get ore name:" + name);
      } else {
         ItemStack stack = ((ItemStack)oreList.get(0)).copy();
         stack.setCount(stackSize);
         return stack;
      }
   }

   public static void scanForOres() {
      String[] validPrefixes = new String[]{"ingot", "ore", "gem", "crushed", "plate", "nugget", "dustSmall", "dustTiny", "dust", "block"};

      for(String oreDicName : OreDictionary.getOreNames()) {
         for(String prefix : validPrefixes) {
            if (oreDicName.startsWith(prefix) && isValidName(oreDicName) && !oreNames.contains(oreDicName.replace(prefix, "").toLowerCase())) {
               oreNames.add(oreDicName.replace(prefix, "").toLowerCase());
            }
         }
      }

      RebornCore.logHelper.info("Found " + oreNames.size() + " ores");
   }

   public static boolean isValidName(String name) {
      String[] invalidNames = new String[]{"blockMetal"};

      for(String invalidName : invalidNames) {
         if (name.equalsIgnoreCase(invalidName)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isValidID(int id) {
      return isValidName(OreDictionary.getOreName(id));
   }

   public static void remove(String name) throws NoSuchFieldException, IllegalAccessException {
      int id = OreDictionary.getOreID(name);
      Field field = OreDictionary.class.getDeclaredField("idToStack");
      field.setAccessible(true);
      List<NonNullList<ItemStack>> idToStack = (List)field.get((Object)null);
      idToStack.remove(id);
      idToStack.add(id, NonNullList.withSize(0, ItemStack.EMPTY));
      OreDictionary.rebakeMap();
   }

   public static boolean hasIngot(String name) {
      return doesOreExistAndValid("ingot" + capitalizeFirstLetter(name));
   }

   public static boolean hasGem(String name) {
      return doesOreExistAndValid("gem" + capitalizeFirstLetter(name));
   }

   public static boolean hasOre(String name) {
      return doesOreExistAndValid("ore" + capitalizeFirstLetter(name));
   }

   public static boolean hasCrushedOre(String name) {
      return doesOreExistAndValid("crushed" + capitalizeFirstLetter(name));
   }

   public static boolean hasPlate(String name) {
      return doesOreExistAndValid("plate" + capitalizeFirstLetter(name));
   }

   public static boolean hasNugget(String name) {
      return doesOreExistAndValid("nugget" + capitalizeFirstLetter(name));
   }

   public static boolean hasDustSmall(String name) {
      return doesOreExistAndValid("dustSmall" + capitalizeFirstLetter(name));
   }

   public static boolean hasDust(String name) {
      return doesOreExistAndValid("dust" + capitalizeFirstLetter(name));
   }

   public static boolean hasBlock(String name) {
      return doesOreExistAndValid("block" + capitalizeFirstLetter(name));
   }

   public static String capitalizeFirstLetter(String original) {
      return original.length() == 0 ? original : original.substring(0, 1).toUpperCase() + original.substring(1);
   }

   public static void registerOre(String name, Item ore) {
      registerOre(name, new ItemStack(ore));
   }

   public static void registerOre(String name, Block ore) {
      registerOre(name, new ItemStack(ore));
   }

   public static void registerOre(String name, @Nonnull ItemStack ore) {
      OreDictionary.registerOre(sanitiseString(name), ore);
   }

   public static String sanitiseString(String input) {
      StringBuilder builder = new StringBuilder();
      String[] split = input.split("_");
      boolean isFirst = true;

      for(String str : split) {
         builder.append(isFirst ? str : capitalizeFirstLetter(str));
      }

      return builder.toString();
   }
}
