package team.creative.enhancedvisuals.common.addon.toughasnails;

import net.minecraft.util.ResourceLocation;
import team.creative.enhancedvisuals.common.visual.VisualRegistry;

public class ToughAsNailsAddon {
   public static ThirstHandler thirst;
   public static TemperatureHandler temperature;

   public ToughAsNailsAddon() {
   }

   public static void load() {
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "thirst"), thirst = new ThirstHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "temperature"), temperature = new TemperatureHandler());
   }
}
