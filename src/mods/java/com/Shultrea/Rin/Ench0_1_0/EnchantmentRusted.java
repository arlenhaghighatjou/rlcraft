package com.Shultrea.Rin.Ench0_1_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Interfaces.IEnchantmentCurse;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EnchantmentRusted extends EnchantmentBase implements IEnchantmentCurse {
   int delayFactor = 10;
   int divisorMin;

   public EnchantmentRusted() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.divisorMin = 75 / this.delayFactor;
      this.setName("Rusted");
      this.setRegistryName("Rusted");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.RustedEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Rusted;
   }

   public int getMinEnchantability(int par1) {
      return 25 + 25 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 50;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && fTest != Enchantments.UNBREAKING;
   }

   public boolean canApply(ItemStack i) {
      return super.canApply(i) && !i.getUnlocalizedName().contains("gold");
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }

   public void register(EventBus bus) {
      bus.register(PlayerTickEvent.class, this::rustEvent);
   }

   public void rustEvent(PlayerTickEvent e) {
      if (e.phase == Phase.END) {
         EntityPlayer p = e.player;
         if (p != null) {
            if (e.side != Side.CLIENT) {
               if (p.getEntityWorld().getTotalWorldTime() % (long)this.delayFactor == 0L) {
                  p.inventory.mainInventory.stream().forEach((is) -> {
                     this.fireRustItemDamage(is, p);
                  });
                  p.inventory.armorInventory.stream().forEach((is) -> {
                     this.fireRustItemDamage(is, p);
                  });
                  p.inventory.offHandInventory.stream().forEach((is) -> {
                     this.fireRustItemDamage(is, p);
                  });
               }

            }
         }
      }
   }

   private void fireRustItemDamage(ItemStack is, EntityPlayer player) {
      if (is != null && player != null) {
         if (!is.isEmpty() && is.isItemStackDamageable() && is.getMaxDamage() > 0) {
            for(Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(is).entrySet()) {
               if (entry.getKey() == Smc_010.Rusted) {
                  int lvl = ((Integer)entry.getValue()).intValue();
                  if (lvl >= 1) {
                     EaglercraftRandom rand = player.getRNG();
                     int divisor = 100 - lvl * this.delayFactor;
                     if (divisor < this.divisorMin) {
                        divisor = this.divisorMin;
                     }

                     if (rand.nextInt(100) > divisor) {
                        double percentage = (double)(is.getMaxDamage() - is.getItemDamage());
                        percentage = percentage / (double)is.getMaxDamage();
                        if (percentage > (double)(0.15F * (float)lvl + 0.05F)) {
                           is.damageItem(rand.nextInt(lvl * 2 + 2), player);
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private int getLevel(ItemStack is, Enchantment enchantment) {
      int c = 0;
      if (!is.isEmpty()) {
         Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(is);

         for(Enchantment en : enchs.keySet()) {
            if (en == enchantment && ((Integer)enchs.get(en)).intValue() > 1 && c < ((Integer)enchs.get(en)).intValue()) {
               c = ((Integer)enchs.get(en)).intValue();
            }
         }
      }

      return c;
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity entiti, ItemStack stack, int level) {
      if (level > 0) {
         stack.damageItem(1 + user.getRNG().nextInt(level * 2), user);
      }

   }
}
