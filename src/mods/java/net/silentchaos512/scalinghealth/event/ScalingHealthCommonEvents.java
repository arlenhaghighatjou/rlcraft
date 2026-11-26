package net.silentchaos512.scalinghealth.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.Calendar;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.init.ModItems;
import net.silentchaos512.scalinghealth.init.ModSounds;
import net.silentchaos512.scalinghealth.lib.module.ModuleAprilTricks;
import net.silentchaos512.scalinghealth.utils.ModifierHandler;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class ScalingHealthCommonEvents {
   public ScalingHealthCommonEvents() {
   }

   public void register(EventBus bus) {
      bus.register(LivingDropsEvent.class, this::onLivingDrops);
      bus.register(LivingExperienceDropEvent.class, EventPriority.HIGHEST, false, this::onXPDropped);
      bus.register(LivingDeathEvent.class, this::onPlayerDied);
      bus.register(PlayerRespawnEvent.class, this::onPlayerRespawn);
      bus.register(PlayerLoggedInEvent.class, this::onPlayerJoinedServer);
      bus.register(PlayerSleepInBedEvent.class, this::onPlayerSleepInBed);
      bus.register(PlayerWakeUpEvent.class, this::onPlayerWakeUp);
      bus.register(OnConfigChangedEvent.class, this::onConfigChanged);
   }

   public void onLivingDrops(LivingDropsEvent event) {
      EntityPlayer player = this.getPlayerThatCausedDeath(event.getSource());
      if (player != null && (!(player instanceof FakePlayer) || Config.FakePlayer.generateHearts)) {
         if (player.world.getGameRules().getBoolean("doMobLoot")) {
            EntityLivingBase killedEntity = event.getEntityLiving();
            if (!killedEntity.world.isRemote) {
               EaglercraftRandom rand = ScalingHealth.random;
               int stackSize = 0;
               float dropRate = killedEntity instanceof IMob ? Config.Items.Heart.chanceHostile : Config.Items.Heart.chancePassive;
               if (killedEntity instanceof EntitySlime) {
                  dropRate /= 6.0F;
               }

               ScalingHealth.logHelper.debug("heart drop rate for {} is {}", new Object[]{killedEntity.getName(), dropRate});
               if (event.isRecentlyHit() && rand.nextFloat() <= dropRate) {
                  ++stackSize;
               }

               if (!killedEntity.isNonBoss()) {
                  int min = Config.Items.Heart.bossMin;
                  int max = Config.Items.Heart.bossMax;
                  stackSize += min + rand.nextInt(max - min + 1);
               }

               if (stackSize > 0) {
                  Item itemToDrop = (Item)(Config.Items.Heart.dropShardsInstead ? ModItems.crystalShard : ModItems.heart);
                  killedEntity.dropItem(itemToDrop, stackSize);
               }
            }

         }
      }
   }

   public void onXPDropped(LivingExperienceDropEvent event) {
      EntityLivingBase entityLiving = event.getEntityLiving();
      short difficulty = entityLiving.getEntityData().getShort("scalinghealth:difficulty");
      float multi = 1.0F + Config.Mob.xpBoost * (float)difficulty;
      float amount = (float)event.getDroppedExperience();
      amount = amount * multi;
      if (BlightHandler.isBlight(entityLiving)) {
         amount *= Config.Mob.Blight.xpMultiplier;
      }

      event.setDroppedExperience(Math.round(amount));
   }

   @Nullable
   private EntityPlayer getPlayerThatCausedDeath(DamageSource source) {
      if (source == null) {
         return null;
      } else {
         Entity entitySource = source.getTrueSource();
         if (entitySource instanceof EntityPlayer) {
            return (EntityPlayer)entitySource;
         } else {
            if (entitySource instanceof EntityTameable && ((EntityTameable)entitySource).isTamed()) {
               boolean var5 = true;
            } else {
               boolean var10000 = false;
            }

            if (entitySource instanceof EntityTameable) {
               EntityTameable tamed = (EntityTameable)entitySource;
               if (tamed.isTamed() && tamed.getOwner() instanceof EntityPlayer) {
                  return (EntityPlayer)tamed.getOwner();
               }
            }

            return null;
         }
      }
   }

   public void onPlayerDied(LivingDeathEvent event) {
      if (event.getEntity() != null && event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if (ModuleAprilTricks.instance.isEnabled() && ModuleAprilTricks.instance.isRightDay()) {
            ScalingHealth.proxy.playSoundOnClient(player, ModSounds.PLAYER_DIED, 0.6F, 1.0F);
         }

      }
   }

   public void onPlayerRespawn(PlayerRespawnEvent event) {
      if (event.player instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)event.player;
         SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
         if (data == null) {
            return;
         }

         if (Config.Player.Health.lostOnDeath > 0 && !event.isEndConquered()) {
            float newHealth = data.getMaxHealth() - (float)Config.Player.Health.lostOnDeath;
            float minHealth = (float)Config.Player.Health.minHealth;
            data.setMaxHealth(newHealth < minHealth ? minHealth : newHealth);
         }

         if (!event.isEndConquered()) {
            double currentDifficulty = data.getDifficulty();
            double newDifficulty = MathHelper.clamp(currentDifficulty - (double)Config.Difficulty.lostOnDeath, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
            data.setDifficulty(newDifficulty);
         }

         if (Config.Player.Health.allowModify) {
            float health = player.getHealth();
            float maxHealth = data.getMaxHealth();
            ModifierHandler.setMaxHealth(player, (double)maxHealth, 0);
            if (health != maxHealth && maxHealth > 0.0F) {
               player.setHealth(player.getMaxHealth());
            }
         }
      }

   }

   public void onPlayerJoinedServer(PlayerLoggedInEvent event) {
      if (event.player instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)event.player;
         SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
         Calendar today = Calendar.getInstance();
         Calendar lastTimePlayed = data.getLastTimePlayed();
         if (Config.Difficulty.DIFFFICULTY_RESET_TIME.shouldReset(today, lastTimePlayed)) {
            ScalingHealth.logHelper.info("Reset player {}'s difficulty to {}", new Object[]{player.getName(), (int)Config.Difficulty.startValue});
            ChatHelper.sendMessage(player, "[Scaling Health] Your difficulty has been reset.");
            data.setDifficulty((double)Config.Difficulty.startValue);
         }

         if (Config.Player.Health.resetTime.shouldReset(today, lastTimePlayed)) {
            data.setMaxHealth((float)Config.Player.Health.startingHealth);
            ScalingHealth.logHelper.info("Reset player {}'s health to {}", new Object[]{player.getName(), Config.Player.Health.startingHealth});
            ChatHelper.sendMessage(player, "[Scaling Health] Your health has been reset.");
         }

         data.getLastTimePlayed().setTime(today.getTime());
         if (Config.Player.Health.allowModify) {
            float maxHealth = data.getMaxHealth();
            ModifierHandler.setMaxHealth(player, (double)maxHealth, 0);
         }
      }

      if (ModuleAprilTricks.instance.isEnabled() && ModuleAprilTricks.instance.isRightDay()) {
         ChatHelper.sendMessage(event.player, TextFormatting.RED + "[Scaling Health] It's April Fool's time... hehehe.");
      }

   }

   public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (!player.world.isRemote && Config.Client.Difficulty.warnWhenSleeping && Config.Difficulty.forSleeping != 0.0F) {
         String override = Config.Client.Difficulty.sleepMessageOverride;
         if (!override.isEmpty()) {
            player.sendMessage(new TextComponentString(override));
         } else {
            player.sendMessage(new TextComponentTranslation("misc.scalinghealth.sleepWarning", new Object[0]));
         }
      }

   }

   public void onPlayerWakeUp(PlayerWakeUpEvent event) {
      if (!event.getEntityPlayer().world.isRemote && !event.updateWorld()) {
         EntityPlayer player = event.getEntityPlayer();
         SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
         if (data != null) {
            data.incrementDifficulty((double)Config.Difficulty.forSleeping, false);
         }
      }

   }

   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("scalinghealth")) {
         Config.INSTANCE.load();
         Config.INSTANCE.save();
      }

   }
}
