package com.Shultrea.Rin.Ench0_1_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class EnchantmentInefficient extends EnchantmentBase {
   public EnchantmentInefficient() {
      super(Rarity.RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Inefficent");
      this.setRegistryName("Inefficent");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.InefficentEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Inefficent;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean isCurse() {
      return true;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && fTest != Enchantments.EFFICIENCY && fTest != Smc_010.ExtremeEfficency;
   }

   public void register(EventBus bus) {
      bus.register(BreakSpeed.class, this::HandleEnchant);
   }

   public void HandleEnchant(BreakSpeed fEvent) {
      ItemStack tool = fEvent.getEntityLiving().getHeldItemMainhand();
      if (tool != null) {
         float OrigSpeed = fEvent.getOriginalSpeed();
         if (tool.getItem() instanceof ItemTool) {
            float blockHardness = fEvent.getState().getBlockHardness(fEvent.getEntityLiving().world, fEvent.getPos());
            if (EnchantmentHelper.getEnchantmentLevel(Smc_010.Inefficient, tool) > 0) {
               int levelInefficent = EnchantmentHelper.getEnchantmentLevel(Smc_010.Inefficient, tool);
               if (tool.getItem().canHarvestBlock(fEvent.getState())) {
                  float Speed = (float)levelInefficent * 0.65F + 2.0F;
                  fEvent.setNewSpeed(OrigSpeed / Speed - 0.15F * (float)levelInefficent);
               } else if (ForgeHooks.isToolEffective(fEvent.getEntityLiving().world, fEvent.getPos(), tool)) {
                  float Speed = (float)levelInefficent * 0.65F + 2.0F;
                  fEvent.setNewSpeed(OrigSpeed / Speed - 0.15F * (float)levelInefficent);
               }

            }
         }
      }
   }
}
