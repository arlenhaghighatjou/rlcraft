package betterquesting.legacy;

import betterquesting.legacy.v0.LegacyLoader_v0;
import java.util.HashMap;

public class LegacyLoaderRegistry {
   private static HashMap<String, ILegacyLoader> legReg = new HashMap();

   public LegacyLoaderRegistry() {
   }

   public static ILegacyLoader getLoader(String version) {
      return (ILegacyLoader)legReg.get(version);
   }

   static {
      legReg.put("0.0.0", LegacyLoader_v0.INSTANCE);
   }
}
