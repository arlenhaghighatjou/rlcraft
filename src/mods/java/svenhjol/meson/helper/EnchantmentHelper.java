package svenhjol.meson.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import svenhjol.meson.Meson;
import svenhjol.meson.iface.IMesonEnchantment;

public class EnchantmentHelper extends net.minecraft.enchantment.EnchantmentHelper {
   private static List<Enchantment> availableCurses = new ArrayList();

   public EnchantmentHelper() {
   }

   public static void addAvailableCurses(Enchantment... curses) {
      availableCurses.addAll(Arrays.asList(curses));
   }

   public static List<Enchantment> getItemCurses(ItemStack item) {
      NBTTagList tags = item.getEnchantmentTagList();
      List<Enchantment> curses = new ArrayList();
      if (!tags.hasNoTags()) {
         Map<Enchantment, Integer> enchants = getEnchantments(item);

         for(Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = (Enchantment)entry.getKey();
            if (enchantment != null && (enchantment.isCurse() || enchantment instanceof IMesonEnchantment.ICurse)) {
               curses.add(enchantment);
            }
         }
      }

      return curses;
   }

   public static boolean hasEnchantment(Enchantment enchantment, ItemStack item) {
      if (item.getItem() instanceof ItemEnchantedBook) {
         NBTTagList enchantments = ItemEnchantedBook.getEnchantments(item);

         for(int i = 0; i < enchantments.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = enchantments.getCompoundTagAt(i);
            Enchantment e = Enchantment.getEnchantmentByID(nbttagcompound.getShort("id"));
            if (e == enchantment) {
               return true;
            }
         }

         return false;
      } else {
         return getEnchantmentLevel(enchantment, item) > 0;
      }
   }

   public static boolean isItemCursed(ItemStack item) {
      return getItemCurses(item).size() > 0;
   }

   public static void applyRandomCurse(EntityPlayer player) {
      List<Enchantment> curses = new ArrayList();
      curses.add(Enchantments.VANISHING_CURSE);
      curses.add(Enchantments.BINDING_CURSE);
      curses.addAll(availableCurses);
      Enchantment curse = (Enchantment)curses.get((new EaglercraftRandom()).nextInt(curses.size()));
      List<ItemStack> curseableItems = new ArrayList();
      ItemStack itemToCurse = null;
      NonNullList<ItemStack> armorInventory = player.inventory.armorInventory;
      NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
      NonNullList<ItemStack> useInventory = null;
      if (curse != null && curse.type != null) {
         switch(curse.type) {
         case ARMOR:
         case WEARABLE:
            useInventory = armorInventory;
            break;
         case WEAPON:
         case DIGGER:
            useInventory = mainInventory;
            break;
         case ALL:
         default:
            if ((new EaglercraftRandom()).nextInt(2) == 0) {
               useInventory = mainInventory;
            } else {
               boolean hasArmor = false;

               for(ItemStack stack : armorInventory) {
                  if (!stack.isEmpty()) {
                     hasArmor = true;
                     break;
                  }
               }

               useInventory = hasArmor ? armorInventory : mainInventory;
            }
         }
      }

      if (useInventory != null) {
         for(ItemStack item : useInventory) {
            if (!item.isEmpty() && !isItemCursed(item) && (item.getItem() instanceof ItemEnchantedBook || item.isItemEnchanted() || item.isItemEnchantable()) && (!(item.getItem() instanceof ItemEnchantedBook) || getEnchantments(item).size() <= 2) && curse.canApplyAtEnchantingTable(item)) {
               curseableItems.add(item);
            }
         }
      }

      if (!curseableItems.isEmpty()) {
         itemToCurse = (ItemStack)curseableItems.get((new EaglercraftRandom()).nextInt(curseableItems.size()));
      }

      if (itemToCurse != null) {
         Map<Enchantment, Integer> existing = getEnchantments(itemToCurse);
         existing.put(curse, Integer.valueOf(1));
         setEnchantments(existing, itemToCurse);
      } else {
         Meson.debug("No item to curse!");
      }

   }

   public static void removeRandomCurse(ItemStack stack) {
      List<Enchantment> curses = getItemCurses(stack);
      if (curses.size() > 0) {
         Enchantment curseToRemove = (Enchantment)curses.get((new EaglercraftRandom()).nextInt(curses.size()));
         removeEnchantment(curseToRemove, stack);
      }

   }

   public static void removeEnchantment(Enchantment enchantment, ItemStack stack) {
      if (hasEnchantment(enchantment, stack)) {
         Map<Enchantment, Integer> enchants = getEnchantments(stack);
         enchants.remove(enchantment);
         setEnchantments(enchants, stack);
      }

   }
}
