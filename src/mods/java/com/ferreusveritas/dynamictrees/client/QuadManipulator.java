package com.ferreusveritas.dynamictrees.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuadManipulator {
   public static final EnumFacing[] everyFace = new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, null};
   private static ModelManager modelManager = null;

   public QuadManipulator() {
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn) {
      return getQuads(modelIn, stateIn, Vec3d.ZERO, everyFace, 0L);
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn, EnumFacing[] sides) {
      return getQuads(modelIn, stateIn, Vec3d.ZERO, sides, 0L);
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn, long rand) {
      return getQuads(modelIn, stateIn, Vec3d.ZERO, everyFace, rand);
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn, Vec3d offset, long rand) {
      return getQuads(modelIn, stateIn, offset, everyFace, rand);
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn, Vec3d offset) {
      return getQuads(modelIn, stateIn, offset, everyFace, 0L);
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn, Vec3d offset, EnumFacing[] sides) {
      return getQuads(modelIn, stateIn, offset, sides, 0L);
   }

   public static List<BakedQuad> getQuads(IBakedModel modelIn, IBlockState stateIn, Vec3d offset, EnumFacing[] sides, long rand) {
      ArrayList<BakedQuad> outQuads = new ArrayList();
      if (stateIn != null) {
         for(EnumFacing side : sides) {
            outQuads.addAll(modelIn.getQuads(stateIn, side, rand));
         }
      }

      return (List<BakedQuad>)(offset.equals(Vec3d.ZERO) ? outQuads : moveQuads(outQuads, offset));
   }

   public static List<BakedQuad> moveQuads(List<BakedQuad> inQuads, Vec3d offset) {
      ArrayList<BakedQuad> outQuads = new ArrayList();

      for(BakedQuad inQuad : inQuads) {
         BakedQuad quadCopy = new BakedQuad((int[])inQuad.getVertexData().clone(), inQuad.getTintIndex(), inQuad.getFace(), inQuad.getSprite(), inQuad.shouldApplyDiffuseLighting(), inQuad.getFormat());
         int[] vertexData = quadCopy.getVertexData();

         for(int i = 0; i < vertexData.length; i += inQuad.getFormat().getIntegerSize()) {
            int pos = 0;

            for(VertexFormatElement vfe : inQuad.getFormat().getElements()) {
               if (vfe.getUsage() == EnumUsage.POSITION) {
                  float x = Float.intBitsToFloat(vertexData[i + pos + 0]);
                  float y = Float.intBitsToFloat(vertexData[i + pos + 1]);
                  float z = Float.intBitsToFloat(vertexData[i + pos + 2]);
                  x = (float)((double)x + offset.x);
                  y = (float)((double)y + offset.y);
                  z = (float)((double)z + offset.z);
                  vertexData[i + pos + 0] = Float.floatToIntBits(x);
                  vertexData[i + pos + 1] = Float.floatToIntBits(y);
                  vertexData[i + pos + 2] = Float.floatToIntBits(z);
                  break;
               }

               pos += vfe.getSize() / 4;
            }
         }

         outQuads.add(quadCopy);
      }

      outQuads.trimToSize();
      return outQuads;
   }

   public static IModel getModelForState(IBlockState state) {
      IModel model = null;

      try {
         model = ModelLoaderRegistry.getModel(getModelLocation(state));
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return model;
   }

   public static ModelManager getModelManager() {
      if (modelManager == null) {
         try {
            Field[] fields = Minecraft.class.getDeclaredFields();

            for(Field f : fields) {
               if (f.getType() == ModelManager.class) {
                  f.setAccessible(true);
                  return modelManager = (ModelManager)f.get(Minecraft.getMinecraft());
               }
            }
         } catch (IllegalArgumentException | IllegalAccessException | SecurityException var5) {
            var5.printStackTrace();
         }
      }

      return modelManager;
   }

   public static ModelResourceLocation getModelLocation(IBlockState state) {
      return (ModelResourceLocation)getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(state.getBlock()).get(state);
   }

   public static ResourceLocation getModelTexture(IModel model, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter, IBlockState state, EnumFacing dir) {
      float[] uvs = getSpriteUVFromBlockState(state, dir);
      if (uvs == null) {
         return null;
      } else {
         List<EaglerTextureAtlasSprite> sprites = new ArrayList();
         float closest = Float.POSITIVE_INFINITY;
         ResourceLocation closestTex = new ResourceLocation("missingno");
         if (model != null) {
            for(ResourceLocation tex : model.getTextures()) {
               EaglerTextureAtlasSprite tas = (EaglerTextureAtlasSprite)bakedTextureGetter.apply(tex);
               float u = tas.getInterpolatedU(8.0D);
               float v = tas.getInterpolatedV(8.0D);
               sprites.add(tas);
               float du = u - uvs[0];
               float dv = v - uvs[1];
               float distSq = du * du + dv * dv;
               if (distSq < closest) {
                  closest = distSq;
                  closestTex = tex;
               }
            }
         }

         return closestTex;
      }
   }

   public static float[] getSpriteUVFromBlockState(IBlockState state, EnumFacing side) {
      IBakedModel bakedModel = getModelManager().getBlockModelShapes().getModelForState(state);
      List<BakedQuad> quads = new ArrayList();
      quads.addAll(bakedModel.getQuads(state, side, 0L));
      quads.addAll(bakedModel.getQuads(state, (EnumFacing)null, 0L));
      Optional<BakedQuad> quad = quads.stream().filter((q) -> {
         return q.getFace() == side;
      }).findFirst();
      if (!quad.isPresent()) {
         System.err.println("Warning: Could not get \"" + side + "\" side quads from blockstate: " + state);
         return null;
      } else {
         float u = 0.0F;
         float v = 0.0F;
         int[] vertexData = ((BakedQuad)quad.get()).getVertexData();
         int numVertices = 0;

         for(int i = 0; i < vertexData.length; i += ((BakedQuad)quad.get()).getFormat().getIntegerSize()) {
            int pos = 0;

            for(VertexFormatElement vfe : ((BakedQuad)quad.get()).getFormat().getElements()) {
               if (vfe.getUsage() == EnumUsage.UV) {
                  u += Float.intBitsToFloat(vertexData[i + pos + 0]);
                  v += Float.intBitsToFloat(vertexData[i + pos + 1]);
               }

               pos += vfe.getSize() / 4;
            }

            ++numVertices;
         }

         return new float[]{u / (float)numVertices, v / (float)numVertices};
      }
   }
}
