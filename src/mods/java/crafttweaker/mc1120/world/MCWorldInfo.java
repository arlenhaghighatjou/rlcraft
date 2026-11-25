package crafttweaker.mc1120.world;

import crafttweaker.api.world.IWorldInfo;
import net.minecraft.world.storage.WorldInfo;

public class MCWorldInfo implements IWorldInfo {
   private final WorldInfo info;

   public MCWorldInfo(WorldInfo info) {
      this.info = info;
   }

   public boolean isCommandsAllowed() {
      return this.info.areCommandsAllowed();
   }

   public double getBorderCenterX() {
      return this.info.getBorderCenterX();
   }

   public double getBorderCenterZ() {
      return this.info.getBorderCenterZ();
   }

   public double getBorderDamagePerBlock() {
      return this.info.getBorderDamagePerBlock();
   }

   public double getBoderLerpTarget() {
      return this.info.getBorderLerpTarget();
   }

   public long getBoderLerpTime() {
      return this.info.getBorderLerpTime();
   }

   public double getBorderSafeZone() {
      return this.info.getBorderSafeZone();
   }

   public double getBorderSize() {
      return this.info.getBorderSize();
   }

   public int getBorderWarningDistance() {
      return this.info.getBorderWarningDistance();
   }

   public int getBorderWarningTime() {
      return this.info.getBorderWarningTime();
   }

   public double getCleanWeatherTime() {
      return (double)this.info.getCleanWeatherTime();
   }

   public String getDifficulty() {
      return this.info.getDifficulty().toString();
   }

   public String getGeneratorOptions() {
      return this.info.getGeneratorOptions();
   }

   public long getLastTimePlayed() {
      return this.info.getLastTimePlayed();
   }

   public int getRainTime() {
      return this.info.getRainTime();
   }

   public int getSaveVersion() {
      return this.info.getSaveVersion();
   }

   public long getSeed() {
      return this.info.getSeed();
   }

   public int getSpawnX() {
      return this.info.getSpawnX();
   }

   public int getSpawnY() {
      return this.info.getSpawnY();
   }

   public int getSpawnZ() {
      return this.info.getSpawnZ();
   }

   public int getThunderTime() {
      return this.info.getThunderTime();
   }

   public int getVersionId() {
      return this.info.getVersionId();
   }

   public String getVersionName() {
      return this.info.getVersionName();
   }

   public String getWorldName() {
      return this.info.getWorldName();
   }

   public long getWorldTotalTime() {
      return this.info.getWorldTotalTime();
   }

   public boolean isDifficultyLocked() {
      return this.info.isDifficultyLocked();
   }

   public boolean isHardcoreModeEnabled() {
      return this.info.isHardcoreModeEnabled();
   }

   public boolean isInitialized() {
      return this.info.isInitialized();
   }

   public boolean isMapFeaturesEnabled() {
      return this.info.isMapFeaturesEnabled();
   }

   public boolean isThundering() {
      return this.info.isThundering();
   }

   public boolean isVersionSnapshot() {
      return this.info.isVersionSnapshot();
   }

   public Object getInternal() {
      return this.info;
   }
}
