package com.lycanitesmobs.core.entity.goals.actions;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class FindNearbyPlayersGoal extends EntityAIBase {
   BaseCreatureEntity host;
   private double searchRange = 64.0D;
   private int searchTime = 0;
   private int searchRate = 20;

   public FindNearbyPlayersGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public FindNearbyPlayersGoal setSearchRange(double searchRange) {
      this.searchRange = searchRange;
      return this;
   }

   public boolean shouldExecute() {
      return this.host.isEntityAlive();
   }

   public void updateTask() {
      if (this.searchTime++ % this.searchRate == 0) {
         EntityLivingBase newTarget = null;

         try {
            this.host.playerTargets = this.host.getEntityWorld().getPlayers(EntityPlayer.class, (input) -> {
               return (double)input.getDistance(this.host) <= this.searchRange;
            });
         } catch (Exception var3) {
            LycanitesMobs.logWarning("", "An exception occurred when player target selecting, this has been skipped to prevent a crash.");
            var3.printStackTrace();
         }

      }
   }
}
