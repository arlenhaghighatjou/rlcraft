package knightminer.inspirations.plugins.waila;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
   id = "WailaPlugin",
   modsRequired = "waila",
   defaultEnable = true
)
public class WailaPlugin {
   public static final String modid = "waila";

   public WailaPlugin() {
   }

   @Subscribe
   public void preInit(FMLPreInitializationEvent event) {
      FMLInterModComms.sendMessage("waila", "register", "knightminer.inspirations.plugins.waila.WailaRegistrar.registerWaila");
   }
}
