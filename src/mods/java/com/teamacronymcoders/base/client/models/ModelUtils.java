package com.teamacronymcoders.base.client.models;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.obj.OBJModel.Normal;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class ModelUtils {
   private static float[] alphaNoFading = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private static final ItemCameraTransforms defaultTransforms = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75.0F, 45.0F, 0.0F), new Vector3f(0.0F, 0.25F, 0.0F), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(75.0F, 45.0F, 0.0F), new Vector3f(0.0F, 0.15625F, 0.0F), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(0.0F, 45.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.4F, 0.4F, 0.4F)), new ItemTransformVec3f(new Vector3f(0.0F, 225.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.4F, 0.4F, 0.4F)), new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 1.0F)), new ItemTransformVec3f(new Vector3f(30.0F, 225.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.625F, 0.625F, 0.625F)), new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.1875F, 0.0F), new Vector3f(0.25F, 0.25F, 0.25F)), new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.5F, 0.5F, 0.5F)));

   public ModelUtils() {
   }

   public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing, EaglerTextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert) {
      return createBakedQuad(format, vertices, facing, sprite, uvs, colour, invert, alphaNoFading);
   }

   public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing, EaglerTextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert, float[] alpha) {
      return createBakedQuad(format, vertices, facing, sprite, uvs, colour, invert, alpha, false, (BlockPos)null);
   }

   public static BakedQuad createBakedQuad(VertexFormat format, Vector3f[] vertices, EnumFacing facing, EaglerTextureAtlasSprite sprite, double[] uvs, float[] colour, boolean invert, float[] alpha, boolean smartLighting, BlockPos basePos) {
      Builder builder = new Builder(format);
      builder.setQuadOrientation(facing);
      builder.setTexture(sprite);
      Normal faceNormal = new Normal((float)facing.getDirectionVec().getX(), (float)facing.getDirectionVec().getY(), (float)facing.getDirectionVec().getZ());
      int vId = invert ? 3 : 0;
      int u = vId > 1 ? 2 : 0;
      putVertexData(format, builder, vertices[vId], faceNormal, uvs[u], uvs[1], sprite, colour, alpha[invert ? 3 : 0]);
      vId = invert ? 2 : 1;
      u = vId > 1 ? 2 : 0;
      putVertexData(format, builder, vertices[invert ? 2 : 1], faceNormal, uvs[u], uvs[3], sprite, colour, alpha[invert ? 2 : 1]);
      vId = invert ? 1 : 2;
      u = vId > 1 ? 2 : 0;
      putVertexData(format, builder, vertices[invert ? 1 : 2], faceNormal, uvs[u], uvs[3], sprite, colour, alpha[invert ? 1 : 2]);
      vId = invert ? 1 : 3;
      u = vId > 1 ? 2 : 0;
      putVertexData(format, builder, vertices[invert ? 0 : 3], faceNormal, uvs[u], uvs[1], sprite, colour, alpha[invert ? 0 : 3]);
      BakedQuad tmp = builder.build();
      return (BakedQuad)(smartLighting ? new SmartLightingQuad(tmp.getVertexData(), -1, facing, sprite, format, basePos) : tmp);
   }

   protected static void putVertexData(VertexFormat format, Builder builder, Vector3f pos, Normal faceNormal, double u, double v, EaglerTextureAtlasSprite sprite, float[] colour, float alpha) {
      for(int e = 0; e < format.getElementCount(); ++e) {
         switch(format.getElement(e).getUsage()) {
         case POSITION:
            builder.put(e, new float[]{pos.getX(), pos.getY(), pos.getZ(), 0.0F});
            break;
         case COLOR:
            float d = 1.0F;
            builder.put(e, new float[]{d * colour[0], d * colour[1], d * colour[2], 1.0F * colour[3] * alpha});
            break;
         case UV:
            if (sprite == null) {
               sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }

            builder.put(e, new float[]{sprite.getInterpolatedU(u), sprite.getInterpolatedV(v), 0.0F, 1.0F});
            break;
         case NORMAL:
            builder.put(e, new float[]{faceNormal.x, faceNormal.y, faceNormal.z, 0.0F});
            break;
         default:
            builder.put(e, new float[0]);
         }
      }

   }

   public static EaglerTextureAtlasSprite getBlockSprite(ResourceLocation path) {
      if (!path.getResourcePath().contains("blocks/")) {
         path = new ResourceLocation(path.getResourceDomain(), "blocks/" + path.getResourcePath());
      }

      return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(path.toString());
   }

   public static ItemCameraTransforms getDefaultTransforms() {
      return defaultTransforms;
   }

   public static List<BakedQuad> bakeQuads(ResourceLocation sprite, Color color) {
      return bakeQuads(getBlockSprite(sprite), color);
   }

   public static List<BakedQuad> bakeQuads(EaglerTextureAtlasSprite sprite, Color color) {
      EaglerTextureAtlasSprite[] sprites = new EaglerTextureAtlasSprite[6];
      Arrays.fill(sprites, sprite);
      return bakeQuads(sprites, color);
   }

   public static List<BakedQuad> bakeQuads(EaglerTextureAtlasSprite[] sprites, Color color) {
      List<BakedQuad> quads = Lists.newArrayListWithExpectedSize(6);
      float[] colorArray;
      if (color != null) {
         colorArray = color.getRGBComponents(new float[4]);
      } else {
         colorArray = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      }

      Vector3f[] vertices = new Vector3f[]{new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 0.0F)};
      quads.add(createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.DOWN, sprites[0], new double[]{0.0D, 16.0D, 16.0D, 0.0D}, colorArray, true));
      vertices = new Vector3f[]{new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 0.0F)};
      quads.add(createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.UP, sprites[1], new double[]{0.0D, 0.0D, 16.0D, 16.0D}, colorArray, false));
      vertices = new Vector3f[]{new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F)};
      quads.add(createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.NORTH, sprites[2], new double[]{0.0D, 16.0D, 16.0D, 0.0D}, colorArray, true));
      vertices = new Vector3f[]{new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(0.0F, 0.0F, 1.0F)};
      quads.add(createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.SOUTH, sprites[3], new double[]{16.0D, 16.0D, 0.0D, 0.0D}, colorArray, false));
      vertices = new Vector3f[]{new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(0.0F, 0.0F, 1.0F)};
      quads.add(createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.WEST, sprites[4], new double[]{0.0D, 16.0D, 16.0D, 0.0D}, colorArray, true));
      vertices = new Vector3f[]{new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F)};
      quads.add(createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.EAST, sprites[5], new double[]{16.0D, 16.0D, 0.0D, 0.0D}, colorArray, false));
      return quads;
   }
}
