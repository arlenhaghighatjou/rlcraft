package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.NetherFossilGenerator;

public class NetherFossils extends Feature {
   public static int chance;

   public NetherFossils() {
   }

   public void setupConfig() {
      chance = this.loadPropInt("Fossil Chance", "The rarity of a fossil in a chunk. Higher means fewer fossils.", 25);
   }

   public void preInit(FMLPreInitializationEvent event) {
      GameRegistry.registerWorldGenerator(new NetherFossilGenerator(), 0);
   }
}
