package ladysnake.spawnercontrol.controlledspawner;

import ladysnake.spawnercontrol.config.SpawnerConfig;

public interface IControllableSpawner {
   void setSpawnedMobsCount(int var1);

   boolean incrementSpawnedMobsCount();

   int getSpawnedMobsCount();

   boolean canSpawn();

   SpawnerConfig getConfig();
}
