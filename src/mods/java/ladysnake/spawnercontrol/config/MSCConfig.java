package ladysnake.spawnercontrol.config;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "spawnercontrol",
   name = "spawnercontrol/spawnercontrol"
)
public class MSCConfig {
   @Ignore
   public static final String MAIN_CONFIG_FILE = "spawnercontrol/spawnercontrol";
   @Ignore
   public static final String VANILLA_CONFIG_CATEGORY = "general.vanillaSpawnerConfig";
   @RequiresWorldRestart
   @Comment({"If set to false, vanilla spawners won't be affected by the mod. This mod's own spawners will be the only ones affected by this config"})
   public static boolean alterVanillaSpawner = true;
   @Comment({"The spawner configuration for vanilla or vanilla-derived spawners"})
   public static SpawnerConfig vanillaSpawnerConfig = new SpawnerConfig();
   @RequiresMcRestart
   @RequiresWorldRestart
   @Comment({"A list of ids used to generate custom spawner objects", "Each custom spawner uses a separate configuration file, accessible in the 'custom spawners config' category"})
   public static String[] customSpawners = new String[0];
   @RequiresMcRestart
   @Comment({"If set to true, the mod will generate its own creative tab to put custom spawners in"})
   public static boolean makeCreativeTab = false;

   public MSCConfig() {
   }

   public static void portConfig() {
      VFile2 oldConfigFile = new VFile2(CustomSpawnersConfig.configDir, "spawnercontrol.cfg");
      if (oldConfigFile.exists()) {
         Configuration oldConfig = new Configuration(oldConfigFile, "1.0");
         Configuration newConfig = CustomSpawnersConfig.getMainConfiguration();
         if (oldConfig.getLoadedConfigVersion() == null) {
            ConfigCategory general = oldConfig.getCategory("general");
            ConfigCategory vanillaSpawner = newConfig.getCategory("general.vanillaSpawnerConfig");
            copyConfigCategory(general, vanillaSpawner);
            if (general.getChildren().size() > 0) {
               ConfigCategory oldSpawnConditions = (ConfigCategory)general.getChildren().iterator().next();
               vanillaSpawner.getChildren().stream().filter((child) -> {
                  return child.getName().equals(oldSpawnConditions.getName());
               }).findAny().ifPresent((spawnConditions) -> {
                  copyConfigCategory(oldSpawnConditions, spawnConditions);
               });
            }

            newConfig.save();

            oldConfigFile.renameTo(new VFile2(CustomSpawnersConfig.configDir, "spawnercontrol_old.cfg"));
         }

      }
   }

   private static void copyConfigCategory(ConfigCategory oldConf, ConfigCategory newConf) {
      for(Entry<String, Property> entry : newConf.getValues().entrySet()) {
         if (oldConf.containsKey((String)entry.getKey())) {
            newConf.put((String)entry.getKey(), oldConf.get((String)entry.getKey()));
         }
      }

   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, MSCConfig::onConfigChanged);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("spawnercontrol")) {
         ConfigManager.sync("spawnercontrol", Type.INSTANCE);
         Collection<SpawnerConfigHolder> holders = CustomSpawnersConfig.getCustomSpawnerConfigs();
         for(SpawnerConfigHolder holder : holders) {
            holder.sync();
         }
      }

   }

   public static void register() {
      ConfigManager.register("spawnercontrol", "spawnercontrol/spawnercontrol", "general", null,
         ConfigEntry.value("alterVanillaSpawner", boolean.class, () -> MSCConfig.alterVanillaSpawner, v -> MSCConfig.alterVanillaSpawner = (Boolean)v).comment("If set to false, vanilla spawners won't be affected by the mod. This mod's own spawners will be the only ones affected by this config").requiresWorldRestart(),
         ConfigEntry.category("vanillaSpawnerConfig", MSCConfig.vanillaSpawnerConfig.entries()).comment("The spawner configuration for vanilla or vanilla-derived spawners"),
         ConfigEntry.value("customSpawners", String[].class, () -> MSCConfig.customSpawners, v -> MSCConfig.customSpawners = (String[])v).comment("A list of ids used to generate custom spawner objects", "Each custom spawner uses a separate configuration file, accessible in the 'custom spawners config' category").requiresMcRestart().requiresWorldRestart(),
         ConfigEntry.value("makeCreativeTab", boolean.class, () -> MSCConfig.makeCreativeTab, v -> MSCConfig.makeCreativeTab = (Boolean)v).comment("If set to true, the mod will generate its own creative tab to put custom spawners in").requiresMcRestart());
   }
}
