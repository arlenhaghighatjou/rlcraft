package shadows.placebo.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerEditor {
   public static final int SPAWN_DELAY = 20;
   public static final int MIN_SPAWN_DELAY = 200;
   public static final int MAX_SPAWN_DELAY = 800;
   public static final int SPAWN_COUNT = 4;
   public static final int MAX_NEARBY_ENTITIES = 6;
   public static final int PLAYER_RANGE = 16;
   public static final int SPAWN_RANGE = 4;
   protected TileEntityMobSpawner spawner;

   public SpawnerEditor(World world, BlockPos pos) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileEntityMobSpawner) {
         this.spawner = (TileEntityMobSpawner)te;
      } else {
         world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState());
         te = world.getTileEntity(pos);
         this.spawner = (TileEntityMobSpawner)te;
      }

   }

   public SpawnerEditor setType(Class<? extends Entity> entity) {
      this.spawner.getSpawnerBaseLogic().setEntityId(EntityList.getKey(entity));
      return this;
   }

   public SpawnerEditor setDelay(int delay) {
      this.spawner.getSpawnerBaseLogic().spawnDelay = delay;
      return this;
   }

   public SpawnerEditor setMinDelay(int min) {
      this.spawner.getSpawnerBaseLogic().minSpawnDelay = min;
      return this;
   }

   public SpawnerEditor setMaxDelay(int max) {
      this.spawner.getSpawnerBaseLogic().maxSpawnDelay = max;
      return this;
   }

   public SpawnerEditor setMinAndMaxDelay(int min, int max) {
      this.setMinDelay(min);
      this.setMaxDelay(max);
      return this;
   }

   public SpawnerEditor setSpawnCount(int count) {
      this.spawner.getSpawnerBaseLogic().spawnCount = count;
      return this;
   }

   public SpawnerEditor setMaxNearbyEntities(int max) {
      this.spawner.getSpawnerBaseLogic().maxNearbyEntities = max;
      return this;
   }

   public SpawnerEditor setPlayerRange(int range) {
      this.spawner.getSpawnerBaseLogic().activatingRangeFromPlayer = range;
      return this;
   }

   public SpawnerEditor setSpawnRange(int range) {
      this.spawner.getSpawnerBaseLogic().spawnRange = range;
      return this;
   }

   public SpawnerEditor setSpawnData(int weight, @Nullable NBTTagCompound data) {
      if (data == null) {
         this.spawner.getSpawnerBaseLogic().spawnData = new WeightedSpawnerEntity();
      } else {
         this.spawner.getSpawnerBaseLogic().spawnData = new WeightedSpawnerEntity(weight, data);
      }

      return this;
   }

   public SpawnerEditor setPotentials(WeightedSpawnerEntity... entries) {
      this.spawner.getSpawnerBaseLogic().potentialSpawns.clear();

      for(WeightedSpawnerEntity e : entries) {
         this.spawner.getSpawnerBaseLogic().potentialSpawns.add(e);
      }

      return this;
   }
}
