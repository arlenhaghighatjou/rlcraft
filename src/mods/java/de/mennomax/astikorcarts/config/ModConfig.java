package de.mennomax.astikorcarts.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "astikorcarts"
)
public class ModConfig {
   @LangKey("config.astikorcarts:speedmodifier")
   @RequiresMcRestart
   @RangeDouble(
      min = -1.0D,
      max = 0.0D
   )
   public static double speedModifier = -0.65D;
   @LangKey("entity.astikorcarts:cargocart.name")
   public static ModConfig.CargoCart cargoCart = new ModConfig.CargoCart();
   @LangKey("entity.astikorcarts:plowcart.name")
   public static ModConfig.PlowCart plowCart = new ModConfig.PlowCart();
   @LangKey("entity.astikorcarts:mobcart.name")
   public static ModConfig.MobCart mobCart = new ModConfig.MobCart();

   public ModConfig() {
   }

   public static class CargoCart {
      public String[] canPull = new String[]{"minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:pig"};

      public CargoCart() {
      }
   }

   @EventBusSubscriber
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("astikorcarts")) {
            ConfigManager.sync("astikorcarts", Type.INSTANCE);
         }

      }
   }

   public static class MobCart {
      public String[] canPull = new String[]{"minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:pig"};

      public MobCart() {
      }
   }

   public static class PlowCart {
      public String[] canPull = new String[]{"minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:pig"};

      public PlowCart() {
      }
   }
}
