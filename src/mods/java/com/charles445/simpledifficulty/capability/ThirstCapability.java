package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import javax.vecmath.Vector3d;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ThirstCapability implements IThirstCapability {
   private float exhaustion = 0.0F;
   private int thirst = 20;
   private float saturation = 5.0F;
   private int ticktimer = 0;
   private int damagecounter = 0;
   private int oldthirst = 0;
   private float oldsaturation = 0.0F;
   Vector3d position = null;
   private int packetTimer = 0;

   public ThirstCapability() {
   }

   public void tickUpdate(EntityPlayer player, World world, Phase phase) {
      if (phase == Phase.START) {
         ++this.packetTimer;
      } else {
         if (this.position == null) {
            this.position = new Vector3d(player.posX, player.posY, player.posZ);
         }

         Vector3d newPosition = new Vector3d(player.posX, player.posY, player.posZ);
         this.position.sub(newPosition);
         this.position.absolute();
         int moveDistance = (int)Math.round(this.position.length() * 100.0D);
         this.position = newPosition;
         if (moveDistance > 1000) {
            moveDistance = 0;
         }

         if (moveDistance > 0) {
            float moveSensitivity = (float)ModConfig.server.thirst.thirstBaseMovement;
            if (!player.isInWater() && !player.isInsideOfMaterial(Material.WATER)) {
               if (player.onGround) {
                  if (player.isSprinting()) {
                     moveSensitivity = (float)ModConfig.server.thirst.thirstSprintingMovement;
                  } else {
                     moveSensitivity = (float)ModConfig.server.thirst.thirstWalkingMovement;
                  }
               }
            } else {
               moveSensitivity = (float)ModConfig.server.thirst.thirstSwimmingMovement;
            }

            this.addThirstExhaustion(moveSensitivity * 0.01F * (float)moveDistance);
         }

         if (this.getThirstExhaustion() > (float)ModConfig.server.thirst.thirstExhaustionLimit) {
            this.addThirstExhaustion(-1.0F * (float)ModConfig.server.thirst.thirstExhaustionLimit);
            if (this.getThirstSaturation() > 0.0F) {
               this.addThirstSaturation(-1.0F);
            } else if (DamageUtil.isModDangerous(world)) {
               this.addThirstLevel(-1);
            }
         }

         if (this.getThirstLevel() <= 0) {
            this.addThirstTickTimer(1);
            if (this.getThirstTickTimer() >= 80) {
               this.setThirstTickTimer(0);
               if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                  float thirstDamageToApply = 1.0F + 1.0F * (float)this.getThirstDamageCounter() * (float)ModConfig.server.thirst.thirstDamageScaling;
                  player.attackEntityFrom(SDDamageSources.DEHYDRATION, thirstDamageToApply);
                  this.addThirstDamageCounter(1);
               }
            }
         } else {
            this.setThirstTickTimer(0);
            this.setThirstDamageCounter(0);
         }

      }
   }

   private void checkSprint(EntityPlayer player) {
      if (player.isSprinting() && this.getThirstLevel() <= 6) {
         player.setSprinting(false);
      }

   }

   public boolean isDirty() {
      return this.thirst != this.oldthirst || this.saturation != this.oldsaturation;
   }

   public void setClean() {
      this.oldthirst = this.thirst;
      this.oldsaturation = this.saturation;
   }

   public float getThirstExhaustion() {
      return this.exhaustion;
   }

   public int getThirstLevel() {
      return this.thirst;
   }

   public float getThirstSaturation() {
      return this.saturation;
   }

   public int getThirstTickTimer() {
      return this.ticktimer;
   }

   public int getThirstDamageCounter() {
      return this.damagecounter;
   }

   public void setThirstExhaustion(float exhaustion) {
      this.exhaustion = Math.max(exhaustion, 0.0F);
      if (!Float.isFinite(this.exhaustion)) {
         this.exhaustion = 0.0F;
      }

   }

   public void setThirstLevel(int thirst) {
      this.thirst = MathHelper.clamp(thirst, 0, 20);
   }

   public void setThirstSaturation(float saturation) {
      this.saturation = MathHelper.clamp(saturation, 0.0F, 20.0F);
      if (!Float.isFinite(this.saturation)) {
         this.saturation = 0.0F;
      }

   }

   public void setThirstTickTimer(int ticktimer) {
      this.ticktimer = ticktimer;
   }

   public void setThirstDamageCounter(int damagecounter) {
      this.damagecounter = damagecounter;
   }

   public void addThirstExhaustion(float exhaustion) {
      this.setThirstExhaustion(this.getThirstExhaustion() + exhaustion);
   }

   public void addThirstLevel(int thirst) {
      this.setThirstLevel(this.getThirstLevel() + thirst);
   }

   public void addThirstSaturation(float saturation) {
      this.setThirstSaturation(this.getThirstSaturation() + saturation);
   }

   public void addThirstTickTimer(int ticktimer) {
      this.setThirstTickTimer(this.getThirstTickTimer() + ticktimer);
   }

   public void addThirstDamageCounter(int damagecounter) {
      this.setThirstDamageCounter(this.getThirstDamageCounter() + damagecounter);
   }

   public boolean isThirsty() {
      return this.getThirstLevel() < 20;
   }

   public int getPacketTimer() {
      return this.packetTimer;
   }
}
