package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.BaseGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class SummonMinionsGoal extends BaseGoal {
   protected int summonTime = 0;
   protected int summonRate = 60;
   protected int summonCap = 5;
   protected CreatureInfo minionInfo;
   protected boolean perPlayer = false;
   protected boolean antiFlight = false;
   protected double sizeScale = 1.0D;

   public SummonMinionsGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public SummonMinionsGoal setSummonRate(int summonRate) {
      this.summonRate = summonRate;
      return this;
   }

   public SummonMinionsGoal setSummonCap(int summonCap) {
      this.summonCap = summonCap;
      return this;
   }

   public SummonMinionsGoal setPerPlayer(boolean perPlayer) {
      this.perPlayer = perPlayer;
      return this;
   }

   public SummonMinionsGoal setAntiFlight(boolean antiFlight) {
      this.antiFlight = antiFlight;
      return this;
   }

   public SummonMinionsGoal setMinionInfo(String creatureName) {
      this.minionInfo = CreatureManager.getInstance().getCreature(creatureName);
      return this;
   }

   public SummonMinionsGoal setSizeScale(double sizeScale) {
      this.sizeScale = sizeScale;
      return this;
   }

   public boolean shouldExecute() {
      if (this.host.isPetType("familiar")) {
         return false;
      } else {
         return super.shouldExecute() && this.minionInfo != null;
      }
   }

   public void startExecuting() {
      this.summonTime = 1;
   }

   public void updateTask() {
      if (this.summonTime++ % this.summonRate == 0) {
         if (this.host.getMinions(this.minionInfo.getEntityClass()).size() < this.summonCap) {
            if (this.antiFlight) {
               for(EntityPlayer target : this.host.playerTargets) {
                  if (!target.isCreative() && !target.isSpectator() && CreatureManager.getInstance().config.bossAntiFlight > 0.0D && target.posY > this.host.posY + CreatureManager.getInstance().config.bossAntiFlight + 1.0D) {
                     this.summonMinion(target);
                  }
               }

            } else {
               this.summonMinion(this.host.getAttackTarget());
            }
         }
      }
   }

   protected void summonMinion(EntityLivingBase target) {
      EntityLivingBase minion = this.minionInfo.createEntity(this.host.getEntityWorld());
      this.host.summonMinion(minion, this.host.getRNG().nextDouble() * 360.0D, (double)(this.host.width + 1.0F));
      if (minion instanceof BaseCreatureEntity) {
         BaseCreatureEntity minionCreature = (BaseCreatureEntity)minion;
         minionCreature.setAttackTarget(target);
         minionCreature.setSizeScale(minionCreature.sizeScale * this.sizeScale);
      }

   }
}
