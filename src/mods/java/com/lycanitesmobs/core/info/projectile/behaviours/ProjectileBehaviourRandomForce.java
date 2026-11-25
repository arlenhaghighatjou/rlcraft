package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;

public class ProjectileBehaviourRandomForce extends ProjectileBehaviour {
   public double force = 0.5D;

   public ProjectileBehaviourRandomForce() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("force")) {
         this.force = json.get("force").getAsDouble();
      }

   }

   public void onProjectileUpdate(BaseProjectileEntity projectile) {
      if (!projectile.getEntityWorld().isRemote) {
         if (projectile.updateTick % 5L == 0L) {
            projectile.addVelocity((0.5D - projectile.getEntityWorld().rand.nextDouble()) * this.force, (0.5D - projectile.getEntityWorld().rand.nextDouble()) * this.force, (0.5D - projectile.getEntityWorld().rand.nextDouble()) * this.force);
         }

      }
   }
}
