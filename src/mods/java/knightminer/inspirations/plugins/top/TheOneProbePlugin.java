package knightminer.inspirations.plugins.top;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
   id = "TheOneProbePlugin",
   modsRequired = "theoneprobe",
   defaultEnable = true
)
public class TheOneProbePlugin {
   public static final String modid = "theoneprobe";

   public TheOneProbePlugin() {
   }

   @Subscribe
   public void preInit(FMLPreInitializationEvent event) {
      FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "knightminer.inspirations.plugins.top.TheOneProbeRegistrar");
   }
}
