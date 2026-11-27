package com.Shultrea.Rin.Ench0_2_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IEnchantmentRune;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import com.Shultrea.Rin.Utility_Sector.UtilityAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentRune_MagicalBlessing extends EnchantmentBase implements IEnchantmentRune {
   public EnchantmentRune_MagicalBlessing() {
      super(Rarity.VERY_RARE, EnumList.SWORD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Rune_MagicalBlessing");
      this.setRegistryName("Rune_MagicalBlessing");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Rune_MagicalBlessingEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Rune_MagicalBlessing;
   }

   public int getMinEnchantability(int par1) {
      return 25 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 50;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IEnchantmentRune);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.LOWEST, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType.equals("player") || fEvent.getSource().damageType.equals("mob")) {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               EntityLivingBase entity = (EntityLivingBase)fEvent.getEntity();
               if (entity != null) {
                  ItemStack stack = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
                  if (stack != null) {
                     if (EnchantmentHelper.getEnchantmentLevel(this, stack) > 0) {
                        if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                           if (EnchantmentHelper.getEnchantmentLevel(Smc_010.Rune_PiercingCapabilities, stack) == 0) {
                              int level = EnchantmentHelper.getEnchantmentLevel(Smc_020.Rune_MagicalBlessing, stack);
                              float LevelDamage = fEvent.getAmount();
                              fEvent.setAmount(fEvent.getAmount() - fEvent.getAmount() * (float)level * 0.25F);
                              LevelDamage = LevelDamage * (float)level * 0.25F;
                              if (fEvent.getEntityLiving() instanceof EntityWitch) {
                                 LevelDamage *= 0.15F;
                              }

                              UtilityAccessor.damageTargetEvent(fEvent.getEntityLiving(), (new EntityDamageSource("player", attacker)).setMagicDamage(), LevelDamage);
                              if (EnchantmentsUtility.RANDOM.nextBoolean()) {
                                 Potion negaPotion = EnchantmentsUtility.getNonInstantNegativePotion();
                                 if (negaPotion != null) {
                                    fEvent.getEntityLiving().addPotionEffect(new PotionEffect(negaPotion, (MathHelper.clamp(EnchantmentsUtility.RANDOM.nextInt(6), 0, Integer.MAX_VALUE) + 1) * 20 * level, MathHelper.clamp(EnchantmentsUtility.RANDOM.nextInt(level) - 1, 0, Integer.MAX_VALUE)));
                                 }
                              } else {
                                 Potion negaIPotion = EnchantmentsUtility.getInstantNegativePotion();
                                 if (negaIPotion != null) {
                                    if (negaIPotion == MobEffects.INSTANT_DAMAGE && fEvent.getEntityLiving().isEntityUndead()) {
                                       negaIPotion = MobEffects.INSTANT_HEALTH;
                                    }

                                    fEvent.getEntityLiving().addPotionEffect(new PotionEffect(negaIPotion, 1, MathHelper.clamp(EnchantmentsUtility.RANDOM.nextInt(level) - 1, 0, Integer.MAX_VALUE)));
                                 }
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return 0.75F * (float)level;
   }

   public String getPrefix() {
      return TextFormatting.GREEN.toString();
   }
}
