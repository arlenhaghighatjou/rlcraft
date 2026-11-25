package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.BrokenPortalGenerator;

public class BrokenNetherPortals extends Feature {
   public static double chance;
   DimensionConfig dims;

   public BrokenNetherPortals() {
   }

   public void setupConfig() {
      chance = this.loadPropChance("Spawn Chance", "The chance a broken portal will try to spawn per chunk (1 is 100%, 0 is 0%)", 0.02D);
      this.dims = new DimensionConfig(this.configCategory, "-1");
   }

   public void preInit(FMLPreInitializationEvent event) {
      GameRegistry.registerWorldGenerator(new BrokenPortalGenerator(this.dims), 1);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
