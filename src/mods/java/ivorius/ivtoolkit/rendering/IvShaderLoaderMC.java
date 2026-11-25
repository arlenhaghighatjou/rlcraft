package ivorius.ivtoolkit.rendering;

import ivorius.ivtoolkit.tools.IvDependencyLoader;
import ivorius.ivtoolkit.tools.IvDependencyLoaderMC;
import net.minecraft.util.ResourceLocation;

public class IvShaderLoaderMC extends IvDependencyLoaderMC {
   public IvShaderLoaderMC() {
      super("<ivimport:", ">");
   }

   public void setUpShader(IvShaderInstance shaderInstance, ResourceLocation vertexShader, ResourceLocation fragmentShader) throws IvDependencyLoader.LoadException, IvDependencyLoader.RecursionException {
      String vShader = this.loadResource(vertexShader.toString());
      String fShader = this.loadResource(fragmentShader.toString());
      shaderInstance.trySettingUpShader(vShader, fShader);
   }
}
