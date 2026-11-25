package pl.asie.foamfix.client;

import java.lang.invoke.MethodHandle;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import pl.asie.foamfix.util.MethodHandleHelper;

public class FoamFixDynamicItemModels {
   public FoamFixDynamicItemModels() {
   }

   public static void register() {
      MethodHandle LOADERS = MethodHandleHelper.findFieldGetter(ModelLoaderRegistry.class, "loaders");

      try {
         Set<ICustomModelLoader> loaders = LOADERS.invoke();
         loaders.remove(net.minecraftforge.client.model.ItemLayerModel.Loader.INSTANCE);
         loaders.add(FoamFixDynamicItemModels.Loader.INSTANCE);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      return FoamyItemLayerModel.bakeStatic((ItemLayerModel)this, state, format, bakedTextureGetter);
   }

   public static enum Loader implements ICustomModelLoader {
      INSTANCE;

      private static final IModel model = new FoamyItemLayerModel(ItemLayerModel.INSTANCE);

      private Loader() {
      }

      public void onResourceManagerReload(IResourceManager resourceManager) {
         net.minecraftforge.client.model.ItemLayerModel.Loader.INSTANCE.onResourceManagerReload(resourceManager);
      }

      public boolean accepts(ResourceLocation modelLocation) {
         return net.minecraftforge.client.model.ItemLayerModel.Loader.INSTANCE.accepts(modelLocation);
      }

      public IModel loadModel(ResourceLocation modelLocation) {
         return model;
      }
   }
}
