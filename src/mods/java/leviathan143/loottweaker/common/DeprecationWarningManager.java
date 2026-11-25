package leviathan143.loottweaker.common;

import crafttweaker.CraftTweakerAPI;
import java.util.HashSet;
import java.util.Set;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import stanhebben.zenscript.annotations.ZenClass;

@EventBusSubscriber(
   modid = "loottweaker"
)
public class DeprecationWarningManager {
   private static final Set<String> deprecatedObjectsUsed = new HashSet();

   public DeprecationWarningManager() {
   }

   public static void addWarning() {
      if (LTConfig.warnings.deprecation) {
         try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stackTrace[2];
            Class<?> clazz = Class.forName(caller.getClassName());
            ZenClass zenClass = (ZenClass)clazz.getAnnotation(ZenClass.class);
            if (zenClass == null) {
               throw new IllegalArgumentException(clazz.getName() + " is not a ZenClass");
            }

            String zenName = zenClass.value();
            deprecatedObjectsUsed.add(zenName + "." + caller.getMethodName());
         } catch (ClassNotFoundException var5) {
            var5.printStackTrace();
         }

      }
   }

   public static void printDeprecationWarnings() {
      if (!deprecatedObjectsUsed.isEmpty() && LTConfig.warnings.deprecation) {
         CraftTweakerAPI.logWarning("LootTweaker: One or more scripts use deprecated methods. Check crafttweaker.log for more information.");

         for(String deprecatedObj : deprecatedObjectsUsed) {
            CraftTweakerAPI.logInfo(String.format("%s is deprecated", deprecatedObj));
         }

         CraftTweakerAPI.logInfo("Refer to https://loottweaker-docs.readthedocs.io/en/latest/reference/deprecations.html for more info. This warning can be disabled in the config.");
      }
   }
}
