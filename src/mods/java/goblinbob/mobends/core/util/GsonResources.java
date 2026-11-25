package goblinbob.mobends.core.util;

import goblinbob.mobends.core.kumo.KumoSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GsonResources {
   private static Map<ResourceLocation, Object> cache = new HashMap();

   public GsonResources() {
   }

   public static void clearCache() {
      cache.clear();
   }

   public static <T> T get(ResourceLocation location, Class<T> classOfT) throws IOException {
      InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
      if (cache.containsKey(location)) {
         return (T)cache.get(location);
      } else {
         T resource = (T)KumoSerializer.INSTANCE.gson.fromJson(new InputStreamReader(stream), classOfT);
         cache.put(location, resource);
         return resource;
      }
   }
}
