package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityPCrude extends EntityParasiteBase {
   public EntityPCrude(World worldIn) {
      super(worldIn);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, !SRPConfigSystems.useOneMind, false, (Predicate)null, 1.0D, 1.0F));
   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 60.0F) {
         super.fall(distance, damageMultiplier);
      }

   }
}
