package ivorius.ivtoolkit.rendering;

import com.google.common.base.Charsets;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class IvShaderInstanceMC {
   public IvShaderInstanceMC() {
   }

   /** @deprecated */
   @Deprecated
   public static void trySettingUpShader(IvShaderInstance shaderInstance, ResourceLocation vertexShader, ResourceLocation fragmentShader, String utils) {
      String vShader = null;
      String fShader = null;
      IResource vShaderRes = null;
      IResource fShaderRes = null;

      try {
         vShaderRes = Minecraft.getMinecraft().getResourceManager().getResource(vertexShader);
         fShaderRes = Minecraft.getMinecraft().getResourceManager().getResource(fragmentShader);
      } catch (IOException var10) {
         var10.printStackTrace();
      }

      if (vShaderRes != null && fShaderRes != null) {
         try {
            vShader = IOUtils.toString(vShaderRes.getInputStream(), Charsets.UTF_8);
            fShader = IOUtils.toString(fShaderRes.getInputStream(), Charsets.UTF_8);
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         if (vShader != null && fShader != null) {
            if (utils != null) {
               vShader = addUtils(vShader, utils);
               fShader = addUtils(fShader, utils);
            }

            shaderInstance.trySettingUpShader(vShader, fShader);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public static String addUtils(String shader, String utils) {
      int indexVersion = shader.indexOf("#version");
      if (indexVersion < 0) {
         indexVersion = 0;
      }

      int indexVersionNL = shader.indexOf("\n", indexVersion);
      return shader.substring(0, indexVersionNL + 1) + utils + shader.substring(indexVersionNL + 1);
   }
}
