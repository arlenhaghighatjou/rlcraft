package pl.asie.foamfix.client;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import pl.asie.foamfix.util.MethodHandleHelper;

public class FoamyMultipartBakedModel implements IBakedModel {
   private static final MethodHandle SELECTORS_GETTER = MethodHandleHelper.findFieldGetter(MultipartBakedModel.class, "selectors", "selectors");
   public final Predicate[] predicates;
   public final IBakedModel[] models;

   public FoamyMultipartBakedModel(MultipartBakedModel parent) {
      try {
         Map<Predicate<IBlockState>, IBakedModel> map = SELECTORS_GETTER.invoke(parent);
         this.predicates = new Predicate[map.size()];
         this.models = new IBakedModel[this.predicates.length];
         int i = 0;

         for(Entry<Predicate<IBlockState>, IBakedModel> entry : map.entrySet()) {
            this.predicates[i] = (Predicate)entry.getKey();
            this.models[i++] = (IBakedModel)entry.getValue();
         }

      } catch (Throwable var6) {
         throw new RuntimeException(var6);
      }
   }

   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      if (state == null) {
         return ImmutableList.of();
      } else {
         List<BakedQuad> list = new ArrayList(this.predicates.length);

         for(int i = 0; i < this.predicates.length; ++i) {
            if (this.predicates[i].apply(state)) {
               list.addAll(this.models[i].getQuads(state, side, rand++));
            }
         }

         return list;
      }
   }

   public boolean isAmbientOcclusion() {
      return this.models[0].isAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.models[0].isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.models[0].getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.models[0].getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this.models[0].getOverrides();
   }
}
