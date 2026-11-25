package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProjectileBehaviourExplosion extends ProjectileBehaviour {
   public int radius = 2;
   public String mode = "none";

   public ProjectileBehaviourExplosion() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("radius")) {
         this.radius = json.get("radius").getAsInt();
      }

      if (json.has("mode")) {
         this.mode = json.get("mode").getAsString();
      }

   }

   public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
      if (this.radius > 0 && !projectile.getEntityWorld().isRemote) {
         if (world.getGameRules().getBoolean("mobGriefing")) {
            int explosionRadius = this.radius;
            if (projectile.getThrower() != null && projectile.getThrower() instanceof BaseCreatureEntity) {
               BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)projectile.getThrower();
               if (baseCreatureEntity.isRareVariant()) {
                  explosionRadius += 2;
               }
            }

            boolean breakBlocks = false;
            boolean destroyBlocks = false;
            if ("break".equalsIgnoreCase(this.mode)) {
               breakBlocks = true;
            } else if ("destroy".equalsIgnoreCase(this.mode)) {
               breakBlocks = true;
               destroyBlocks = true;
            }

            world.newExplosion(projectile, projectile.getPositionVector().x, projectile.getPositionVector().y, projectile.getPositionVector().z, (float)explosionRadius, destroyBlocks, breakBlocks);
         }
      }
   }
}
