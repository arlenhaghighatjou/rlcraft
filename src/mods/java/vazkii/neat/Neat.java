package vazkii.neat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "neat",
   name = "Neat",
   version = "1.4-17",
   dependencies = "",
   guiFactory = "vazkii.neat.GuiFactory",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Neat {
   public static final String MOD_ID = "neat";
   public static final String MOD_NAME = "Neat";
   public static final String BUILD = "17";
   public static final String VERSION = "1.4-17";
   public static final String DEPENDENCIES = "";
   public static final String GUI_FACTORY = "vazkii.neat.GuiFactory";

   public Neat() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      NeatConfig.init(event.getSuggestedConfigurationFile());
      (new ToggleKeybind()).register(MinecraftForge.EVENT_BUS);
      (new HealthBarRenderer()).register(MinecraftForge.EVENT_BUS);
   }
}
