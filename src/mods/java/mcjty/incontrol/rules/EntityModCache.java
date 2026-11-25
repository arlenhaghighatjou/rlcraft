package mcjty.incontrol.rules;

import java.util.HashMap;
import java.util.Map;
import mcjty.tools.varia.Tools;
import net.minecraft.entity.Entity;

public class EntityModCache {
   private Map<Class, String> cache = new HashMap();

   public EntityModCache() {
   }

   public String getMod(Entity entity) {
      Class<? extends Entity> cls = entity.getClass();
      if (!this.cache.containsKey(cls)) {
         this.cache.put(cls, Tools.findModID(cls));
      }

      return (String)this.cache.get(cls);
   }
}
