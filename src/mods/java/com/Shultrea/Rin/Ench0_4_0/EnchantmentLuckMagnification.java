package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IDamageMultiplier;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EnchantmentLuckMagnification extends EnchantmentBase implements IDamageMultiplier {
   public EnchantmentLuckMagnification() {
      super(Rarity.RARE, EnumList.COMBAT, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("LuckMagnification");
      this.setRegistryName("LuckMagnification");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.LuckMagnification;
   }

   public int getMaxLevel() {
      return ModConfig.level.LuckMagnification;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IDamageMultiplier);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onCritical(CriticalHitEvent e) {
      if (e.getEntityPlayer() != null) {
         if (e.getTarget() != null) {
            IAttributeInstance luck = e.getEntityPlayer().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK);
            float amount = (float)luck.getAttributeValue();
            if (amount != 0.0F) {
               if (e.getEntityPlayer().getHeldItemMainhand() != null) {
                  int level = EnchantmentHelper.getEnchantmentLevel(this, e.getEntityPlayer().getHeldItemMainhand());
                  if (level > 0) {
                     if ((float)e.getEntityPlayer().getRNG().nextInt(100) < Math.abs(amount * (float)level)) {
                        e.setDamageModifier(e.getDamageModifier() + amount * (float)level * 0.05F);
                     }

                  }
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void HandleEnchant(LootingLevelEvent fEvent) {
      if (fEvent.getDamageSource().getTrueSource() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)fEvent.getDamageSource().getTrueSource();
         ItemStack sword = player.getHeldItemMainhand();
         if (sword != null) {
            int level = EnchantmentHelper.getEnchantmentLevel(this, sword);
            if (level > 0) {
               IAttributeInstance luck = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK);
               int modifier = (int)((double)fEvent.getLootingLevel() + luck.getAttributeValue() * (double)level / 2.0D);
               fEvent.setLootingLevel(modifier);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void magnifyLuck(PlayerTickEvent e) {
      if (e.player != null) {
         if (e.phase != Phase.START) {
            if (e.player.getHeldItemMainhand() != null) {
               int level = EnchantmentHelper.getEnchantmentLevel(this, e.player.getHeldItemMainhand());
               if (level > 0) {
                  e.player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 10, level - 1, true, false));
               }

            }
         }
      }
   }
}
