package svenhjol.charm.enchanting.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import svenhjol.charm.enchanting.feature.CurseBreak;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.EnchantmentHelper;

public class EnchantmentCurseBreak extends MesonEnchantment {
   public EnchantmentCurseBreak() {
      super("curse_break", Rarity.RARE, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.MAINHAND);
   }

   public String getModId() {
      return "charm";
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean canApply(ItemStack stack) {
      return stack.getItem() == Items.BOOK;
   }

   public int getMaxLevel() {
      return 1;
   }

   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack in = event.getLeft();
      ItemStack combine = event.getRight();
      if (!in.isEmpty() && !combine.isEmpty() && combine.getItem() == Items.ENCHANTED_BOOK && EnchantmentHelper.hasEnchantment(this, combine)) {
         ItemStack out = in.copy();
         String name = out.getDisplayName();
         EnchantmentHelper.removeRandomCurse(out);
         if (!name.isEmpty()) {
            out.setStackDisplayName(name);
         }

         event.setCost(CurseBreak.xpCost);
         event.setOutput(out);
      }

   }
}
