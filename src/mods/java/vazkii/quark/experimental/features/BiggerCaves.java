package vazkii.quark.experimental.features;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.experimental.world.BigCaveGenerator;

public class BiggerCaves extends Feature {
   public static float overallCaveSizeVariance;
   public static float overallCaveSizeBase;
   public static float bigCaveSizeVariance;
   public static float bigCaveSizeBase;
   public static boolean generateHugeCaves;
   public static int hugeCaveMaxY;
   public static int hugeCaveChance;
   public static float hugeCaveSizeVariance;
   public static float hugeCaveSizeBase;

   public BiggerCaves() {
   }

   public void setupConfig() {
      overallCaveSizeVariance = (float)this.loadPropDouble("Overall Cave Size Variance", "Vanilla value is 3", 7.0D);
      overallCaveSizeBase = (float)this.loadPropDouble("Overall Cave Size Minimum", "Vanilla value is 0", 0.0D);
      bigCaveSizeVariance = (float)this.loadPropDouble("Big Cave Size Variance", "Vanilla value is 3", 8.0D);
      bigCaveSizeBase = (float)this.loadPropDouble("Big Cave Size Minimum", "Vanilla value is 1", 1.0D);
      generateHugeCaves = this.loadPropBool("Huge Caves Enabled", "", true);
      hugeCaveMaxY = this.loadPropInt("Huge Cave Maximum Y Level", "", 32);
      hugeCaveChance = this.loadPropInt("Huge Cave Chance", "Given the value of this config as X, in average, 1 in X caves will be a huge cave", 1800);
      hugeCaveSizeVariance = (float)this.loadPropDouble("Huge Cave Size Variance", "", 6.0D);
      hugeCaveSizeBase = (float)this.loadPropDouble("Huge Cave Size Minimum", "", 14.0D);
   }

   @SubscribeEvent
   public void getCaveGenerator(InitMapGenEvent event) {
      if (event.getType() == EventType.CAVE) {
         event.setNewGen(new BigCaveGenerator());
      }

   }

   public boolean hasTerrainSubscriptions() {
      return true;
   }
}
