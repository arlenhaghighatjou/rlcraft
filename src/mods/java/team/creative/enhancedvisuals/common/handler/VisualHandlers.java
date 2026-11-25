package team.creative.enhancedvisuals.common.handler;

import net.minecraft.util.ResourceLocation;
import team.creative.enhancedvisuals.common.visual.VisualRegistry;

public class VisualHandlers {
   public static ExplosionHandler EXPLOSION;
   public static PotionHandler POTION;
   public static SandSplatHandler SAND;
   public static SplashHandler SPLASH;
   public static DamageHandler DAMAGE;
   public static SlenderHandler SLENDER;
   public static SaturationHandler SATURATION;
   public static HeartbeatHandler HEARTBEAT;

   public VisualHandlers() {
   }

   public static void init() {
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "explosion"), EXPLOSION = new ExplosionHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "potion"), POTION = new PotionHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "sand"), SAND = new SandSplatHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "splash"), SPLASH = new SplashHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "damage"), DAMAGE = new DamageHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "slender"), SLENDER = new SlenderHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "saturation"), SATURATION = new SaturationHandler());
      VisualRegistry.registerHandler(new ResourceLocation("enhancedvisuals", "heartbeat"), HEARTBEAT = new HeartbeatHandler());
   }
}
