package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class ProjectileBehaviourDrainHealth extends ProjectileBehaviour {
   public float rate = 0.5F;
   public float mountRate = 0.1F;

   public ProjectileBehaviourDrainHealth() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("rate")) {
         this.rate = json.get("rate").getAsFloat();
      }

      if (json.has("mountRate")) {
         this.mountRate = json.get("mountRate").getAsFloat();
      }

   }

   public void onProjectileDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, float damage) {
      if (projectile.getThrower() != null) {
         projectile.getThrower().heal(damage * this.rate);
         if (projectile.getThrower().getRidingEntity() instanceof EntityLiving) {
            ((EntityLiving)projectile.getThrower().getRidingEntity()).heal(damage * this.mountRate);
         }

      }
   }
}
