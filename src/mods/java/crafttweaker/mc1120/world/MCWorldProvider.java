package crafttweaker.mc1120.world;

import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorldProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class MCWorldProvider implements IWorldProvider {
   private final WorldProvider provider;

   public MCWorldProvider(int id) {
      this.provider = DimensionManager.getProvider(id);
   }

   public MCWorldProvider(WorldProvider provider) {
      this.provider = provider;
   }

   public int getActualHeight() {
      return this.provider.getActualHeight();
   }

   public int getAverageGroundLevel() {
      return this.provider.getAverageGroundLevel();
   }

   public IBiome getBiomeForCoords(IBlockPos pos) {
      return new MCBiome(this.provider.getBiomeForCoords((BlockPos)pos.getInternal()));
   }

   public float getCloudHeight() {
      return this.provider.getCloudHeight();
   }

   public float getCurrentMoonPhaseFactor() {
      return this.provider.getCurrentMoonPhaseFactor();
   }

   public int getDimensionID() {
      return this.provider.getDimension();
   }

   public int getHeight() {
      return this.provider.getHeight();
   }

   public double getHorizon() {
      return this.provider.getHorizon();
   }

   public float[] getLightBrightnesTable() {
      return this.provider.getLightBrightnessTable();
   }

   public int getMoonPhase(long time) {
      return this.provider.getMoonPhase(time);
   }

   public double getMovementFactor() {
      return this.provider.getMovementFactor();
   }

   public IBlockPos getRandomizedSpawnPoint() {
      return new MCBlockPos(this.provider.getRandomizedSpawnPoint());
   }

   public IWorldProvider getRespawnDimension(IPlayer player) {
      return !(player.getInternal() instanceof EntityPlayerMP) ? null : new MCWorldProvider(this.provider.getRespawnDimension((EntityPlayerMP)player.getInternal()));
   }

   public String getSaveFolder() {
      return this.provider.getSaveFolder();
   }

   public long getSeed() {
      return this.provider.getSeed();
   }

   public IBlockPos getSpawnCoordinate() {
      return new MCBlockPos(this.provider.getSpawnCoordinate());
   }

   public IBlockPos getSpawnPoint() {
      return new MCBlockPos(this.provider.getSpawnPoint());
   }

   public float getStarBrightness(float something) {
      return this.provider.getStarBrightness(something);
   }

   public float getSunBrightness(float something) {
      return this.provider.getSunBrightness(something);
   }

   public float getSunBrightnessFactor(float something) {
      return this.provider.getSunBrightnessFactor(something);
   }

   public double getVoidFogYFactor() {
      return this.provider.getVoidFogYFactor();
   }

   public long getWorldTime() {
      return this.provider.getWorldTime();
   }

   public boolean canRespawnHere() {
      return this.provider.canRespawnHere();
   }

   public boolean doesWaterVaporize() {
      return this.provider.doesWaterVaporize();
   }

   public boolean hasSkyLight() {
      return this.provider.hasSkyLight();
   }

   public boolean isBlockHighHumidity(IBlockPos pos) {
      return this.provider.isBlockHighHumidity((BlockPos)pos.getInternal());
   }

   public boolean isDaytime() {
      return this.provider.isDaytime();
   }

   public boolean isNether() {
      return this.provider.isNether();
   }

   public boolean isSkyColored() {
      return this.provider.isSkyColored();
   }

   public boolean isSurfaceWorld() {
      return this.provider.isSurfaceWorld();
   }
}
