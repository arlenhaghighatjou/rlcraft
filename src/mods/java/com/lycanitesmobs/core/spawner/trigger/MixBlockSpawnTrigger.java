package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MixBlockSpawnTrigger extends BlockSpawnTrigger {
   public MixBlockSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
      return 0;
   }

   public void onMix(World world, IBlockState blockState, BlockPos mixPos) {
      if (this.isTriggerBlock(blockState, world, mixPos, 0)) {
         if (this.chance >= 1.0D || world.rand.nextDouble() <= this.chance) {
            this.trigger(world, (EntityPlayer)null, mixPos.up(), this.getBlockLevel(blockState, world, mixPos), 0);
         }
      }
   }
}
