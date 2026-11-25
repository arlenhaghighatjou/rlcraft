package com.TominoCZ.FBP.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder;

public final class FBPModelTransformer {
   public FBPModelTransformer() {
   }

   public static FBPSimpleBakedModel transform(IBakedModel model, IBlockState state, long rand, FBPModelTransformer.IVertexTransformer transformer) {
      try {
         FBPSimpleBakedModel out = new FBPSimpleBakedModel(model);

         for(int i = 0; i <= 6; ++i) {
            EnumFacing side = i == 6 ? null : EnumFacing.getFront(i);

            for(BakedQuad quad : model.getQuads(state, side, rand)) {
               out.addQuad(side, transform(quad, transformer));
            }
         }

         return out;
      } catch (Throwable var10) {
         return null;
      }
   }

   private static BakedQuad transform(BakedQuad quad, FBPModelTransformer.IVertexTransformer transformer) {
      VertexFormat format = quad.getFormat();
      Builder builder = new Builder(format);
      LightUtil.putBakedQuad(new FBPModelTransformer.VertexTransformerWrapper(builder, quad, transformer), quad);
      return builder.build();
   }

   public interface IVertexTransformer {
      float[] transform(BakedQuad var1, VertexFormatElement var2, float... var3);
   }

   private static final class VertexTransformerWrapper implements IVertexConsumer {
      private final IVertexConsumer parent;
      private final BakedQuad parentQuad;
      private final VertexFormat format;
      private final FBPModelTransformer.IVertexTransformer transformer;

      public VertexTransformerWrapper(IVertexConsumer parent, BakedQuad parentQuad, FBPModelTransformer.IVertexTransformer transformer) {
         this.parent = parent;
         this.parentQuad = parentQuad;
         this.format = parent.getVertexFormat();
         this.transformer = transformer;
      }

      public VertexFormat getVertexFormat() {
         return this.format;
      }

      public void setQuadTint(int tint) {
         this.parent.setQuadTint(tint);
      }

      public void setQuadOrientation(EnumFacing orientation) {
         this.parent.setQuadOrientation(orientation);
      }

      public void setApplyDiffuseLighting(boolean diffuse) {
         this.parent.setApplyDiffuseLighting(diffuse);
      }

      public void setTexture(TextureAtlasSprite texture) {
         this.parent.setTexture(texture);
      }

      public void put(int elementId, float... data) {
         VertexFormatElement element = this.format.getElement(elementId);
         this.parent.put(elementId, this.transformer.transform(this.parentQuad, element, data));
      }
   }
}
