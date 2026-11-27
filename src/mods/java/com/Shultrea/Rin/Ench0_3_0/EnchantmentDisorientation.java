package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IPotionDebuffer;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public class EnchantmentDisorientation extends EnchantmentBase implements IPotionDebuffer {
   public EnchantmentDisorientation() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Disorientation");
      this.setRegistryName("Disorientation");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.DisorientationEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Disorientation;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 50;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IPotionDebuffer);
   }

   public void register(EventBus bus) {
      bus.register(CriticalHitEvent.class, this::criticalWhenDisoriented);
   }

   public void criticalWhenDisoriented(CriticalHitEvent e) {
      if (e.getTarget() instanceof EntityLivingBase) {
         EntityLivingBase eb = (EntityLivingBase)e.getTarget();
         if (eb.isPotionActive(MobEffects.SLOWNESS) && eb.isPotionActive(MobEffects.NAUSEA) && EnchantmentHelper.getEnchantmentLevel(this, e.getEntityPlayer().getHeldItemMainhand()) >= 4) {
            e.setDamageModifier(e.getDamageModifier() + 0.25F);
         }

      }
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity entiti, ItemStack stack, int level) {
      if (entiti instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)entiti;
         if (entity.getRNG().nextInt(100) < 10 && (level == 1 || level == 2)) {
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 + level * 10, level - 1));
            entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80 + level * 10, 0));
         }

         if (level >= 3) {
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 + level * 10, level - 1));
            entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 + level * 10, level - 1));
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 25 + level * 7, 0));
         }

      }
   }
}
