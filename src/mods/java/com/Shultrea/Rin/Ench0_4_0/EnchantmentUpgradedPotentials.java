package com.Shultrea.Rin.Ench0_4_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;

public class EnchantmentUpgradedPotentials extends EnchantmentBase {
   public EnchantmentUpgradedPotentials() {
      super(Rarity.RARE, EnumList.NONE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("upgrade");
      this.setRegistryName("upgrade");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Upgrade;
   }

   public int getMinEnchantability(int par1) {
      return 35;
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 45;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean canApply(ItemStack fTest) {
      return false;
   }

   public void register(EventBus bus) {
      bus.register(AnvilUpdateEvent.class, this::onAnvilAttach);
   }

   public void onAnvilAttach(AnvilUpdateEvent event) {
      ItemStack left = event.getLeft();
      ItemStack right = event.getRight();
      if (!left.isEmpty() && !right.isEmpty()) {
         if (right.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(right);
            if (enchantments.containsKey(this) && ((Integer)enchantments.get(this)).intValue() >= 1) {
               if (left.isStackable()) {
                  event.setOutput(ItemStack.EMPTY);
                  return;
               }

               if (EnchantmentHelper.getEnchantments(left).size() == 0) {
                  event.setOutput(ItemStack.EMPTY);
                  return;
               }

               if (EnchantmentHelper.getEnchantmentLevel(this, left) >= 1) {
                  event.setOutput(ItemStack.EMPTY);
                  return;
               }

               int cost = left.getRepairCost();
               cost = Math.max(0, cost / 4 - 20);
               ItemStack output = left.copy();
               output.setRepairCost(cost);
               output.addEnchantment(this, 1);
               event.setOutput(output);
               event.setCost(10);
            }
         }

      }
   }
}
