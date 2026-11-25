package com.charles445.aireducer.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(
   modid = "aireducer"
)
public class ModConfig {
   @Comment({"Options that affect all mobs"})
   @Name("Any")
   public static AnyConfig any = new AnyConfig();
   @Comment({"Ice and Fire 1.8+ AI Options"})
   @Name("IceAndFire")
   public static IceAndFireConfig iceandfire = new IceAndFireConfig();
   @Comment({"Ice and Fire 1.7.1 AI Options"})
   @Name("IceAndFire171")
   public static IceAndFireOldConfig iceandfireold = new IceAndFireOldConfig();
   @Comment({"Minecraft AI Options"})
   @Name("Minecraft")
   public static VanillaConfig vanilla = new VanillaConfig();
   @Comment({"Enables debug features. Will spam the console with unnecessary information, so leave this off unless you are trying to fix a problem!"})
   @Name("Debug Mode")
   public static boolean debug = false;
   @Comment({"Master switch, enables all features"})
   @Name("Reducer Enabled")
   public static boolean enabled = true;

   public ModConfig() {
   }

   @EventBusSubscriber(
      modid = "aireducer"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      @SideOnly(Side.CLIENT)
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("aireducer")) {
            ConfigManager.sync("aireducer", Type.INSTANCE);
         }

      }
   }
}
