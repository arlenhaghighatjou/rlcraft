package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FishingSpawnTrigger extends SpawnTrigger {
   protected Entity hookEntity;

   public FishingSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public void onFished(World world, EntityPlayer player, Entity hookEntity) {
      if (this.chance >= 1.0D || player.getRNG().nextDouble() <= this.chance) {
         BlockPos spawnPos = player.getPosition().add(0, 0, 1);
         this.hookEntity = hookEntity;
         if (this.hookEntity != null) {
            spawnPos = hookEntity.getPosition();
         }

         this.trigger(world, player, spawnPos, 0, 0);
      }
   }

   public void applyToEntity(EntityLiving entityLiving) {
      super.applyToEntity(entityLiving);
      if (this.hookEntity != null) {
         entityLiving.motionX = this.hookEntity.motionX;
         entityLiving.motionY = this.hookEntity.motionY;
         entityLiving.motionZ = this.hookEntity.motionZ;
      }

   }
}
