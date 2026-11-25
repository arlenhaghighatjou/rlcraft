package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ChunkSpawnTrigger extends SpawnTrigger {
   public ChunkSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public boolean onChunkPopulate(World world, ChunkPos chunkPos) {
      return this.chance < 1.0D && world.rand.nextDouble() > this.chance ? false : this.trigger(world, (EntityPlayer)null, new BlockPos(chunkPos.getXStart() + 8, world.getSeaLevel(), chunkPos.getZStart() + 8), 0, 0);
   }
}
