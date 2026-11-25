package bl4ckscor3.mod.sit;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "sit"
)
public class Configuration {
   @Name("block_reach_distance")
   @Comment({"The maximum distance in blocks that the player can be away from a block to still be able to sit on it.", "Do note, that this is limited by how far a player can access a block in vanilla Minecraft.", "If this is set to 0, the player has to stand on top of the block to be able to sit on it."})
   @RangeInt(
      min = 0,
      max = 1000
   )
   public static int blockReachDistance = 4;

   public Configuration() {
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, Configuration::onConfigChanged);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("sit")) {
         ConfigManager.sync("sit", Type.INSTANCE);
      }

   }

   public static void register() {
      ConfigManager.register("sit", "", "general", null,
         ConfigEntry.value("block_reach_distance", int.class, () -> Configuration.blockReachDistance, v -> Configuration.blockReachDistance = (Integer)v).comment("The maximum distance in blocks that the player can be away from a block to still be able to sit on it.", "Do note, that this is limited by how far a player can access a block in vanilla Minecraft.", "If this is set to 0, the player has to stand on top of the block to be able to sit on it.").langKey("sit.general.blockreachdistance").range(0, 1000));
   }
}
