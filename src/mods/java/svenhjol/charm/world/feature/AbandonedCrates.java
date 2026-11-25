package svenhjol.charm.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import svenhjol.charm.Charm;
import svenhjol.charm.crafting.feature.Crate;
import svenhjol.charm.world.generator.AbandonedCrateGenerator;
import svenhjol.meson.Feature;

public class AbandonedCrates extends Feature {
   public static int maxTries;
   public static int lowerLimit;
   public static int upperLimit;
   public static double generateChance;
   public static float rareChance;
   public static float valuableChance;
   public static float uncommonChance;

   public AbandonedCrates() {
   }

   public String getDescription() {
      return "Abandoned Crates may be found underground.  These crates are sealed so must be smashed to obtain contained items.";
   }

   public void configure() {
      super.configure();
      generateChance = this.propDouble("Generate crate chance", "Chance (out of 1.0) of a crate generating in a chunk, if it is possible to do so.", 0.3D);
      upperLimit = this.propInt("Upper limit", "Crates will spawn lower than this Y value.", 48);
      lowerLimit = this.propInt("Lower limit", "Crates will spawn higher than this Y value.\nFor Cubic Chunks you should set this value very low.", 28);
      maxTries = 50;
      rareChance = 0.005F;
      valuableChance = 0.06F;
      uncommonChance = 0.2F;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      if (Charm.hasFeature(Crate.class)) {
         GameRegistry.registerWorldGenerator(new AbandonedCrateGenerator(), 1000);
      }

   }
}
