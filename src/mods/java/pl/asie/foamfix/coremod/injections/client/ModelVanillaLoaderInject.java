package pl.asie.foamfix.coremod.injections.client;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import pl.asie.foamfix.ProxyClient;

public class ModelVanillaLoaderInject {
   public ModelVanillaLoaderInject() {
   }

   public IModel loadModel(ResourceLocation modelLocation) throws Exception {
      IModel model = this.loadModel_foamfix_old(modelLocation);
      if (model != null && ProxyClient.bakingStage >= 1) {
         model.getDependencies();
         model.getTextures();
      }

      return model;
   }

   public IModel loadModel_foamfix_old(ResourceLocation modelLocation) throws Exception {
      return null;
   }
}
