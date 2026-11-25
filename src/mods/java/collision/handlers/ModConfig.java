package collision.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "collisiondamage"
)
public class ModConfig {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ModConfig.ServerConfig server = new ModConfig.ServerConfig();

   public ModConfig() {
   }

   @EventBusSubscriber(
      modid = "collisiondamage"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("collisiondamage")) {
            ConfigManager.sync("collisiondamage", Type.INSTANCE);
         }

      }
   }

   public static class ServerConfig {
      @Comment({"How large the player's deceleration must be before they will begin taking damage. (Measured in meters per second per second"})
      @Name("Acceleration Threshold")
      @RangeDouble(
         min = 5.0D,
         max = 100.0D
      )
      public double accelerationThreshold = 12.0D;
      @Comment({"Multiplies the damage taken when over the threshold. Default 1.0x is 1 damage per 1m/s/s over threshold."})
      @Name("Damage Multiplier")
      @RangeDouble(
         min = 0.0D,
         max = 100.0D
      )
      public double damageMultiplier = 1.0D;
      @Comment({"Use damage type FLY_INTO_WALL? If false, will instead use FALL. (Set this to false if you want stuff like feather-falling to affect collision damage as well)"})
      @Name("Damage Type Wall")
      public boolean damageTypeWall = true;

      public ServerConfig() {
      }
   }
}
