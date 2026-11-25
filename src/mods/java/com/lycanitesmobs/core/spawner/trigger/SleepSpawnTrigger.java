package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SleepSpawnTrigger extends BlockSpawnTrigger {
   public SleepSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public boolean onSleep(World world, EntityPlayer player, BlockPos spawnPos, IBlockState blockState) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (this.chance < 1.0D && player.getRNG().nextDouble() > this.chance) {
         return false;
      } else if (!this.isTriggerBlock(blockState, world, spawnPos, 0)) {
         return false;
      } else {
         boolean success = this.trigger(world, player, spawnPos, 0, 0);
         if (this.cooldown > -1 && playerExt != null) {
            this.playerUsedTicks.put(player, Long.valueOf(playerExt.timePlayed));
         }

         return success;
      }
   }
}
