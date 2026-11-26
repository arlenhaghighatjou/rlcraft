package com.lycanitesmobs;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.base.Predicate;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.FearEntity;
import com.lycanitesmobs.core.info.CreatureGroup;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageEntityVelocity;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class PotionEffects {
   private static final EaglercraftUUID swiftswimmingMoveBoostUUID = EaglercraftUUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5e");
   private static final AttributeModifier swiftswimmingMoveBoost = (new AttributeModifier(swiftswimmingMoveBoostUUID, "Swiftswimming Speed Boost", 1.0D, 2)).setSaved(false);
   private static final EaglercraftUUID swiftswimmingMoveBoostUUID2 = EaglercraftUUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5d");
   private static final AttributeModifier swiftswimmingMoveBoost2 = (new AttributeModifier(swiftswimmingMoveBoostUUID2, "Swiftswimming Speed Boost 2", 2.0D, 2)).setSaved(false);
   private static final EaglercraftUUID swiftswimmingMoveBoostUUID3 = EaglercraftUUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5c");
   private static final AttributeModifier swiftswimmingMoveBoost3 = (new AttributeModifier(swiftswimmingMoveBoostUUID3, "Swiftswimming Speed Boost 3", 3.0D, 2)).setSaved(false);
   private static final EaglercraftUUID swiftswimmingMoveBoostUUID4 = EaglercraftUUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5b");
   private static final AttributeModifier swiftswimmingMoveBoost4 = (new AttributeModifier(swiftswimmingMoveBoostUUID4, "Swiftswimming Speed Boost 4", 4.0D, 2)).setSaved(false);
   public boolean disableNausea = false;

   public PotionEffects() {
   }

   public void init(ConfigBase config) {
      config.setCategoryComment("Potion Effects", "Here you can override each potion effect ID from the automatic ID, use 0 if you want it to stay automatic. Overrides should only be needed if you are running a lot of mods that add custom effects.");
      if (config.getBool("Potion Effects", "Enable Custom Effects", true, "Set to false to disable the custom potion effects.")) {
         ObjectManager.addEffect("paralysis", config, true, 16776960, false);
         ObjectManager.addEffect("penetration", config, true, 2236962, false);
         ObjectManager.addEffect("recklessness", config, true, 16711748, false);
         ObjectManager.addEffect("rage", config, true, 16729088, false);
         ObjectManager.addEffect("weight", config, true, 34, false);
         ObjectManager.addEffect("fear", config, true, 2228258, false);
         ObjectManager.addEffect("decay", config, true, 1114163, false);
         ObjectManager.addEffect("insomnia", config, true, 8738, false);
         ObjectManager.addEffect("instability", config, true, 17442, false);
         ObjectManager.addEffect("lifeleak", config, true, 22015, false);
         ObjectManager.addEffect("bleed", config, true, 16720418, false);
         ObjectManager.addEffect("plague", config, true, 2228326, false);
         ObjectManager.addEffect("aphagia", config, true, 16768477, false);
         ObjectManager.addEffect("smited", config, true, 14540287, false);
         ObjectManager.addEffect("smouldering", config, true, 14483456, false);
         ObjectManager.addEffect("leech", config, false, 65433, true);
         ObjectManager.addEffect("swiftswimming", config, false, 255, true);
         ObjectManager.addEffect("fallresist", config, false, 14548991, true);
         ObjectManager.addEffect("rejuvenation", config, false, 10092475, true);
         ObjectManager.addEffect("immunization", config, false, 6750139, true);
         ObjectManager.addEffect("cleansed", config, false, 6732799, true);
         ObjectManager.addEffect("repulsion", config, false, 12342062, true);
         ObjectManager.addEffect("heataura", config, false, 10053120, true);
         ObjectManager.addEffect("staticaura", config, false, 268154193, true);
         ObjectManager.addEffect("freezeaura", config, false, 5618687, true);
         ObjectManager.addEffect("envenom", config, false, 4513126, true);
         this.register(MinecraftForge.EVENT_BUS);
         AssetManager.addSound("effect_fear", LycanitesMobs.modInfo, "effect.fear");
      }

      this.disableNausea = config.getBool("Potion Effects", "Disable Nausea Debuff", this.disableNausea, "Set to true to disable the vanilla nausea debuff on players.");
   }

   public void register(EventBus bus) {
      bus.register(LivingUpdateEvent.class, this::onEntityUpdate);
      bus.register(LivingJumpEvent.class, this::onEntityJump);
      bus.register(LivingAttackEvent.class, this::onLivingAttack);
      bus.register(LivingHurtEvent.class, this::onLivingHurt);
      bus.register(LivingHealEvent.class, this::onEntityHeal);
      bus.register(PlayerSleepInBedEvent.class, this::onSleep);
      bus.register(LivingEntityUseItemEvent.class, this::onLivingUseItem);
   }

   public void onEntityUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         for(Object potionEffectObj : entity.getActivePotionEffects()) {
            if (potionEffectObj == null) {
               entity.clearActivePotions();
               LycanitesMobs.logWarning("EffectsSetup", "Found a null potion effect on entity: " + entity + " all effects have been removed from this entity.");
            }
         }

         if (entity.isPotionActive(MobEffects.BLINDNESS) && entity.isPotionActive(MobEffects.NIGHT_VISION)) {
            entity.removePotionEffect(MobEffects.BLINDNESS);
         }

         if (this.disableNausea && event.getEntityLiving() instanceof EntityPlayer && entity.isPotionActive(MobEffects.NAUSEA)) {
            entity.removePotionEffect(MobEffects.NAUSEA);
         }

         boolean invulnerable = false;
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            invulnerable = player.isCreative() || player.isSpectator();
         }

         PotionBase paralysis = ObjectManager.getEffect("paralysis");
         if (paralysis != null && !invulnerable && entity.isPotionActive(paralysis)) {
            entity.motionX = 0.0D;
            if (entity.motionY > 0.0D) {
               entity.motionY = 0.0D;
            }

            entity.motionZ = 0.0D;
            entity.onGround = false;
         }

         PotionBase weight = ObjectManager.getEffect("weight");
         if (weight != null && !invulnerable && entity.isPotionActive(weight) && !entity.isPotionActive(MobEffects.STRENGTH) && entity.motionY > -0.2D) {
            entity.motionY = -0.2D;
         }

         PotionBase fear = ObjectManager.getEffect("fear");
         if (fear != null && !entity.getEntityWorld().isRemote && !invulnerable && !this.isBoss(entity) && entity.isPotionActive(fear)) {
            ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
            if (extendedEntity != null && extendedEntity.fearEntity == null) {
               FearEntity fearEntity = new FearEntity(entity.getEntityWorld(), entity);
               entity.getEntityWorld().spawnEntity(fearEntity);
               extendedEntity.fearEntity = fearEntity;
            }
         }

         PotionBase instability = ObjectManager.getEffect("instability");
         if (instability != null && !entity.getEntityWorld().isRemote && !(entity instanceof IGroupBoss) && !invulnerable && entity.isPotionActive(instability) && entity.getEntityWorld().rand.nextDouble() <= 0.1D) {
            double strength = (double)(1 + entity.getActivePotionEffect(instability).getAmplifier());
            entity.motionX += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
            entity.motionY += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
            entity.motionZ += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);

            try {
               if (entity instanceof EntityPlayerMP) {
                  EntityPlayerMP player = (EntityPlayerMP)entity;
                  player.connection.sendPacket(new SPacketEntityVelocity(entity));
                  MessageEntityVelocity messageEntityVelocity = new MessageEntityVelocity(player, strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D), strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D), strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D));
                  LycanitesMobs.packetHandler.sendToPlayer(messageEntityVelocity, player);
               }
            } catch (Exception var17) {
               LycanitesMobs.logWarning("", "Failed to create and send a network packet for instability velocity!");
               var17.printStackTrace();
            }
         }

         PotionBase plague = ObjectManager.getEffect("plague");
         if (plague != null && !entity.getEntityWorld().isRemote && !invulnerable && entity.isPotionActive(plague)) {
            int poisonAmplifier = entity.getActivePotionEffect(plague).getAmplifier();
            int poisonDuration = entity.getActivePotionEffect(plague).getDuration();
            if (entity.isPotionActive(MobEffects.POISON)) {
               poisonAmplifier = Math.max(poisonAmplifier, entity.getActivePotionEffect(MobEffects.POISON).getAmplifier());
               poisonDuration = Math.max(poisonDuration, entity.getActivePotionEffect(MobEffects.POISON).getDuration());
            }

            entity.addPotionEffect(new PotionEffect(MobEffects.POISON, poisonDuration, poisonAmplifier));
            if (entity.getEntityWorld().getTotalWorldTime() % 20L == 0L) {
               for(Object entityObj : this.getNearbyEntities(entity, EntityLivingBase.class, (Class)null, 2.0D)) {
                  EntityLivingBase target = (EntityLivingBase)entityObj;
                  if (target != entity && !entity.isOnSameTeam(target)) {
                     int amplifier = entity.getActivePotionEffect(plague).getAmplifier();
                     int duration = entity.getActivePotionEffect(plague).getDuration();
                     if (amplifier > 0) {
                        target.addPotionEffect(new PotionEffect(plague, duration, amplifier - 1));
                     } else {
                        target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, amplifier));
                     }
                  }
               }
            }
         }

         PotionBase smited = ObjectManager.getEffect("smited");
         if (smited != null && !entity.getEntityWorld().isRemote && !invulnerable && entity.isPotionActive(smited) && entity.getEntityWorld().getTotalWorldTime() % 20L == 0L) {
            float brightness = entity.getBrightness();
            if (brightness > 0.5F && entity.getEntityWorld().canBlockSeeSky(entity.getPosition())) {
               entity.setFire(4);
            }
         }

         PotionBase bleed = ObjectManager.getEffect("bleed");
         if (bleed != null && !entity.getEntityWorld().isRemote && !invulnerable && entity.isPotionActive(bleed) && entity.getEntityWorld().getTotalWorldTime() % 20L == 0L && !entity.isRiding() && entity.prevDistanceWalkedModified != entity.distanceWalkedModified) {
            entity.attackEntityFrom(DamageSource.MAGIC, (float)(entity.getActivePotionEffect(bleed).getAmplifier() + 1));
         }

         PotionBase smouldering = ObjectManager.getEffect("smouldering");
         if (smouldering != null && !entity.getEntityWorld().isRemote && !invulnerable && entity.isPotionActive(smouldering) && entity.getEntityWorld().getTotalWorldTime() % 20L == 0L) {
            entity.setFire(4 + 4 * entity.getActivePotionEffect(smouldering).getAmplifier());
         }

         PotionBase swiftswimming = ObjectManager.getEffect("swiftswimming");
         if (swiftswimming != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            IAttributeInstance movement = entity.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
            int amplifier = -1;
            if (entity.isPotionActive(swiftswimming)) {
               amplifier = entity.getActivePotionEffect(swiftswimming).getAmplifier();
            }

            if (amplifier == 0 && movement.getModifier(swiftswimmingMoveBoostUUID) == null) {
               movement.applyModifier(swiftswimmingMoveBoost);
            } else if (amplifier != 0 && movement.getModifier(swiftswimmingMoveBoostUUID) != null) {
               movement.removeModifier(swiftswimmingMoveBoost);
            }

            if (amplifier == 1 && movement.getModifier(swiftswimmingMoveBoostUUID2) == null) {
               movement.applyModifier(swiftswimmingMoveBoost2);
            } else if (amplifier != 1 && movement.getModifier(swiftswimmingMoveBoostUUID2) != null) {
               movement.removeModifier(swiftswimmingMoveBoost2);
            }

            if (amplifier == 2 && movement.getModifier(swiftswimmingMoveBoostUUID3) == null) {
               movement.applyModifier(swiftswimmingMoveBoost3);
            } else if (amplifier != 2 && movement.getModifier(swiftswimmingMoveBoostUUID3) != null) {
               movement.removeModifier(swiftswimmingMoveBoost3);
            }

            if (amplifier >= 3 && movement.getModifier(swiftswimmingMoveBoostUUID4) == null) {
               movement.applyModifier(swiftswimmingMoveBoost4);
            } else if (amplifier < 3 && movement.getModifier(swiftswimmingMoveBoostUUID4) != null) {
               movement.removeModifier(swiftswimmingMoveBoost4);
            }
         }

         PotionBase immunization = ObjectManager.getEffect("immunization");
         if (immunization != null && !entity.getEntityWorld().isRemote && entity.isPotionActive(ObjectManager.getEffect("immunization"))) {
            if (entity.isPotionActive(MobEffects.POISON)) {
               entity.removePotionEffect(MobEffects.POISON);
            }

            if (entity.isPotionActive(MobEffects.HUNGER)) {
               entity.removePotionEffect(MobEffects.HUNGER);
            }

            if (entity.isPotionActive(MobEffects.WEAKNESS)) {
               entity.removePotionEffect(MobEffects.WEAKNESS);
            }

            if (entity.isPotionActive(MobEffects.NAUSEA)) {
               entity.removePotionEffect(MobEffects.NAUSEA);
            }

            if (ObjectManager.getEffect("paralysis") != null && entity.isPotionActive(ObjectManager.getEffect("paralysis"))) {
               entity.removePotionEffect(ObjectManager.getEffect("paralysis"));
            }
         }

         PotionBase cleansed = ObjectManager.getEffect("cleansed");
         if (ObjectManager.getEffect("cleansed") != null && !entity.getEntityWorld().isRemote && entity.isPotionActive(ObjectManager.getEffect("cleansed"))) {
            if (entity.isPotionActive(MobEffects.WITHER)) {
               entity.removePotionEffect(MobEffects.WITHER);
            }

            if (entity.isPotionActive(MobEffects.UNLUCK)) {
               entity.removePotionEffect(MobEffects.UNLUCK);
            }

            if (ObjectManager.getEffect("fear") != null && entity.isPotionActive(ObjectManager.getEffect("fear"))) {
               entity.removePotionEffect(ObjectManager.getEffect("fear"));
            }

            if (ObjectManager.getEffect("insomnia") != null && entity.isPotionActive(ObjectManager.getEffect("insomnia"))) {
               entity.removePotionEffect(ObjectManager.getEffect("insomnia"));
            }
         }

      }
   }

   public void onEntityJump(LivingJumpEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         boolean invulnerable = false;
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            invulnerable = player.capabilities.isCreativeMode;
         }

         if (!invulnerable) {
            PotionBase paralysis = ObjectManager.getEffect("paralysis");
            if (paralysis != null && entity.isPotionActive(paralysis) && event.isCancelable()) {
               event.setCanceled(true);
            }

            PotionBase weight = ObjectManager.getEffect("weight");
            if (weight != null && entity.isPotionActive(weight) && event.isCancelable()) {
               event.setCanceled(true);
            }

         }
      }
   }

   public void onLivingAttack(LivingAttackEvent event) {
      if (!event.isCancelable() || !event.isCanceled()) {
         if (event.getEntityLiving() != null) {
            EntityLivingBase target = event.getEntityLiving();
            EntityLivingBase attacker = null;
            if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
               attacker = (EntityLivingBase)event.getSource().getTrueSource();
            }

            if (attacker != null) {
               PotionBase lifeleak = ObjectManager.getEffect("lifeleak");
               if (lifeleak != null && !event.getEntityLiving().getEntityWorld().isRemote && attacker.isPotionActive(lifeleak)) {
                  if (event.isCancelable()) {
                     event.setCanceled(true);
                  }

                  target.heal(event.getAmount());
               }

            }
         }
      }
   }

   public void onLivingHurt(LivingHurtEvent event) {
      if (!event.isCancelable() || !event.isCanceled()) {
         if (event.getEntityLiving() != null) {
            EntityLivingBase target = event.getEntityLiving();
            Entity attacker = event.getSource().getTrueSource();
            PotionBase fallresist = ObjectManager.getEffect("fallresist");
            if (fallresist != null && event.getEntityLiving().isPotionActive(fallresist) && "fall".equals(event.getSource().damageType)) {
               event.setAmount(0.0F);
               event.setCanceled(true);
            }

            PotionBase penetration = ObjectManager.getEffect("penetration");
            if (penetration != null && event.getEntityLiving().isPotionActive(penetration)) {
               float damage = event.getAmount();
               float multiplier = 0.25F * (float)(event.getEntityLiving().getActivePotionEffect(penetration).getAmplifier() + 1);
               event.setAmount(damage + damage * multiplier);
            }

            PotionBase fear = ObjectManager.getEffect("fear");
            if (fear != null && !this.isBoss(event.getEntityLiving()) && event.getEntityLiving().isPotionActive(fear) && "inWall".equals(event.getSource().damageType)) {
               event.setAmount(0.0F);
               event.setCanceled(true);
            }

            PotionBase leech = ObjectManager.getEffect("leech");
            if (leech != null && event.getSource().getTrueSource() != null) {
               EntityLivingBase leechingEntity = null;
               if (event.getSource().getImmediateSource() instanceof EntityLivingBase) {
                  leechingEntity = (EntityLivingBase)event.getSource().getImmediateSource();
               } else if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                  leechingEntity = (EntityLivingBase)event.getSource().getTrueSource();
               }

               if (leechingEntity != null && leechingEntity.isPotionActive(leech)) {
                  int leeching = leechingEntity.getActivePotionEffect(leech).getAmplifier() + 1;
                  leechingEntity.heal((float)Math.max(leeching, 1));
               }
            }

            PotionBase repulsion = ObjectManager.getEffect("repulsion");
            if (repulsion != null) {
               boolean attackerIsBoss = attacker instanceof IGroupBoss;
               if (!attackerIsBoss && CreatureManager.getInstance().getCreatureGroup("boss") != null) {
                  attackerIsBoss = CreatureManager.getInstance().getCreatureGroup("boss").hasEntity(attacker);
               }

               if (attacker != null && !attackerIsBoss && target.isPotionActive(repulsion)) {
                  float knockback = (float)(target.getActivePotionEffect(repulsion).getAmplifier() + 2);
                  double xDist = attacker.getPositionVector().x - target.getPositionVector().x;
                  double zDist = attacker.getPositionVector().z - target.getPositionVector().z;
                  double xzDist = Math.max((double)MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
                  double motionCap = 10.0D;
                  double xVel = xDist / xzDist * (double)knockback;
                  double zVel = zDist / xzDist * (double)knockback;
                  if (attacker.motionX < motionCap && attacker.motionX > -motionCap && attacker.motionZ < motionCap && attacker.motionZ > -motionCap) {
                     attacker.addVelocity(xVel, 0.0D, zVel);
                  }
               }
            }

         }
      }
   }

   public void onEntityHeal(LivingHealEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null) {
         PotionBase rejuvenation = ObjectManager.getEffect("rejuvenation");
         if (rejuvenation != null && entity.isPotionActive(rejuvenation)) {
            event.setAmount((float)Math.ceil((double)(event.getAmount() * (float)(2 * (1 + entity.getActivePotionEffect(rejuvenation).getAmplifier())))));
         }

         PotionBase decay = ObjectManager.getEffect("decay");
         if (decay != null && entity.isPotionActive(decay)) {
            event.setAmount((float)Math.floor((double)(event.getAmount() / (float)(2 * (1 + entity.getActivePotionEffect(decay).getAmplifier())))));
         }

      }
   }

   public void onSleep(PlayerSleepInBedEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null && !player.getEntityWorld().isRemote && !event.isCanceled()) {
         PotionBase insomnia = ObjectManager.getEffect("insomnia");
         if (insomnia != null && player.isPotionActive(insomnia)) {
            event.setResult(SleepResult.NOT_SAFE);
         }

      }
   }

   public void onLivingUseItem(LivingEntityUseItemEvent event) {
      if (!event.isCancelable() || !event.isCanceled()) {
         if (event.getEntityLiving() != null) {
            PotionBase aphagia = ObjectManager.getEffect("aphagia");
            if (aphagia != null && !event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving().isPotionActive(aphagia) && event.isCancelable()) {
               event.setCanceled(true);
            }

         }
      }
   }

   public <T extends Entity> List<T> getNearbyEntities(Entity searchEntity, Class<? extends T> clazz, Class filterClass, double range) {
      return searchEntity.getEntityWorld().getEntitiesWithinAABB(clazz, searchEntity.getEntityBoundingBox().grow(range, range, range), (entity) -> {
         return filterClass == null ? true : filterClass.isAssignableFrom(entity.getClass());
      });
   }

   public boolean isBoss(Entity entity) {
      if (!(entity instanceof EntityDragon) && !(entity instanceof EntityWither)) {
         if (entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity creature = (BaseCreatureEntity)entity;
            return creature.isBoss();
         } else {
            CreatureGroup bossGroup = CreatureManager.getInstance().getCreatureGroup("boss");
            return bossGroup != null ? bossGroup.hasEntity(entity) : false;
         }
      } else {
         return true;
      }
   }
}
