package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class HealWhenNoPlayersGoal extends EntityAIBase {
   BaseCreatureEntity host;
   public List<EntityPlayer> playerTargets = new ArrayList();
   public boolean firstPlayerTargetCheck = false;
   private float healAmount = 50.0F;

   public HealWhenNoPlayersGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public HealWhenNoPlayersGoal setHealAmount(float healAmount) {
      this.healAmount = healAmount;
      return this;
   }

   public boolean shouldExecute() {
      return this.host.isEntityAlive();
   }

   public boolean shouldContinueExecuting() {
      return this.host.isEntityAlive();
   }

   public void startExecuting() {
   }

   public void resetTask() {
      this.firstPlayerTargetCheck = false;
   }

   public void updateTask() {
      if (this.host.updateTick % 200L == 0L && this.firstPlayerTargetCheck) {
         this.firstPlayerTargetCheck = true;
         this.playerTargets = this.host.<EntityPlayer>getNearbyEntities(EntityPlayer.class, (Predicate)null, 64.0D);
         if (this.host.updateTick % 20L == 0L && this.playerTargets.isEmpty()) {
            this.host.heal(this.healAmount);
         }

      }
   }
}
