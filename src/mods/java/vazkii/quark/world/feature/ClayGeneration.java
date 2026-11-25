package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.ClayGenerator;

public class ClayGeneration extends Feature {
   public static int minHeight;
   public static int maxHeight;
   public static int clusterSize;
   public static int clusterCount;

   public ClayGeneration() {
   }

   public void setupConfig() {
      clusterSize = this.loadPropInt("Cluster size", "", 20);
      clusterCount = this.loadPropInt("Cluster count", "", 3);
      minHeight = this.loadPropInt("Min Height", "", 20);
      maxHeight = this.loadPropInt("Max Height", "", 60);
   }

   public void preInit(FMLPreInitializationEvent event) {
      GameRegistry.registerWorldGenerator(new ClayGenerator(clusterSize, clusterCount), 0);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Underground Clay";
   }
}
