package com.TominoCZ.FBP.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class FBPModelHelper {
   static int vertexes = 0;
   static boolean isAllCorruptedTexture = true;

   public FBPModelHelper() {
   }

   public static boolean isModelValid(IBlockState state) {
      IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
      TextureAtlasSprite s = model.getParticleTexture();
      if (s != null && !s.getIconName().equals("missingno")) {
         vertexes = 0;

         try {
            FBPModelTransformer.transform(model, state, 0L, new FBPModelTransformer.IVertexTransformer() {
               public float[] transform(BakedQuad quad, VertexFormatElement element, float... data) {
                  if (element.getUsage() == EnumUsage.POSITION) {
                     ++FBPModelHelper.vertexes;
                  }

                  TextureAtlasSprite s = quad.getSprite();
                  if (s != null && !s.getIconName().equals("missingno")) {
                     FBPModelHelper.isAllCorruptedTexture = false;
                  }

                  return data;
               }
            });
         } catch (Throwable var4) {
            ;
         }

         return vertexes >= 3 && !isAllCorruptedTexture;
      } else {
         return false;
      }
   }
}
