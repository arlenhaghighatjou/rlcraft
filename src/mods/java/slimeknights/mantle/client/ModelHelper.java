package slimeknights.mantle.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class ModelHelper {
   public static final IModelState DEFAULT_ITEM_STATE;
   public static final IModelState DEFAULT_TOOL_STATE;
   public static final TRSRTransformation BLOCK_THIRD_PERSON_RIGHT = get(0.0F, 2.5F, 0.0F, 75.0F, 45.0F, 0.0F, 0.375F);
   public static final TRSRTransformation BLOCK_THIRD_PERSON_LEFT = get(0.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.4F);

   public ModelHelper() {
   }

   public static EaglerTextureAtlasSprite getTextureFromBlock(Block block, int meta) {
      IBlockState state = block.getStateFromMeta(meta);
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
   }

   public static EaglerTextureAtlasSprite getTextureFromBlockstate(IBlockState state) {
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
   }

   public static BakedQuad colorQuad(int color, BakedQuad quad) {
      ModelHelper.ColorTransformer transformer = new ModelHelper.ColorTransformer(color, quad.getFormat());
      quad.pipe(transformer);
      return transformer.build();
   }

   private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
      return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), (Quat4f)null));
   }

   static {
      Builder<IModelPart, TRSRTransformation> builder = ImmutableMap.builder();
      builder.put(TransformType.GROUND, get(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F));
      builder.put(TransformType.HEAD, get(0.0F, 13.0F, 7.0F, 0.0F, 180.0F, 0.0F, 1.0F));
      builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.55F));
      builder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(0.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.55F));
      builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13F, 3.2F, 1.13F, 0.0F, -90.0F, 25.0F, 0.68F));
      builder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(1.13F, 3.2F, 1.13F, 0.0F, 90.0F, -25.0F, 0.68F));
      DEFAULT_ITEM_STATE = new SimpleModelState(builder.build());
      builder = ImmutableMap.builder();
      builder.put(TransformType.GROUND, get(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F));
      builder.put(TransformType.HEAD, get(0.0F, 13.0F, 7.0F, 0.0F, 180.0F, 0.0F, 1.0F));
      builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0.0F, 4.0F, 0.5F, 0.0F, -90.0F, 55.0F, 0.85F));
      builder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(0.0F, 4.0F, 0.5F, 0.0F, 90.0F, -55.0F, 0.85F));
      builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13F, 3.2F, 1.13F, 0.0F, -90.0F, 25.0F, 0.68F));
      builder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(1.13F, 3.2F, 1.13F, 0.0F, 90.0F, -25.0F, 0.68F));
      DEFAULT_TOOL_STATE = new SimpleModelState(builder.build());
   }

   private static class ColorTransformer extends VertexTransformer {
      private final float r;
      private final float g;
      private final float b;
      private final float a;

      public ColorTransformer(int color, VertexFormat format) {
         super(new net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder(format));
         int a = color >> 24;
         if (a == 0) {
            a = 255;
         }

         int r = color >> 16 & 255;
         int g = color >> 8 & 255;
         int b = color >> 0 & 255;
         this.r = (float)r / 255.0F;
         this.g = (float)g / 255.0F;
         this.b = (float)b / 255.0F;
         this.a = (float)a / 255.0F;
      }

      public void put(int element, float... data) {
         EnumUsage usage = this.parent.getVertexFormat().getElement(element).getUsage();
         if (usage == EnumUsage.COLOR && data.length >= 4) {
            data[0] = this.r;
            data[1] = this.g;
            data[2] = this.b;
            data[3] = this.a;
         }

         super.put(element, data);
      }

      public UnpackedBakedQuad build() {
         return ((net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder)this.parent).build();
      }
   }
}
