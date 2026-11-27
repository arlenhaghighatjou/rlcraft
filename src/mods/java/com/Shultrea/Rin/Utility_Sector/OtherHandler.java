package com.Shultrea.Rin.Utility_Sector;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Prop_Sector.ArrowPropertiesProvider;
import com.Shultrea.Rin.Prop_Sector.IArrowProperties;
import com.Shultrea.Rin.Prop_Sector.PlayerPropertiesProvider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class OtherHandler {
   public OtherHandler() {
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, this::dealwithit);
      bus.register(LivingHurtEvent.class, EventPriority.HIGHEST, false, this::enchHand);
      bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, EventPriority.HIGHEST, true, this::onArrowCapAttach);
      bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, EventPriority.LOWEST, true, this::onPlayerCapAttach);
   }

   public void dealwithit(EntityJoinWorldEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)fEvent.getEntity();
         EntityLivingBase shooter = (EntityLivingBase)arrow.shootingEntity;
         if (shooter == null) {
            return;
         }

         ItemStack bow = shooter.getActiveItemStack();
         if (bow == null || bow == ItemStack.EMPTY) {
            bow = shooter.getHeldItemOffhand();
            if (bow == null || bow == ItemStack.EMPTY) {
               return;
            }
         }

         if (!arrow.hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
            return;
         }

         IArrowProperties properties = (IArrowProperties)arrow.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
         int p = ModConfig.enabled.SupremeFlame ? EnchantmentHelper.getEnchantmentLevel(Smc_040.supremeflame, bow) : 0;
         int l = ModConfig.enabled.StrafeEnable ? EnchantmentHelper.getEnchantmentLevel(Smc_030.Strafe, bow) : 0;
         if (Math.random() < (double)(0.125F * (float)l) && l > 0) {
            properties.setArrowRapidDamage(true);
         }

         if (p > 0) {
            properties.setFlameLevel(3);
            arrow.setFire(400);
         }

         int s = ModConfig.enabled.AdvancedFlame ? EnchantmentHelper.getEnchantmentLevel(Smc_040.advancedflame, bow) : 0;
         if (s > 0) {
            properties.setFlameLevel(2);
            arrow.setFire(200);
         }

         int t = ModConfig.enabled.LesserFlame ? EnchantmentHelper.getEnchantmentLevel(Smc_040.lesserflame, bow) : 0;
         if (t > 0) {
            properties.setFlameLevel(1);
         }
      }

   }

   public void enchHand(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType.equals("player") || fEvent.getSource().damageType.equals("mob")) {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (!EnchantmentBase.isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                     float levelmath = ModConfig.enabled.Mathematics ? (float)EnchantmentHelper.getEnchantmentLevel(Smc_040.Mathematics, dmgSource) : 0.0F;
                     float levelhistory = ModConfig.enabled.History ? (float)EnchantmentHelper.getEnchantmentLevel(Smc_040.History, dmgSource) : 0.0F;
                     float english = ModConfig.enabled.English ? (float)EnchantmentHelper.getEnchantmentLevel(Smc_040.English, dmgSource) : 0.0F;
                     if (levelmath > 0.0F) {
                        float hp = fEvent.getEntityLiving().getMaxHealth() * levelmath * 0.003125F;
                        float currentHp = fEvent.getEntityLiving().getHealth() * (0.025F * levelmath + 0.025F);
                        float FD = EnchantmentsUtility.CalculateDamageIgnoreSwipe(fEvent.getAmount(), hp + currentHp, 0.0F, 1.0F, attacker, Smc_040.Mathematics);
                        fEvent.setAmount(FD);
                     }

                     if (levelhistory > 0.0F) {
                        float history = (float)(attacker.getEntityWorld().getTotalWorldTime() / 20L);
                        history = history / 60.0F;
                        history = history / 12.0F;
                        history = history / 5.0F;
                        history = history / (6.0F - levelhistory);
                        if (history >= 5.0F * levelhistory * 0.5F) {
                           history = 5.0F * levelhistory * 0.5F;
                        }

                        float FD = EnchantmentsUtility.CalculateDamageIgnoreSwipe(fEvent.getAmount(), history, 0.0F, 1.0F, attacker, Smc_040.History);
                        fEvent.setAmount(FD);
                     }

                     if (english > 0.0F && (fEvent.getEntity() instanceof EntityWitch || fEvent.getEntity() instanceof EntityVillager || fEvent.getEntity() instanceof EntityWolf || fEvent.getEntity() instanceof EntitySnowman || fEvent.getEntity() instanceof EntityIronGolem || fEvent.getEntity() instanceof EntityPigZombie || fEvent.getEntity() instanceof EntityVindicator || fEvent.getEntity() instanceof EntityIllusionIllager || fEvent.getEntity() instanceof EntityEvoker)) {
                        float FD = EnchantmentsUtility.CalculateDamageIgnoreSwipe(fEvent.getAmount(), 0.0F, 3.0F, 1.0F, attacker, Smc_040.English);
                        fEvent.setAmount(FD);
                        if (Math.random() <= 0.4000000059604645D) {
                           fEvent.getEntityLiving().setSilent(true);
                        }
                     }

                  }
               }
            }
         }
      }
   }

   public void onArrowCapAttach(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityArrow && !((Entity)event.getObject()).hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
         event.addCapability(new ResourceLocation("somanyenchantments:arrow_capabilities"), new ArrowPropertiesProvider());
      }

   }

   public void onPlayerCapAttach(AttachCapabilitiesEvent<Entity> event) {
      if (!((Entity)event.getObject()).hasCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null)) {
         event.addCapability(new ResourceLocation("somanyenchantments:player_capabilities"), new PlayerPropertiesProvider());
      }

   }
}
