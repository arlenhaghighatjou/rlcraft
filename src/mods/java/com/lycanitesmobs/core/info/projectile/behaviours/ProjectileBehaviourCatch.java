package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ProjectileBehaviourCatch extends ProjectileBehaviour {
   public List<String> catchEntityIds = new ArrayList();
   public boolean resetAttackCooldown = true;
   public boolean preventDamage = true;

   public ProjectileBehaviourCatch() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("catchEntityIds")) {
         this.catchEntityIds = JSONHelper.getJsonStrings(json.get("catchEntityIds").getAsJsonArray());
      }

      if (json.has("resetAttackCooldown")) {
         this.resetAttackCooldown = json.get("resetAttackCooldown").getAsBoolean();
      }

      if (json.has("preventDamage")) {
         this.preventDamage = json.get("preventDamage").getAsBoolean();
      }

   }

   public boolean canDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, boolean canDamage) {
      if (!projectile.getEntityWorld().isRemote && target != projectile.getThrower()) {
         ResourceLocation entityResourceLocation = EntityList.getKey(target);
         if (entityResourceLocation == null) {
            return canDamage;
         } else {
            String entityId = entityResourceLocation.toString();
            if (!this.catchEntityIds.contains(entityId)) {
               return canDamage;
            } else {
               if (this.resetAttackCooldown && target instanceof BaseCreatureEntity) {
                  ((BaseCreatureEntity)target).resetAttackCooldown();
               }

               projectile.setDead();
               return !this.preventDamage;
            }
         }
      } else {
         return canDamage;
      }
   }
}
