package vazkii.quark.experimental.features;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class RejectMods extends Feature {
   public static String[] rejected;

   public RejectMods() {
   }

   public static boolean accepts(Map<String, String> mods, Side remoteSide) {
      if (!"r1.6-179".equals("r1.6-179") && !((String)mods.get("quark")).equals("r1.6-179")) {
         return false;
      } else if (!ModuleLoader.isFeatureEnabled(RejectMods.class)) {
         return true;
      } else {
         boolean var2;
         if (remoteSide != Side.SERVER) {
            Stream var10000 = Arrays.stream(rejected);
            Set var10001 = mods.keySet();
            var10001.getClass();
            if (!var10000.anyMatch(var10001::contains)) {
               var2 = false;
               return var2;
            }
         }

         var2 = true;
         return var2;
      }
   }

   public void setupConfig() {
      rejected = this.loadPropStringList("Rejected Mods", "Mods which aren't allowed on this server.", new String[0]);
   }
}
