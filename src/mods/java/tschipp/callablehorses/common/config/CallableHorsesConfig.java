package tschipp.callablehorses.common.config;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tschipp.callablehorses.common.helper.ReflectionUtil;

@Config(
   modid = "callablehorses"
)
public class CallableHorsesConfig {
   @LangKey("callablehorses")
   @Comment({"General Mod Settings"})
   public static Configs.Settings settings = new Configs.Settings();

   public CallableHorsesConfig() {
   }

   @EventBusSubscriber
   public static class EventHandler {
      private static final MethodHandle CONFIGS_GETTER = ReflectionUtil.findFieldGetter(ConfigManager.class, "CONFIGS");
      private static Configuration configuration;

      public EventHandler() {
      }

      public static Configuration getConfiguration() {
         if (configuration == null) {
            try {
               String fileName = "callablehorses.cfg";
               Map<String, Configuration> configsMap = CONFIGS_GETTER.invokeExact();
               Optional<Entry<String, Configuration>> entryOptional = configsMap.entrySet().stream().filter((entry) -> {
                  return "callablehorses.cfg".equals((new File((String)entry.getKey())).getName());
               }).findFirst();
               entryOptional.ifPresent((stringConfigurationEntry) -> {
                  configuration = (Configuration)stringConfigurationEntry.getValue();
               });
            } catch (Throwable var3) {
               var3.printStackTrace();
            }
         }

         return configuration;
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("callablehorses")) {
            ConfigManager.load("callablehorses", Type.INSTANCE);
         }

      }
   }
}
