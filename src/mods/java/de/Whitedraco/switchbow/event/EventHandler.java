package de.Whitedraco.switchbow.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.PotionInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.Config.BlackListConfig;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLuck;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowLuckUpgrade;
import de.Whitedraco.switchbow.helper.HelperMethodes;
import de.Whitedraco.switchbow.proxy.packets.ParticleFreezePacket;
import de.Whitedraco.switchbow.proxy.packets.ParticleLovePacket;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class EventHandler {
   private final int procentArrowBlock = 75;
   EaglercraftUUID loveUUID = EaglercraftUUID.fromString("93c6cc7f-a79e-4c07-b64a-91618d7d54b7");
   AttributeModifier Love;

   public EventHandler() {
      this.Love = new AttributeModifier(this.loveUUID, "Love", -1.0D, 1);
   }

   public void register(EventBus bus) {
      bus.register(LivingUpdateEvent.class, this::onEntityUpdate);
      bus.register(LootingLevelEvent.class, this::LootingLevelEvent);
      bus.register(LivingAttackEvent.class, this::onEntityAttacked);
   }

   public void onEntityUpdate(LivingUpdateEvent e) {
      EntityLivingBase eos = e.getEntityLiving();
      if (eos.isPotionActive(PotionInit.PotionFreeze)) {
         if (eos.getActivePotionEffect(PotionInit.PotionFreeze).getDuration() > 0 && !BlackListConfig.isBlacklistFrost(eos)) {
            eos.setPosition(eos.prevPosX, eos.posY, eos.prevPosZ);
            eos.motionX *= 0.0D;
            if (eos.motionY > 0.0D) {
               eos.motionY *= 0.0D;
            }

            eos.motionZ *= 0.0D;
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 1.0D;
            Initial.network.sendToAll(new ParticleFreezePacket(eos.posX, eos.posY, eos.posZ, d0, d1, d2));
            Initial.network.sendToAll(new ParticleFreezePacket(eos.posX, eos.posY, eos.posZ, d0, d1, d2));
         } else {
            eos.removePotionEffect(PotionInit.PotionFreeze);
         }
      }

      if (eos.isPotionActive(PotionInit.PotionLove)) {
         if (eos.getActivePotionEffect(PotionInit.PotionLove).getDuration() != 1 && !BlackListConfig.isBlacklistLove(eos)) {
            if (eos instanceof EntityAnimal) {
               EntityAnimal animal = (EntityAnimal)eos;
               if (!animal.isChild()) {
                  animal.setGrowingAge(0);
                  animal.setInLove((EntityPlayer)null);
               }

               eos.removePotionEffect(PotionInit.PotionLove);
            }

            if (!(eos instanceof EntityPlayer)) {
               if (eos.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null && !eos.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).hasModifier(this.Love)) {
                  eos.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(this.Love);
               }

               if (eos.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE) != null && !eos.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).hasModifier(this.Love)) {
                  eos.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(this.Love);
               }

               eos.setLastAttackedEntity((Entity)null);
               eos.setRevengeTarget((EntityLivingBase)null);
               if (e.getEntityLiving() instanceof EntityLiving) {
                  ((EntityLiving)eos).setAttackTarget((EntityLivingBase)null);
               }
            }

            if (eos instanceof EntityCreeper) {
               ((EntityCreeper)eos).setCreeperState(-1);
            }

            Initial.network.sendToAll(new ParticleLovePacket(eos.posX, eos.posY, eos.posZ, (double)eos.width, (double)eos.height));
         } else {
            if (!(eos instanceof EntityPlayer)) {
               if (eos.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE) != null && eos.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).hasModifier(this.Love)) {
                  eos.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(this.Love);
               }

               if (eos.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null && eos.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).hasModifier(this.Love)) {
                  eos.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(this.Love);
               }
            }

            eos.removePotionEffect(PotionInit.PotionLove);
         }
      }

   }

   public void LootingLevelEvent(LootingLevelEvent e) {
      DamageSource damageSource = e.getDamageSource();
      if (damageSource != null && damageSource.getImmediateSource() != null) {
         if (damageSource.getImmediateSource() instanceof EntityArrowLuck) {
            e.setLootingLevel(ArrowConfig.LootingArrowLuck.getInteger());
         } else if (damageSource.getImmediateSource() instanceof EntityArrowLuckUpgrade) {
            e.setLootingLevel(ArrowConfig.LootingArrowLuckUpgrade.getInteger());
         }
      }

   }

   public void onEntityAttacked(LivingAttackEvent e) {
      if (HelperMethodes.checkProbability(75) && HelperMethodes.hasFullArcherArmor(e.getEntityLiving()) && e.getSource().getImmediateSource() instanceof EntityArrow) {
         e.setCanceled(true);
      }

   }
}
