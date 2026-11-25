package ivorius.ivtoolkit.models.loaders;

import ivorius.ivtoolkit.models.Model;
import ivorius.ivtoolkit.models.textures.TextureProvider;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftModelLoader {
   private static Logger logger = LogManager.getLogger("Models");
   private static G3DModelLoader g3DModelLoader = new G3DModelLoader(logger);

   public MinecraftModelLoader() {
   }

   public static Model loadModelG3DJ(ResourceLocation resourceLocation, TextureProvider textureProvider) {
      return g3DModelLoader.createModel(new InputStreamReader(streamFromResourceLocation(resourceLocation)), textureProvider);
   }

   public static InputStream streamFromResourceLocation(ResourceLocation resourceLocation) {
      return MinecraftModelLoader.class.getResourceAsStream("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
   }
}
