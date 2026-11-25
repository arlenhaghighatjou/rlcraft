package com.mujmajnkraft.bettersurvival.enchantments;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class EnchantmentSmelting extends Enchantment {
   public EnchantmentSmelting() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setRegistryName("smelting");
      this.setName("mujmajnkraftsbettersurvival.smelting");
   }

   public static void smeltDrops(List<ItemStack> drops, int fortuneLevel, EntityPlayer player) {
      for(int i = 0; i < drops.size(); ++i) {
         if (drops.get(i) != ItemStack.EMPTY && ((ItemStack)drops.get(i)).getItem() != Items.AIR) {
            ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult((ItemStack)drops.get(i)).copy();
            if (smeltingResult != ItemStack.EMPTY && smeltingResult.getItem() != Items.AIR) {
               smeltingResult.setCount(((ItemStack)drops.get(i)).getCount());
               if (!(smeltingResult.getItem() instanceof ItemBlock)) {
                  smeltingResult.setCount(player.getRNG().nextInt(fortuneLevel + 1) + smeltingResult.getCount());
               }

               drops.set(i, smeltingResult);
            }
         }
      }

   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 30;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return ForgeConfigHandler.enchantments.smeltingLevel;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.SILK_TOUCH;
   }

   public boolean isTreasureEnchantment() {
      return ForgeConfigHandler.enchantments.smeltingTreasure;
   }

   public boolean isAllowedOnBooks() {
      return ForgeConfigHandler.enchantments.smeltingLevel != 0;
   }
}
