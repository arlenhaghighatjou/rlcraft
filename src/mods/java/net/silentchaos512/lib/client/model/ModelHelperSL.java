package net.silentchaos512.lib.client.model;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;

public class ModelHelperSL {
   public ModelHelperSL() {
   }

   public static OBJModel loadModel(ResourceLocation resource) {
      try {
         return (OBJModel)OBJLoader.INSTANCE.loadModel(resource);
      } catch (Exception var2) {
         throw new ReportedException(new CrashReport("Error making the model for " + resource, var2));
      }
   }

   public static IModel retexture(OBJModel model, String toReplace, String replaceWith) {
      return model.retexture(ImmutableMap.of(toReplace, replaceWith)).process(ImmutableMap.of("flip-v", "true"));
   }

   public static IBakedModel bake(IModel model) {
      return model.bake(TRSRTransformation.identity(), Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
   }

   public static void renderModel(IBakedModel model, int color) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.ITEM);
      List<BakedQuad> quads = model.getQuads((IBlockState)null, (EnumFacing)null, 0L);
      quads.forEach((quad) -> {
         LightUtil.renderQuadColor(buffer, quad, color);
      });
      tessellator.draw();
   }
}
