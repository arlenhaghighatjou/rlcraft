package crafttweaker.api.world;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.world.IWorldProvider")
@ZenRegister
public interface IWorldProvider {
   @ZenMethod
   @ZenGetter("actualHeight")
   int getActualHeight();

   @ZenGetter("actualGroundLevel")
   @ZenMethod
   int getAverageGroundLevel();

   @ZenMethod
   IBiome getBiomeForCoords(IBlockPos var1);

   @ZenGetter("cloudHeight")
   @ZenMethod
   float getCloudHeight();

   @ZenGetter("currentMoonPhaseFactor")
   @ZenMethod
   float getCurrentMoonPhaseFactor();

   @ZenGetter("dimensionID")
   @ZenMethod
   int getDimensionID();

   @ZenGetter("height")
   @ZenMethod
   int getHeight();

   @ZenGetter("horizon")
   @ZenMethod
   double getHorizon();

   @ZenGetter("lightBrightnesTable")
   @ZenMethod
   float[] getLightBrightnesTable();

   @ZenMethod
   int getMoonPhase(long var1);

   @ZenGetter("movementFactor")
   @ZenMethod
   double getMovementFactor();

   @ZenGetter("randomizedSpawnPoint")
   @ZenMethod
   IBlockPos getRandomizedSpawnPoint();

   @ZenMethod
   IWorldProvider getRespawnDimension(IPlayer var1);

   @ZenGetter("saveFolder")
   @ZenMethod
   String getSaveFolder();

   @ZenGetter("seed")
   @ZenMethod
   long getSeed();

   @ZenGetter("spawnCoordinate")
   @ZenMethod
   IBlockPos getSpawnCoordinate();

   @ZenGetter("spawnPoint")
   @ZenMethod
   IBlockPos getSpawnPoint();

   @ZenMethod
   float getStarBrightness(float var1);

   @ZenMethod
   float getSunBrightness(float var1);

   @ZenMethod
   float getSunBrightnessFactor(float var1);

   @ZenGetter("voidFogYFactor")
   @ZenMethod
   double getVoidFogYFactor();

   @ZenGetter("worldTime")
   @ZenMethod
   long getWorldTime();

   @ZenGetter("canRespawnHere")
   @ZenMethod
   boolean canRespawnHere();

   @ZenGetter("waterVaporize")
   @ZenMethod
   boolean doesWaterVaporize();

   @ZenGetter("skylight")
   @ZenMethod
   boolean hasSkyLight();

   @ZenMethod
   boolean isBlockHighHumidity(IBlockPos var1);

   @ZenGetter("daytime")
   @ZenMethod
   boolean isDaytime();

   @ZenGetter("nether")
   @ZenMethod
   boolean isNether();

   @ZenGetter("skyColored")
   @ZenMethod
   boolean isSkyColored();

   @ZenGetter("surfaceWorld")
   @ZenMethod
   boolean isSurfaceWorld();
}
