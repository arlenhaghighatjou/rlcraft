package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.WorldUtil;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TemperatureCapability implements ITemperatureCapability {
   private int temperature = 12;
   private int ticktimer = 0;
   private int damagecounter = 0;
   private Map<String, TemporaryModifier> temporaryModifiers = new HashMap();
   private int oldtemperature = 0;
   private int updatetimer = 500;
   private int targettemp = 0;
   private int debugtimer = 0;
   private boolean manualDirty = false;
   private int oldmodifiersize = 0;
   private int packetTimer = 0;

   public TemperatureCapability() {
   }

   public int getTemperatureLevel() {
      return this.temperature;
   }

   public int getTemperatureTickTimer() {
      return this.ticktimer;
   }

   public int getTemperatureDamageCounter() {
      return this.damagecounter;
   }

   public void setTemperatureLevel(int temperature) {
      this.temperature = TemperatureUtil.clampTemperature(temperature);
   }

   public void setTemperatureTickTimer(int ticktimer) {
      this.ticktimer = ticktimer;
   }

   public void setTemperatureDamageCounter(int damagecounter) {
      this.damagecounter = damagecounter;
   }

   public void addTemperatureLevel(int temperature) {
      this.setTemperatureLevel(this.getTemperatureLevel() + temperature);
   }

   public void addTemperatureTickTimer(int ticktimer) {
      this.setTemperatureTickTimer(this.getTemperatureTickTimer() + ticktimer);
   }

   public void addTemperatureDamageCounter(int damagecounter) {
      this.setTemperatureDamageCounter(this.getTemperatureDamageCounter() + damagecounter);
   }

   public void tickUpdate(EntityPlayer player, World world, Phase phase) {
      if (phase == Phase.START) {
         ++this.packetTimer;
      } else {
         ++this.debugtimer;
         if (this.debugtimer >= 40 && ServerConfig.instance.getBoolean(ServerOptions.DEBUG)) {
            this.debugtimer = 0;
            this.debugRoutine(player, world);
         }

         ++this.updatetimer;
         if (this.updatetimer >= 5) {
            this.updatetimer = 0;
            this.targettemp = TemperatureUtil.getPlayerTargetTemperature(player);
         }

         this.addTemperatureTickTimer(1);
         boolean appliedEffect = false;
         if (this.getTemperatureTickTimer() >= this.getTemperatureTickLimit()) {
            this.setTemperatureTickTimer(0);
            int destinationTemp = TemperatureUtil.clampTemperature(this.targettemp);
            if (this.getTemperatureLevel() != destinationTemp) {
               if (this.getTemperatureLevel() > destinationTemp) {
                  this.addTemperatureLevel(-1);
               } else {
                  this.addTemperatureLevel(1);
               }
            }

            TemperatureEnum tempEnum = this.getTemperatureEnum();
            if (tempEnum == TemperatureEnum.BURNING) {
               if (TemperatureEnum.BURNING.getMiddle() < this.getTemperatureLevel() && !player.isPotionActive(SDPotions.heat_resist) && !player.isSpectator() && !player.isCreative()) {
                  player.removePotionEffect(SDPotions.hyperthermia);
                  player.addPotionEffect(new PotionEffect(SDPotions.hyperthermia, ModConfig.server.temperature.temperatureDamageDuration, 0));
                  appliedEffect = true;
               }
            } else if (tempEnum == TemperatureEnum.FREEZING && TemperatureEnum.FREEZING.getMiddle() >= this.getTemperatureLevel() && !player.isPotionActive(SDPotions.cold_resist) && !player.isSpectator() && !player.isCreative()) {
               player.removePotionEffect(SDPotions.hypothermia);
               player.addPotionEffect(new PotionEffect(SDPotions.hypothermia, ModConfig.server.temperature.temperatureDamageDuration, 0));
               appliedEffect = true;
            }
         }

         if (!appliedEffect && this.getTemperatureDamageCounter() != 0) {
            boolean hasHypothermia = player.isPotionActive(SDPotions.hypothermia);
            boolean hasHyperthermia = player.isPotionActive(SDPotions.hyperthermia);
            if (!hasHypothermia && !hasHyperthermia) {
               this.setTemperatureDamageCounter(0);
            }
         }

         Map<String, TemporaryModifier> tweaks = new HashMap();
         int modifierSize = this.temporaryModifiers.size();

         for(Entry<String, TemporaryModifier> entry : this.temporaryModifiers.entrySet()) {
            TemporaryModifier tm = (TemporaryModifier)entry.getValue();
            if (tm.duration > 0) {
               tweaks.put(entry.getKey(), new TemporaryModifier(tm.temperature, tm.duration - 1));
            }
         }

         this.temporaryModifiers.clear();
         this.temporaryModifiers.putAll(tweaks);
         tweaks.clear();
         if (this.oldmodifiersize != this.temporaryModifiers.size()) {
            this.manualDirty = true;
         }

         this.oldmodifiersize = this.temporaryModifiers.size();
      }
   }

   private void debugRoutine(EntityPlayer player, World world) {
      DebugUtil.clientMessage(player, "----------------");
      BlockPos pos = WorldUtil.getSidedBlockPos(world, player);
      float cumulative = 0.0F;

      for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
         float modsum = 0.0F;
         long nanotime = System.nanoTime();
         modsum = modsum + modifier.getWorldInfluence(world, pos);
         modsum = modsum + modifier.getPlayerInfluence(player);
         long nanotime2 = System.nanoTime();
         DebugUtil.clientMessage(player, "" + (nanotime2 - nanotime) + " : " + modifier.getName() + " - " + modsum);
         cumulative += modsum;
      }

      for(ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values()) {
         float oldcumulative = cumulative;
         long nanotime = System.nanoTime();
         float var12 = dynmodifier.applyDynamicWorldInfluence(world, pos, cumulative);
         cumulative = dynmodifier.applyDynamicPlayerInfluence(player, var12);
         long nanotime2 = System.nanoTime();
         DebugUtil.clientMessage(player, "" + (nanotime2 - nanotime) + " : " + dynmodifier.getName() + " - " + (cumulative - oldcumulative));
      }

      DebugUtil.clientMessage(player, "( " + TemperatureUtil.getPlayerTargetTemperature(player) + " )");
      DebugUtil.clientMessage(player, "TempTickLimit: " + this.getTemperatureTickLimit());
   }

   private int getTemperatureTickLimit() {
      int tickrange = ModConfig.server.temperature.temperatureTickMax - ModConfig.server.temperature.temperatureTickMin;
      int temprange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound();
      int currentrange = Math.abs(this.getTemperatureLevel() - this.targettemp);
      boolean escapingDanger = this.getTemperatureLevel() <= this.targettemp ? this.getTemperatureEnum() == TemperatureEnum.FREEZING : this.getTemperatureEnum() == TemperatureEnum.BURNING;
      return Math.max(ModConfig.server.temperature.temperatureTickMin, ModConfig.server.temperature.temperatureTickMax - currentrange * tickrange / temprange - (escapingDanger ? ModConfig.server.temperature.temperatureTickDangerBoost : 0));
   }

   public boolean isDirty() {
      return this.manualDirty || this.temperature != this.oldtemperature;
   }

   public void setClean() {
      this.oldtemperature = this.temperature;
      this.manualDirty = false;
   }

   public TemperatureEnum getTemperatureEnum() {
      return TemperatureUtil.getTemperatureEnum(this.getTemperatureLevel());
   }

   public ImmutableMap<String, TemporaryModifier> getTemporaryModifiers() {
      return ImmutableMap.copyOf(this.temporaryModifiers);
   }

   public void setTemporaryModifier(String name, float temp, int duration) {
      if (temp != 0.0F && Float.isFinite(temp)) {
         if (this.temporaryModifiers.containsKey(name)) {
            this.manualDirty = true;
         }

         this.temporaryModifiers.put(name, new TemporaryModifier(temp, duration));
      }
   }

   public void clearTemporaryModifiers() {
      this.temporaryModifiers.clear();
   }

   public int getPacketTimer() {
      return this.packetTimer;
   }
}
