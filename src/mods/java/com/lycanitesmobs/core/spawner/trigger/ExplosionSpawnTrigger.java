package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ExplosionSpawnTrigger extends EntitySpawnTrigger {
   public int strength = 4;
   public boolean playerOnly = false;

   public ExplosionSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("strength")) {
         this.strength = json.get("strength").getAsInt();
      }

      if (json.has("playerOnly")) {
         this.playerOnly = json.get("playerOnly").getAsBoolean();
      }

      super.loadFromJSON(json);
   }

   public boolean onExplosion(World world, EntityPlayer player, Explosion explosion) {
      if (this.playerOnly && player == null) {
         return false;
      } else if (this.chance < 1.0D && world.rand.nextDouble() > this.chance) {
         return false;
      } else {
         return !this.playerOnly && explosion.getExplosivePlacedBy() != null && !this.isMatchingEntity(explosion.getExplosivePlacedBy()) ? false : this.trigger(world, player, new BlockPos(explosion.getPosition()), 0, 0);
      }
   }
}
