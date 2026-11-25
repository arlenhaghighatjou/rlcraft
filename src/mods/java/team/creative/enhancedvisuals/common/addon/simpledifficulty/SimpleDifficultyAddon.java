package team.creative.enhancedvisuals.common.addon.simpledifficulty;

import net.minecraft.util.ResourceLocation;
import team.creative.enhancedvisuals.common.visual.VisualRegistry;

public class SimpleDifficultyAddon {
   public static ThirstHandler thirst;
   public static TemperatureHandler temperature;

   public SimpleDifficultyAddon() {
   }

   public static void load() {
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "simple-thirst"), thirst = new ThirstHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "simple-temperature"), temperature = new TemperatureHandler());
   }
}
