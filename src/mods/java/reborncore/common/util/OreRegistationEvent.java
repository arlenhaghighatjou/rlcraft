package reborncore.common.util;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import org.apache.commons.io.FileUtils;
import reborncore.common.RebornCoreConfig;

public class OreRegistationEvent {
   public static Map<String, String> oreModIDMap = new HashMap();

   public OreRegistationEvent() {
   }

   @SubscribeEvent
   public static void oreRegistationEvent(OreRegisterEvent event) {
      if (RebornCoreConfig.oreDebug) {
         ModContainer modContainer = Loader.instance().activeModContainer();
         oreModIDMap.put(event.getName(), modContainer.getModId());
      }
   }

   public static void loadComplete() {
      if (RebornCoreConfig.oreDebug) {
         File file = new File("rc_ores.txt");
         StringBuilder stringBuilder = new StringBuilder();
         oreModIDMap.forEach((s, s2) -> {
            stringBuilder.append(s + " was added by " + s2);
            stringBuilder.append("\n");
         });

         try {
            FileUtils.writeStringToFile(file, stringBuilder.toString(), Charsets.UTF_8);
         } catch (IOException var3) {
            throw new RuntimeException("Failed to export ore data", var3);
         }
      }
   }
}
