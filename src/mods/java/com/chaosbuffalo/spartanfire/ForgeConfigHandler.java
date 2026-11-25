package com.chaosbuffalo.spartanfire;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "spartanfire"
)
public class ForgeConfigHandler {
   @Comment({"General Config Options"})
   @Name("General Options")
   public static final ForgeConfigHandler.GeneralConfig general = new ForgeConfigHandler.GeneralConfig();

   public ForgeConfigHandler() {
   }

   @EventBusSubscriber(
      modid = "spartanfire"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("spartanfire")) {
            ConfigManager.sync("spartanfire", Type.INSTANCE);
         }

      }
   }

   public static class GeneralConfig {
      @Comment({"Registers custom recipe handling for venom throwing weapons"})
      @Name("Register Venom Throwing Weapon Recipes")
      @RequiresMcRestart
      public boolean registerVenomThrowingRecipes = true;
      @Comment({"Registers custom recipe handling for flame/ice throwing weapons"})
      @Name("Register Flame/Ice Throwing Weapon Recipes")
      @RequiresMcRestart
      public boolean registerFlameIceThrowingRecipes = true;

      public GeneralConfig() {
      }
   }
}
