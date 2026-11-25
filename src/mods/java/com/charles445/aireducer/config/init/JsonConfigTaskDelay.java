package com.charles445.aireducer.config.init;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.common.Loader;

public class JsonConfigTaskDelay {
   public JsonConfigTaskDelay() {
   }

   public static Map<String, Integer> getDefaults() {
      Map<String, Integer> map = new HashMap();
      map.put("minecraft:rabbit", Integer.valueOf(6));
      if (Loader.isModLoaded("iceandfire")) {
         int iafv = 10;
         map.put("iceandfire:deathworm", Integer.valueOf(iafv));
         map.put("iceandfire:myrmex_worker", Integer.valueOf(iafv));
         map.put("iceandfire:myrmex_soldier", Integer.valueOf(iafv));
         map.put("iceandfire:myrmex_sentinel", Integer.valueOf(iafv));
         map.put("iceandfire:myrmex_royal", Integer.valueOf(iafv));
         map.put("iceandfire:myrmex_queen", Integer.valueOf(iafv));
      }

      return map;
   }
}
