package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ProjectileBehaviour {
   public String type;

   public ProjectileBehaviour() {
   }

   public static ProjectileBehaviour createFromJSON(JsonObject json) {
      String type = json.get("type").getAsString();
      ProjectileBehaviour projectileBehaviour = null;
      if ("placeBlocks".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourPlaceBlocks();
      } else if ("summon".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourSummon();
      } else if ("explosion".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourExplosion();
      } else if ("fireProjectiles".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourFireProjectiles();
      } else if ("drainHealth".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourDrainHealth();
      } else if ("randomForce".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourRandomForce();
      } else if ("laser".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourLaser();
      } else if ("randomEffect".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourRandomEffect();
      } else if ("catch".equals(type)) {
         projectileBehaviour = new ProjectileBehaviourCatch();
      }

      if (projectileBehaviour == null) {
         return null;
      } else {
         projectileBehaviour.type = type;
         projectileBehaviour.loadFromJSON(json);
         return projectileBehaviour;
      }
   }

   public abstract void loadFromJSON(JsonObject var1);

   public void onProjectileUpdate(BaseProjectileEntity projectile) {
   }

   public boolean canDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, boolean canDamage) {
      return true;
   }

   public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
   }

   public void onProjectileDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, float damage) {
   }
}
