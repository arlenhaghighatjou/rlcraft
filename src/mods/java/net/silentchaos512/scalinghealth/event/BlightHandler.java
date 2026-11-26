package net.silentchaos512.scalinghealth.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.entity.EntityBlightFire;
import net.silentchaos512.scalinghealth.init.ModItems;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageMarkBlight;

public final class BlightHandler {
   public static final BlightHandler INSTANCE = new BlightHandler();
   private static final String NBT_BLIGHT = "ScalingHealth.IsBlight";
   private static final int UPDATE_DELAY = 200;

   private BlightHandler() {
   }

   public static boolean isBlight(EntityLivingBase entityLiving) {
      return entityLiving != null && entityLiving.getEntityData().getBoolean("ScalingHealth.IsBlight");
   }

   /** @deprecated */
   @Deprecated
   public static void markBlight(EntityLivingBase entityLiving) {
      markBlight(entityLiving, true);
   }

   public static void markBlight(EntityLivingBase entityLiving, boolean isBlight) {
      if (entityLiving != null) {
         entityLiving.getEntityData().setBoolean("ScalingHealth.IsBlight", isBlight);
         if (!isBlight) {
            EntityBlightFire blightFire = getBlightFire(entityLiving);
            if (blightFire != null) {
               blightFire.setDead();
            }
         }
      }

   }

   static void spawnBlightFire(EntityLivingBase blight) {
      if (!blight.world.isRemote && getBlightFire(blight) == null) {
         EntityBlightFire fire = new EntityBlightFire(blight);
         fire.setPosition(blight.posX, blight.posY, blight.posZ);
         blight.world.spawnEntity(fire);
         if (Config.Mob.Blight.fireRidesBlight) {
            fire.startRiding(blight);
         }

      }
   }

   @Nullable
   private static EntityBlightFire getBlightFire(EntityLivingBase blight) {
      for(EntityBlightFire fire : blight.world.getEntities(EntityBlightFire.class, (e) -> {
         return true;
      })) {
         if (blight.equals(fire.getParent())) {
            return fire;
         }
      }

      return null;
   }

   static void applyBlightPotionEffects(EntityLivingBase entityLiving) {
      int duration = Config.Mob.Blight.potionDuration;
      if (duration < 0) {
         duration = Integer.MAX_VALUE;
      } else if (duration == 0) {
         return;
      }

      if (Config.Mob.Blight.invisibility) {
         entityLiving.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, duration, 0, true, false));
      }

      if (Config.Mob.Blight.fireResist) {
         entityLiving.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, duration, 0, true, false));
      }

      if (Config.Mob.Blight.speedAmp > -1) {
         entityLiving.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, Config.Mob.Blight.speedAmp, true, false));
      }

      if (Config.Mob.Blight.strengthAmp > -1) {
         entityLiving.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, Config.Mob.Blight.strengthAmp, true, false));
      }

   }

   public void register(EventBus bus) {
      bus.register(LivingDeathEvent.class, EventPriority.LOWEST, false, this::onBlightKilled);
      bus.register(LivingUpdateEvent.class, this::onBlightUpdate);
      bus.register(LivingAttackEvent.class, this::onBlightAttack);
   }

   public void onBlightKilled(LivingDeathEvent event) {
      if (event.getSource() != null && isBlight(event.getEntityLiving()) && !event.getEntity().world.isRemote) {
         Entity entitySource = event.getSource().getTrueSource();
         boolean isTamedAnimal = entitySource instanceof EntityTameable && ((EntityTameable)entitySource).isTamed();
         if (!(entitySource instanceof EntityPlayer) && !isTamedAnimal) {
            EntityLivingBase blight = event.getEntityLiving();
            if (Config.Mob.Blight.notifyOnDeath) {
               ITextComponent deathMessage = event.getSource().getDeathMessage(blight);
               if (deathMessage instanceof TextComponentTranslation) {
                  TextComponentTranslation original = (TextComponentTranslation)deathMessage;
                  TextComponentTranslation newMessage = new TextComponentTranslation(original.getKey(), new Object[]{"Blight " + blight.getName()});
                  ScalingHealth.logHelper.info("Blight {} has died", new Object[]{blight.getName()});

                  for(EntityPlayer p : blight.world.getPlayers(EntityPlayer.class, (e) -> {
                     return true;
                  })) {
                     ChatHelper.sendMessage(p, newMessage);
                  }
               }
            }
         } else {
            EntityLivingBase blight = event.getEntityLiving();
            EntityPlayer player;
            EntityLivingBase actualKiller;
            if (isTamedAnimal) {
               player = (EntityPlayer)((EntityTameable)entitySource).getOwner();
               actualKiller = (EntityLivingBase)entitySource;
            } else {
               actualKiller = player = (EntityPlayer)entitySource;
            }

            if (Config.Mob.Blight.notifyOnDeath && player != null) {
               ScalingHealth.logHelper.info("Blight {} was killed by {}", new Object[]{blight.getName(), actualKiller.getName()});

               for(EntityPlayer p : player.world.getPlayers(EntityPlayer.class, (e) -> {
                  return true;
               })) {
                  ChatHelper.translate(p, ScalingHealth.i18n.getKey("blight", "killedByPlayer"), new Object[]{blight.getName(), actualKiller.getName()});
               }
            }

            boolean canGetHearts = !(player instanceof FakePlayer) || Config.FakePlayer.generateHearts;
            int min = Config.Items.Heart.blightMin;
            int max = Config.Items.Heart.blightMax;
            int heartCount = ScalingHealth.random.nextInt(max - min + 1) + min;
            if (canGetHearts && heartCount > 0) {
               Item itemToDrop = (Item)(Config.Items.Heart.dropShardsInstead ? ModItems.crystalShard : ModItems.heart);
               blight.dropItem(itemToDrop, heartCount);
            }
         }

      }
   }

   public void onBlightUpdate(LivingUpdateEvent event) {
      EntityLivingBase entityLiving = event.getEntityLiving();
      if (entityLiving != null && !entityLiving.world.isRemote && isBlight(entityLiving)) {
         World world = entityLiving.world;
         if ((world.getTotalWorldTime() + (long)entityLiving.getEntityId()) % 200L == 0L) {
            MessageMarkBlight message = new MessageMarkBlight(entityLiving, true);
            NetworkHandler.INSTANCE.sendToAllAround(message, new TargetPoint(entityLiving.dimension, entityLiving.posX, entityLiving.posY, entityLiving.posZ, 128.0D));
            if (getBlightFire(entityLiving) == null) {
               spawnBlightFire(entityLiving);
            }

            applyBlightPotionEffects(entityLiving);
         }
      }

   }

   public void onBlightAttack(LivingAttackEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null && !entity.world.isRemote && isBlight(entity) && Config.Mob.Blight.immuneToSuffocation && event.getSource() == DamageSource.IN_WALL) {
         event.setCanceled(true);
      }

   }
}
