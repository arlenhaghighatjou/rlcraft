package com.bloodnbonesgaming.lib.util.data;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class ExtendedItemPredicate extends ItemPredicate {
   private final String oreDict;
   private final MinMaxBounds metaBounds;

   public ExtendedItemPredicate() {
      this.oreDict = null;
      this.metaBounds = MinMaxBounds.UNBOUNDED;
   }

   public ExtendedItemPredicate(@Nullable Item item, MinMaxBounds meta, MinMaxBounds count, MinMaxBounds durability, EnchantmentPredicate[] enchantments, @Nullable PotionType potion, NBTPredicate nbt, @Nullable String oreDict) {
      super(item, meta.min != null ? meta.min.intValue() : null, count, durability, enchantments, potion, nbt);
      this.oreDict = oreDict;
      this.metaBounds = meta;
   }

   public boolean test(ItemStack item) {
      if (this.item != null && item.getItem() != this.item) {
         return false;
      } else if (!this.metaBounds.test((float)item.getMetadata())) {
         return false;
      } else if (!this.count.test((float)item.getCount())) {
         return false;
      } else if (this.durability != MinMaxBounds.UNBOUNDED && !item.isItemStackDamageable()) {
         return false;
      } else if (!this.durability.test((float)(item.getMaxDamage() - item.getItemDamage()))) {
         return false;
      } else if (!this.nbt.test(item)) {
         return false;
      } else {
         if (this.oreDict != null) {
            boolean foundOreDict = false;
            NonNullList<ItemStack> ores = OreDictionary.getOres(this.oreDict, false);
            if (!ores.isEmpty()) {
               for(ItemStack stack : ores) {
                  if (OreDictionary.itemMatches(stack, item, false)) {
                     foundOreDict = true;
                     break;
                  }
               }
            }

            if (!foundOreDict) {
               return false;
            }
         }

         Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(item);

         for(int i = 0; i < this.enchantments.length; ++i) {
            if (!this.enchantments[i].test(map)) {
               return false;
            }
         }

         PotionType potiontype = PotionUtils.getPotionFromItem(item);
         if (this.potion != null && this.potion != potiontype) {
            return false;
         } else {
            return true;
         }
      }
   }
}
