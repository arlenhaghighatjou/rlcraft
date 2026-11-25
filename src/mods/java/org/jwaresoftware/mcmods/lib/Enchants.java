package org.jwaresoftware.mcmods.lib;

import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public final class Enchants {
   public static final int MAX_XAMPLIFIED_ENCHANTMENT_LEVEL_ALLOWED = 10;
   public static final int HIGH_FORTUNE = Enchantments.FORTUNE.getMaxLevel();
   public static final int MAX_FORTUNE = 10;
   public static final int HIGH_LOOTING = Enchantments.LOOTING.getMaxLevel();
   public static final int MAX_LOOTING = 10;
   public static final int MAX_ATTACK_DAMAGE = 10;
   public static final int HIGH_PROTECTION = Enchantments.PROTECTION.getMaxLevel();
   public static final int MAX_PROTECTION = 7;
   public static final int HIGH_UNBREAKING = Enchantments.UNBREAKING.getMaxLevel();
   public static final int MAX_UNBREAKING = 7;
   public static final int HIGH_CURSED_BINDING = Enchantments.BINDING_CURSE.getMaxLevel();
   public static final int MAX_CURSED_BINDING = 3;
   public static final int HIGH_EFFICIENCY = 5;
   public static final int MAX_EFFICIENCY = 10;
   public static final int INFINITE_INFINITY = 4;
   public static final String _NBT_ID = "id";
   public static final String _NBT_LEVEL = "lvl";
   public static final String _NBT_RES = "res";
   public static final Predicate<EnchantmentData> ANY_CURSE = new Predicate<EnchantmentData>() {
      public boolean apply(EnchantmentData edata) {
         return edata.enchantment == Enchantments.VANISHING_CURSE || edata.enchantment == Enchantments.BINDING_CURSE || edata.enchantment.isCurse();
      }
   };

   public static final void removeEnchantments(@Nonnull ItemStack stack) {
      if (stack.hasTagCompound()) {
         stack.getTagCompound().removeTag(SharedGlue.DEFAULT_NONBOOK_ENCHANTS_TAGNAME());
      }

   }

   public static final void initEnchantments(@Nonnull ItemStack to, NBTTagList enchantments, boolean copy) {
      removeEnchantments(to);
      if (enchantments != null && enchantments.tagCount() > 0) {
         to.setTagInfo(SharedGlue.DEFAULT_NONBOOK_ENCHANTS_TAGNAME(), copy ? enchantments.copy() : enchantments);
      }

   }

   public static final void copyinitEnchantments(@Nonnull ItemStack from, @Nonnull ItemStack to) {
      removeEnchantments(to);
      if (from.isItemEnchanted()) {
         to.setTagInfo(SharedGlue.DEFAULT_NONBOOK_ENCHANTS_TAGNAME(), from.getEnchantmentTagList().copy());
      }

   }

   public static final void hideEnchantments(@Nonnull ItemStack on) {
      if (on.hasTagCompound()) {
         int current = on.getTagCompound().getInteger(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME());
         on.getTagCompound().setInteger(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME(), current | 1);
      }

   }

   public static final boolean areHidden(@Nonnull ItemStack on) {
      boolean hidden = false;
      if (on.isEmpty()) {
         hidden = true;
      } else if (on.hasTagCompound() && on.getTagCompound().hasKey(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME(), 99)) {
         int flags = on.getTagCompound().getInteger(SharedGlue.ITEM_INFO_HIDDEN_TAGNAME());
         hidden = (flags & 1) == 1;
      }

      return hidden;
   }

   public static final int checkedXLevel(Enchantment e, int level) {
      if ((e == Enchantments.RESPIRATION || e == Enchantments.DEPTH_STRIDER) && level > e.getMaxLevel()) {
         level = e.getMaxLevel();
      }

      return level;
   }

   @Nullable
   public static final NBTTagList getBrokenEnchantments(@Nonnull ItemStack stack) {
      NBTTagList enchList = null;
      if (stack.hasTagCompound()) {
         enchList = stack.getTagCompound().getTagList("BrokenEnchantments", 10);
         if (enchList != null && enchList.tagCount() <= 0) {
            enchList = null;
         }
      }

      return enchList;
   }

   public static final int getBrokenEnchantmentsOriginalSetsize(@Nonnull ItemStack stack) {
      int n = -1;
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BrokenEnchantments_Setsize", 3)) {
         n = stack.getTagCompound().getInteger("BrokenEnchantments_Setsize");
         if (n > 10) {
            n = 10;
         }
      }

      return n;
   }

   public static final void setBrokenEnchantments(@Nonnull ItemStack stack, @Nullable NBTTagList enchList, int original_count) {
      if (enchList != null && enchList.tagCount() != 0) {
         stack.setTagInfo("BrokenEnchantments", enchList);
         if (original_count > 1) {
            stack.setTagInfo("BrokenEnchantments_Setsize", new NBTTagInt(original_count));
         }
      } else {
         stack.removeSubCompound("BrokenEnchantments");
      }

   }

   @Nullable
   public static final EnchantmentData getRawData(@Nonnull NBTTagCompound enchantmentNbt) {
      Enchantment enchantment = Enchantment.getEnchantmentByID(enchantmentNbt.getShort("id"));
      EnchantmentData edata = null;
      if (enchantment != null) {
         int level = enchantmentNbt.getShort("lvl");
         edata = new EnchantmentData(enchantment, level);
      }

      return edata;
   }

   @Nullable
   public static final EnchantmentData getData(@Nonnull NBTTagCompound enchantmentNbt, boolean clamp) {
      Enchantment enchantment = Enchantment.getEnchantmentByID(enchantmentNbt.getShort("id"));
      int level = enchantmentNbt.getShort("lvl");
      if (enchantment == null && enchantmentNbt.hasKey("res", 8)) {
         String resource = enchantmentNbt.getString("res");
         enchantment = Enchantment.getEnchantmentByLocation(resource);
      }

      EnchantmentData edata = null;
      if (enchantment != null) {
         if (clamp || level <= 0) {
            level = MathHelper.clamp(level, enchantment.getMinLevel(), enchantment.getMaxLevel());
         }

         edata = new EnchantmentData(enchantment, level);
      }

      return edata;
   }

   @Nonnull
   public static final Map<Enchantment, Integer> getEnchantments(@Nonnull ItemStack stack, boolean hiddenOk, Item broken) {
      Map<Enchantment, Integer> map = new LinkedHashMap();
      if (!ItemStacks.isEmpty(stack) && (hiddenOk || !areHidden(stack))) {
         Item stackitem = stack.getItem();
         NBTTagList var9;
         if (stackitem instanceof ItemEnchantedBook) {
            ItemEnchantedBook var10000 = (ItemEnchantedBook)stackitem;
            var9 = ItemEnchantedBook.getEnchantments(stack);
         } else {
            var9 = stackitem == broken ? getBrokenEnchantments(stack) : stack.getEnchantmentTagList();
         }

         NBTTagList enchList = var9;
         if (enchList != null) {
            for(int i = 0; i < enchList.tagCount(); ++i) {
               EnchantmentData edata = getData(enchList.getCompoundTagAt(i), false);
               if (edata != null) {
                  Integer oldLevel = (Integer)map.put(edata.enchantment, Integer.valueOf(edata.enchantmentLevel));
                  if (oldLevel != null && oldLevel.intValue() > edata.enchantmentLevel) {
                     map.put(edata.enchantment, oldLevel);
                  }
               }
            }
         }

         return map;
      } else {
         return map;
      }
   }

   @Nonnull
   public static final Map<Enchantment, Integer> getEnchantments(@Nonnull ItemStack stack) {
      return getEnchantments(stack, true, (Item)null);
   }

   @Nullable
   public static final EnchantmentData getFirst(@Nonnull ItemStack stack, boolean hiddenOk) {
      if (!ItemStacks.isEmpty(stack) && (hiddenOk || !areHidden(stack))) {
         NBTTagList var5;
         if (stack.getItem() instanceof ItemEnchantedBook) {
            ItemEnchantedBook var10000 = (ItemEnchantedBook)stack.getItem();
            var5 = ItemEnchantedBook.getEnchantments(stack);
         } else {
            var5 = stack.getEnchantmentTagList();
         }

         NBTTagList enchList = var5;
         if (enchList != null) {
            for(int i = 0; i < enchList.tagCount(); ++i) {
               EnchantmentData edata = getData(enchList.getCompoundTagAt(i), false);
               if (edata != null) {
                  return edata;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static final boolean isEnchanted(@Nonnull ItemStack stack, boolean hiddenOk) {
      EnchantmentData edata = getFirst(stack, hiddenOk);
      return edata != null;
   }

   public static final boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      boolean is = !ItemStacks.isEmpty(stack);
      if (is) {
         Item itemimpl = stack.getItem();
         if (!itemimpl.isEnchantable(stack)) {
            is = false;
         } else {
            for(Enchantment e : getEnchantments(book).keySet()) {
               if (!itemimpl.canApplyAtEnchantingTable(stack, e)) {
                  is = false;
                  break;
               }
            }
         }
      }

      return is;
   }

   public static final int getOtherCount(ItemStack itemstack, boolean stopquick, Enchantment... endemicList) {
      int n = 0;
      if (!ItemStacks.isEmpty(itemstack) && endemicList.length > 0) {
         NBTTagList enchList = itemstack.getEnchantmentTagList();

         for(int i = 0; i < enchList.tagCount(); ++i) {
            EnchantmentData edata = getData(enchList.getCompoundTagAt(i), false);
            if (edata != null) {
               boolean found = false;

               for(Enchantment owned : endemicList) {
                  if (edata.enchantment == owned) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  ++n;
                  if (stopquick) {
                     break;
                  }
               }
            }
         }
      }

      return n;
   }

   public static final void removeEnchantments(ItemStack stack, @Nullable Collection<Enchantment> filterList) {
      if (stack != null && stack.isItemEnchanted()) {
         if (filterList == null) {
            filterList = Collections.EMPTY_LIST;
         }

         if (filterList.isEmpty()) {
            removeEnchantments(stack);
         } else {
            NBTTagList curList = stack.getEnchantmentTagList();
            NBTTagList newList = new NBTTagList();

            for(int i = 0; i < curList.tagCount(); ++i) {
               EnchantmentData edata = getData(curList.getCompoundTagAt(i), false);
               if (filterList.contains(edata.enchantment)) {
                  newList.appendTag(curList.getCompoundTagAt(i));
               }
            }

            if (newList.tagCount() != curList.tagCount()) {
               if (newList.tagCount() == 0) {
                  removeEnchantments(stack);
               } else {
                  stack.setTagInfo(SharedGlue.DEFAULT_NONBOOK_ENCHANTS_TAGNAME(), newList);
               }
            }
         }
      }

   }

   public static final NBTTagList sameItemEnchantments(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
      NBTTagList outList = null;
      if (stack1.isItemEnchanted() && stack2.isItemEnchanted()) {
         NBTTagList tagList1 = stack1.getEnchantmentTagList();
         NBTTagList tagList2 = stack2.getEnchantmentTagList();
         int n;
         if ((n = tagList1.tagCount()) == tagList2.tagCount() && n > 0) {
            for(int i = 0; i < n; ++i) {
               EnchantmentData edata1 = getRawData(tagList1.getCompoundTagAt(i));
               if (edata1 == null) {
                  return null;
               }

               boolean match_found = false;

               for(int j = 0; j < n && !match_found; ++j) {
                  EnchantmentData edata2 = getRawData(tagList2.getCompoundTagAt(j));
                  if (edata2 == null) {
                     return null;
                  }

                  if (edata2.enchantment == edata1.enchantment) {
                     if (outList == null) {
                        outList = new NBTTagList();
                     }

                     if (edata1.enchantmentLevel <= edata2.enchantmentLevel) {
                        outList.appendTag(tagList1.getCompoundTagAt(i).copy());
                     } else {
                        outList.appendTag(tagList2.getCompoundTagAt(j).copy());
                     }

                     match_found = true;
                     break;
                  }
               }

               if (!match_found) {
                  return null;
               }
            }
         }
      }

      return outList;
   }

   public static final NBTTagList addEnchantmentToStack(Enchantment enchantment, int level, NBTTagList listNbt) {
      NBTTagCompound info = new NBTTagCompound();
      if (level <= 0) {
         level = enchantment.getMinLevel();
      }

      info.setShort("id", (short)Enchantment.getEnchantmentID(enchantment));
      info.setShort("lvl", (short)level);
      if (enchantment.getRegistryName() != null) {
         info.setString("res", enchantment.getRegistryName().toString());
      }

      listNbt.appendTag(info);
      return listNbt;
   }

   public static void addEnchantmentToStack(Enchantment enchantment, int level, @Nonnull ItemStack stack) {
      addEnchantmentToStack(enchantment, level, ItemStacks.getTagCompoundNonNull(stack));
   }

   public static final NBTTagCompound addEnchantmentToStack(Enchantment enchantment, int level, NBTTagCompound stackNbt) {
      NBTTagList listNbt = stackNbt.getTagList(SharedGlue.DEFAULT_NONBOOK_ENCHANTS_TAGNAME(), 10);
      addEnchantmentToStack(enchantment, level, listNbt);
      stackNbt.setTag(SharedGlue.DEFAULT_NONBOOK_ENCHANTS_TAGNAME(), listNbt);
      return stackNbt;
   }

   public static ItemStack mergeEnchantmentToStack(@Nonnull Enchantment enchantment, int level, @Nonnull ItemStack stack) {
      boolean add = level > 0;
      if (stack.isItemEnchanted()) {
         NBTTagList list = stack.getEnchantmentTagList();
         int i = 0;

         for(int n = list.tagCount(); i < n; ++i) {
            NBTTagCompound element = list.getCompoundTagAt(i);
            if (enchantment == Enchantment.getEnchantmentByID(element.getShort("id"))) {
               add = false;
               if (level <= 0) {
                  list.removeTag(i);
               } else {
                  int current_level = element.getShort("lvl");
                  if (level > current_level) {
                     element.setShort("lvl", (short)level);
                  }
               }
               break;
            }
         }
      }

      if (add) {
         stack.addEnchantment(enchantment, level);
      }

      return stack;
   }

   public static ItemStack clearEnchantmentFromStack(@Nonnull Enchantment enchantment, @Nonnull ItemStack stack) {
      return mergeEnchantmentToStack(enchantment, -1, stack);
   }

   public static final boolean isLikely(ItemStack stack, @Nonnull Predicate<Entry<Enchantment, Integer>> test) {
      boolean is = false;
      if (!ItemStacks.isEmpty(stack)) {
         for(Entry<Enchantment, Integer> e : getEnchantments(stack).entrySet()) {
            if (test.apply(e)) {
               is = true;
               break;
            }
         }
      }

      return is;
   }

   @Nonnull
   public static final List<EnchantmentData> strippedOf(@Nonnull List<EnchantmentData> elist, @Nonnull Predicate<EnchantmentData> test) {
      ListIterator<EnchantmentData> litr = elist.listIterator();

      while(litr.hasNext()) {
         if (test.apply(litr.next())) {
            litr.remove();
         }
      }

      return elist;
   }

   @Nonnull
   public static final List<EnchantmentData> strippedOfCurses(@Nonnull List<EnchantmentData> elist) {
      return strippedOf(elist, ANY_CURSE);
   }

   public static final List<EnchantmentData> strippedOfVanishingCurse(List<EnchantmentData> elist) {
      return strippedOf(elist, new Predicate<EnchantmentData>() {
         public boolean apply(EnchantmentData edata) {
            return edata.enchantment == Enchantments.VANISHING_CURSE;
         }
      });
   }

   protected Enchants() {
   }

   public static final void init0() {
   }
}
