package ivorius.ivtoolkit.tools;

import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class IvDependencyLoaderMC extends IvDependencyLoader {
   public IvDependencyLoaderMC(String importStart, String importEnd) {
      super(importStart, importEnd);
   }

   public String loadResourceRaw(String location) throws IvDependencyLoader.LoadException {
      try {
         IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
         IResource vShaderRes = resourceManager.getResource(new ResourceLocation(location));
         return IOUtils.toString(vShaderRes.getInputStream(), Charsets.UTF_8);
      } catch (Throwable var4) {
         throw new IvDependencyLoader.LoadException(var4);
      }
   }
}
