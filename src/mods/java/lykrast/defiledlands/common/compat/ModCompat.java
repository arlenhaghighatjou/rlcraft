package lykrast.defiledlands.common.compat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import lykrast.defiledlands.common.util.Config;
import lykrast.defiledlands.core.DefiledLands;
import net.minecraftforge.fml.common.Loader;

public abstract class ModCompat {
   public static HashMap<String, Class<? extends ModCompat>> compat = new HashMap();
   public static Set<ModCompat> compatLoaded = new HashSet();

   public ModCompat() {
   }

   public static void preInitCompat() {
      for(Entry<String, Class<? extends ModCompat>> e : compat.entrySet()) {
         if (Loader.isModLoaded((String)e.getKey())) {
            try {
               if (((Boolean)Config.compatEnabled.get(e.getKey())).booleanValue()) {
                  ModCompat c = (ModCompat)((Class)e.getValue()).newInstance();
                  compatLoaded.add(c);
                  c.preInit();
               }
            } catch (Exception var3) {
               DefiledLands.LOGGER.warn("Compat module for " + (String)e.getKey() + " could not be pre-initialized", var3);
            }
         }
      }

   }

   public static void initCompat() {
      for(ModCompat compat : compatLoaded) {
         compat.init();
      }

   }

   public static void postInitCompat() {
      for(ModCompat compat : compatLoaded) {
         compat.postInit();
      }

   }

   public void preInit() {
   }

   public void init() {
   }

   public void postInit() {
   }

   static {
      compat.put("chisel", CompatChisel.class);
      compat.put("projecte", CompatProjectE.class);
      compat.put("immersiveengineering", CompatImmersiveEngineering.class);
      compat.put("tconstruct", CompatTinkersConstruct.class);
   }
}
