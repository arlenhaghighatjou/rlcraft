package goblinbob.mobends.core.animation.keyframe;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class AnimationLoader {
   private static Map<String, KeyframeAnimation> internalRegistry = new HashMap();
   private static Map<ResourceLocation, KeyframeAnimation> cachedAnimations = new HashMap();

   public AnimationLoader() {
   }

   public static void clearCache() {
      internalRegistry.clear();
      cachedAnimations.clear();
   }

   public static KeyframeAnimation loadFromFile(File file) throws IOException {
      if (file.getName().endsWith(".json")) {
         JsonReader fileReader = new JsonReader(new FileReader(file));
         return (KeyframeAnimation)(new Gson()).fromJson(fileReader, KeyframeAnimation.class);
      } else {
         return BinaryAnimationLoader.loadFromBinaryInputStream(new BufferedInputStream(new FileInputStream(file)));
      }
   }

   public static KeyframeAnimation loadFromString(String animationJson) {
      return (KeyframeAnimation)(new Gson()).fromJson(animationJson, KeyframeAnimation.class);
   }

   public static KeyframeAnimation loadFromResource(ResourceLocation location) throws IOException {
      InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
      if (cachedAnimations.containsKey(location)) {
         return (KeyframeAnimation)cachedAnimations.get(location);
      } else {
         KeyframeAnimation animation = null;
         if (location.getResourcePath().endsWith(".json")) {
            animation = (KeyframeAnimation)(new Gson()).fromJson(new InputStreamReader(stream), KeyframeAnimation.class);
         } else {
            animation = BinaryAnimationLoader.loadFromBinaryInputStream(stream);
         }

         if (animation != null) {
            cachedAnimations.put(location, animation);
         }

         return animation;
      }
   }

   public static KeyframeAnimation loadFromPath(String key) throws IOException {
      int colonIndex = key.indexOf(":");
      if (colonIndex != -1) {
         String domain = key.substring(0, colonIndex);
         String path = key.substring(colonIndex + 1);
         return loadFromResource(new ResourceLocation(domain, path));
      } else {
         return (KeyframeAnimation)internalRegistry.get(key);
      }
   }
}
