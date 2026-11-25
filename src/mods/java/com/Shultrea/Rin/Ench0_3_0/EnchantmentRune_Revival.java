package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IEnchantmentRune;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentRune_Revival extends EnchantmentBase implements IEnchantmentRune {
   public EnchantmentRune_Revival() {
      super(Rarity.VERY_RARE, EnumList.DAMAGEABLE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("Rune_Revival");
      this.setRegistryName("Rune_Revival");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.RevivalEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Revival;
   }

   public int getMinEnchantability(int par1) {
      return 30 + 30 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 60;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IEnchantmentRune);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return super.canApplyAtEnchantingTable(stack) && !(stack.getItem() instanceof ItemArmor);
   }

   @SubscribeEvent
   public void TriggeredEvent(PlayerDestroyItemEvent fEvent) {
      int amount = 0;
      EntityPlayer entity = (EntityPlayer)fEvent.getEntityLiving();
      ItemStack tool = fEvent.getOriginal();
      int levelR = EnchantmentHelper.getEnchantmentLevel(Smc_030.Rune_Revival, tool);
      if (levelR > 0) {
         if (levelR >= 3) {
            levelR = 2;
         }

         int durability = tool.getMaxDamage();
         float extraChance = durability > 1250 ? 0.0F : (durability > 750 ? 4.0F : (durability > 200 ? 6.0F : (durability > 80 ? 8.0F : (durability <= 80 ? 10.0F : 0.0F))));
         extraChance = extraChance / 100.0F;
         boolean test = EnchantmentsUtility.RANDOM.nextDouble() < (double)(0.15F + (float)levelR * 0.15F + extraChance);
         if (test) {
            ItemStack newTool = tool.copy();
            newTool.setItemDamage((int)((float)newTool.getItemDamage() - (float)newTool.getItemDamage() * 0.5F * (float)levelR));
            List<ItemStack> list = entity.inventory.mainInventory;
            int slot = entity.inventory.currentItem;
            boolean flag = EnchantmentsUtility.addItemStackToInventoryWithoutHolding(newTool, entity.inventory);
            if (!flag) {
               EntityItem entityItem = entity.entityDropItem(newTool, 1.3F);
               entityItem.setOwner(entity.getName());
               entityItem.setNoPickupDelay();
               entityItem.setEntityInvulnerable(true);
            }
         }

      }
   }

   public String getPrefix() {
      return TextFormatting.GREEN.toString();
   }
}
