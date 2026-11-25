package pl.asie.foamfix.coremod.injections.client;

import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import pl.asie.foamfix.util.FoamUtils;

public class VanillaModelWrapperInject {
   private ModelBlockAnimation animation;

   public VanillaModelWrapperInject() {
   }

   private IBakedModel bakeNormal(ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter, boolean uvLocked) {
      IBakedModel bakedModel = this.bakeNormal_foamfix_old(model, perState, modelState, newTransforms, format, bakedTextureGetter, uvLocked);
      if (bakedModel instanceof PerspectiveMapWrapper && (this.animation == null || this.animation.getClips().isEmpty())) {
         try {
            return new PerspectiveMapWrapper(FoamUtils.PMW_GET_PARENT.invokeExact((PerspectiveMapWrapper)bakedModel), perState);
         } catch (Throwable var10) {
            var10.printStackTrace();
            return bakedModel;
         }
      } else {
         return bakedModel;
      }
   }

   private IBakedModel bakeNormal_foamfix_old(ModelBlock model, IModelState perState, IModelState modelState, List<TRSRTransformation> newTransforms, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter, boolean uvLocked) {
      return null;
   }
}
