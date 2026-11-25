package com.creativemd.creativecore.client.rendering.model;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.RenderBox;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.SingletonList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import org.lwjgl.util.Color;

public class CreativeModelPipeline {
   public static BlockModelRenderer renderer;
   private static BlockColors blockColors;
   private static Class ambientOcclusionFaceClass;
   private static Constructor ambientOcclusionFaceClassConstructor;
   private static Method renderQuadsSmoothMethod;
   private static Method renderQuadsFlatMethod;
   private static Field vertexColorMultiplierField;
   private static Field lighterFlatField;
   private static Field lighterSmoothField;
   private static Field wrFlatField;
   private static Field wrSmoothField;
   private static Method setBufferMethod;
   private static Field blockInfoField;
   private static Field fullBlockInfoField;
   private static Field sArrayBlockInfoField;
   private static Field bArrayBlockInfoField;
   private static Method combineBlockInfoMethod;
   public static ThreadLocal<SingletonList<BakedQuad>> singletonList;

   public CreativeModelPipeline() {
   }

   public static Object createAmbientOcclusionFace() {
      try {
         return ambientOcclusionFaceClassConstructor.newInstance(renderer);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var1) {
         var1.printStackTrace();
         throw new RuntimeException("Could not create ambient occlusion face!");
      }
   }

   public static void renderBlockFaceSmooth(IBlockAccess world, IBlockState state, BlockPos pos, WorldRenderer buffer, BlockRenderLayer layer, List<BakedQuad> quads, float[] afloat, EnumFacing facing, BitSet set, Object ambientOcclusionFace, RenderBox cube) {
      try {
         SingletonList<BakedQuad> list = (SingletonList)singletonList.get();
         if (ForgeModContainer.forgeLightPipelineEnabled) {
            ThreadLocal<VertexBufferConsumer> wrSmooth = (ThreadLocal)wrSmoothField.get(renderer);
            ThreadLocal<VertexLighterSmoothAo> lighterSmooth = (ThreadLocal)lighterSmoothField.get(renderer);
            VertexBufferConsumer newCons = (VertexBufferConsumer)wrSmooth.get();
            if (setBufferMethod == null) {
               newCons = new VertexBufferConsumer(buffer);
               wrSmooth.set(newCons);
            } else {
               setBufferMethod.invoke(newCons, buffer);
            }

            ((VertexLighterSmoothAo)lighterSmooth.get()).setParent(newCons);
            ((VertexBufferConsumer)wrSmooth.get()).setOffset(pos);
            VertexLighterSmoothAo lighter = (VertexLighterSmoothAo)lighterSmooth.get();
            lighter.setWorld(world);
            lighter.setState(state);
            lighter.setBlockPos(pos);
            if (cube.emissive) {
               BlockInfo info = (BlockInfo)blockInfoField.get(lighter);
               info.updateShift();
               boolean[][][] t = info.getTranslucent();
               int[][][] s = (int[][][])sArrayBlockInfoField.get(info);
               int[][][] b = (int[][][])bArrayBlockInfoField.get(info);
               float[][][] ao = info.getAo();
               float[][][][] skyLight = info.getSkyLight();
               float[][][][] blockLight = info.getBlockLight();

               for(int x = 0; x <= 2; ++x) {
                  for(int y = 0; y <= 2; ++y) {
                     for(int z = 0; z <= 2; ++z) {
                        t[x][y][z] = true;
                        int brightness = 240;
                        s[x][y][z] = brightness >> 20 & 15;
                        b[x][y][z] = brightness >> 4 & 15;
                        ao[x][y][z] = 1.0F;
                     }
                  }
               }

               for(EnumFacing side : EnumFacing.VALUES) {
                  int x = side.getFrontOffsetX() + 1;
                  int y = side.getFrontOffsetY() + 1;
                  int z = side.getFrontOffsetZ() + 1;
                  s[x][y][z] = Math.max(s[1][1][1] - 1, s[x][y][z]);
                  b[x][y][z] = Math.max(b[1][1][1] - 1, b[x][y][z]);
               }

               for(int x = 0; x < 2; ++x) {
                  for(int y = 0; y < 2; ++y) {
                     for(int z = 0; z < 2; ++z) {
                        int x1 = x * 2;
                        int y1 = y * 2;
                        int z1 = z * 2;
                        int sxyz = s[x1][y1][z1];
                        int bxyz = b[x1][y1][z1];
                        boolean txyz = t[x1][y1][z1];
                        int sxz = s[x1][1][z1];
                        int sxy = s[x1][y1][1];
                        int syz = s[1][y1][z1];
                        int bxz = b[x1][1][z1];
                        int bxy = b[x1][y1][1];
                        int byz = b[1][y1][z1];
                        boolean txz = t[x1][1][z1];
                        boolean txy = t[x1][y1][1];
                        boolean tyz = t[1][y1][z1];
                        int sx = s[x1][1][1];
                        int sy = s[1][y1][1];
                        int sz = s[1][1][z1];
                        int bx = b[x1][1][1];
                        int by = b[1][y1][1];
                        int bz = b[1][1][z1];
                        boolean tx = t[x1][1][1];
                        boolean ty = t[1][y1][1];
                        boolean tz = t[1][1][z1];
                        skyLight[0][x][y][z] = ((Float)combineBlockInfoMethod.invoke(info, sx, sxz, sxy, !txz && !txy ? sx : sxyz, tx, txz, txy, !txz && !txy ? tx : txyz)).floatValue();
                        blockLight[0][x][y][z] = ((Float)combineBlockInfoMethod.invoke(info, bx, bxz, bxy, !txz && !txy ? bx : bxyz, tx, txz, txy, !txz && !txy ? tx : txyz)).floatValue();
                        skyLight[1][x][y][z] = ((Float)combineBlockInfoMethod.invoke(info, sy, sxy, syz, !txy && !tyz ? sy : sxyz, ty, txy, tyz, !txy && !tyz ? ty : txyz)).floatValue();
                        blockLight[1][x][y][z] = ((Float)combineBlockInfoMethod.invoke(info, by, bxy, byz, !txy && !tyz ? by : bxyz, ty, txy, tyz, !txy && !tyz ? ty : txyz)).floatValue();
                        skyLight[2][x][y][z] = ((Float)combineBlockInfoMethod.invoke(info, sz, syz, sxz, !tyz && !txz ? sz : sxyz, tz, tyz, txz, !tyz && !txz ? tz : txyz)).floatValue();
                        blockLight[2][x][y][z] = ((Float)combineBlockInfoMethod.invoke(info, bz, byz, bxz, !tyz && !txz ? bz : bxyz, tz, tyz, txz, !tyz && !txz ? tz : txyz)).floatValue();
                     }
                  }
               }
            } else {
               lighter.updateBlockInfo();
            }

            fullBlockInfoField.setBoolean(blockInfoField.get(lighter), false);

            for(int i = 0; i < quads.size(); ++i) {
               ((BakedQuad)quads.get(i)).pipe(lighter);
               overwriteColor(world, state, pos, buffer, layer, (BakedQuad)quads.get(i), cube, (Object)null, lighter);
            }
         } else {
            for(int i = 0; i < quads.size(); ++i) {
               SingletonList<BakedQuad> singleQuad = quads instanceof SingletonList ? (SingletonList)quads : list.setElement(quads.get(i));
               if (FMLClientHandler.instance().hasOptifine()) {
                  renderQuadsSmoothMethod.invoke(renderer, world, state, pos, buffer, singleQuad, ambientOcclusionFace);
               } else {
                  renderQuadsSmoothMethod.invoke(renderer, world, state, pos, buffer, singleQuad, afloat, set, ambientOcclusionFace);
               }

               overwriteColor(world, state, pos, buffer, layer, singleQuad.get(0), cube, ambientOcclusionFace, (VertexLighterFlat)null);
            }
         }

         list.setElement((Object)null);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var50) {
         var50.printStackTrace();
      }

   }

   public static void renderBlockFaceFlat(IBlockAccess world, IBlockState state, BlockPos pos, WorldRenderer buffer, BlockRenderLayer layer, List<BakedQuad> quads, EnumFacing facing, BitSet set, RenderBox cube, Object renderEnv) {
      int light = state.getPackedLightmapCoords(world, pos.offset(facing));

      try {
         if (ForgeModContainer.forgeLightPipelineEnabled) {
            ThreadLocal<VertexBufferConsumer> wrFlat = (ThreadLocal)wrFlatField.get(renderer);
            ThreadLocal<VertexLighterFlat> lighterFlat = (ThreadLocal)lighterFlatField.get(renderer);
            VertexBufferConsumer newCons = (VertexBufferConsumer)wrFlat.get();
            if (setBufferMethod == null) {
               newCons = new VertexBufferConsumer(buffer);
               wrFlat.set(newCons);
            } else {
               setBufferMethod.invoke(newCons, buffer);
            }

            ((VertexLighterFlat)lighterFlat.get()).setParent(newCons);
            ((VertexBufferConsumer)wrFlat.get()).setOffset(pos);
            VertexLighterFlat lighter = (VertexLighterFlat)lighterFlat.get();
            lighter.setWorld(world);
            lighter.setState(state);
            lighter.setBlockPos(pos);
            lighter.updateBlockInfo();
            if (cube.emissive) {
               BlockInfo info = (BlockInfo)blockInfoField.get(lighter);
               info.updateShift();
               int[] packed = info.getPackedLight();

               for(int i = 0; i < packed.length; ++i) {
                  packed[0] = 240;
               }
            } else {
               lighter.updateBlockInfo();
            }

            fullBlockInfoField.setBoolean(blockInfoField.get(lighter), false);

            for(int i = 0; i < quads.size(); ++i) {
               ((BakedQuad)quads.get(i)).pipe(lighter);
               overwriteColor(world, state, pos, buffer, layer, (BakedQuad)quads.get(i), cube, (Object)null, (VertexLighterFlat)null);
            }
         } else {
            for(int i = 0; i < quads.size(); ++i) {
               SingletonList<BakedQuad> list = (SingletonList)singletonList.get();
               List<BakedQuad> singleQuad = (List<BakedQuad>)(quads instanceof SingletonList ? quads : list.setElement(quads.get(i)));
               if (FMLClientHandler.instance().hasOptifine()) {
                  renderQuadsFlatMethod.invoke(renderer, world, state, pos, light, false, buffer, singleQuad, renderEnv);
               } else {
                  renderQuadsFlatMethod.invoke(renderer, world, state, pos, light, false, buffer, singleQuad, set);
               }

               overwriteColor(world, state, pos, buffer, layer, (BakedQuad)singleQuad.get(0), cube, (Object)null, (VertexLighterFlat)null);
               list.setElement((Object)null);
            }
         }
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var18) {
         var18.printStackTrace();
      }

   }

   public static void overwriteColor(IBlockAccess world, IBlockState state, BlockPos pos, WorldRenderer buffer, BlockRenderLayer layer, BakedQuad quad, RenderBox cube, Object ambientOcclusionFace, VertexLighterFlat lighter) {
      if (FMLClientHandler.instance().hasOptifine() && ambientOcclusionFace != null) {
         ambientOcclusionFace = OptifineHelper.getAoFace(ambientOcclusionFace);
      }

      float[] vertexColorMultiplier = null;

      try {
         vertexColorMultiplier = ambientOcclusionFace != null ? (float[])((float[])vertexColorMultiplierField.get(ambientOcclusionFace)) : null;
      } catch (IllegalAccessException | IllegalArgumentException var22) {
         var22.printStackTrace();
      }

      VertexFormat format = buffer.getVertexFormat();
      int tint = quad.getTintIndex();
      int multiplier = -1;
      if (OptifineHelper.isActive()) {
         multiplier = OptifineHelper.getColorMultiplier(quad, state, world, pos);
         if (multiplier != -1) {
            tint = 0;
         }
      }

      if (tint == -1 || cube.color != -1 && !ColorUtils.isWhite(cube.color)) {
         if (layer == BlockRenderLayer.CUTOUT_MIPPED && state.getBlock().getBlockLayer() != BlockRenderLayer.CUTOUT_MIPPED) {
            tint = 0;
         }

         if (layer != BlockRenderLayer.CUTOUT_MIPPED || state.getBlock().canRenderInLayer(state, BlockRenderLayer.CUTOUT)) {
            tint = 0;
         }

         multiplier = cube.color;
         if (EntityRenderer.anaglyphEnable) {
            multiplier = TextureUtil.anaglyphColor(multiplier);
         }
      } else {
         if (multiplier == -1) {
            multiplier = blockColors.colorMultiplier(state, world, pos, tint);
         }

         Color tempColor = ColorUtils.IntToRGBA(multiplier);
         if (cube.color != -1) {
            tempColor.setAlpha(ColorUtils.getAlpha(cube.color));
         } else {
            tempColor.setAlpha(255);
         }

         multiplier = ColorUtils.RGBAToInt(tempColor);
      }

      if (tint != -1 && cube.color != -1) {
         float r = (float)(multiplier >> 16 & 255) / 255.0F;
         float g = (float)(multiplier >> 8 & 255) / 255.0F;
         float b = (float)(multiplier & 255) / 255.0F;
         int a = ColorUtils.getAlpha(multiplier);
         if (vertexColorMultiplier == null && lighter == null) {
            if (quad.shouldApplyDiffuseLighting()) {
               float diffuse = LightUtil.diffuseLight(quad.getFace());
               r *= diffuse;
               g *= diffuse;
               b *= diffuse;
            }

            buffer.putColorRGBA(buffer.getColorIndex(4), (int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), a);
            buffer.putColorRGBA(buffer.getColorIndex(3), (int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), a);
            buffer.putColorRGBA(buffer.getColorIndex(2), (int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), a);
            buffer.putColorRGBA(buffer.getColorIndex(1), (int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), a);
         } else if (ForgeModContainer.forgeLightPipelineEnabled) {
            try {
               BlockInfo info = (BlockInfo)blockInfoField.get(lighter);
               int oldMultiplier = info.getColorMultiplier(quad.getTintIndex());

               for(int i = 1; i <= 4; ++i) {
                  int exisiting = BufferBuilderUtils.get(buffer, buffer.getColorIndex(i));
                  float ao = ColorUtils.getRed(oldMultiplier) > 0 ? (float)ColorUtils.getRed(exisiting) / (float)ColorUtils.getRed(oldMultiplier) : (ColorUtils.getGreen(oldMultiplier) > 0 ? (float)ColorUtils.getGreen(exisiting) / (float)ColorUtils.getGreen(oldMultiplier) : (float)ColorUtils.getBlue(exisiting) / (float)ColorUtils.getBlue(oldMultiplier));
                  if (!Float.isFinite(ao)) {
                     ao = 1.0F;
                  }

                  buffer.putColorRGBA(buffer.getColorIndex(i), (int)(r * ao * 255.0F), (int)(g * ao * 255.0F), (int)(b * ao * 255.0F), a);
               }
            } catch (IllegalAccessException | IllegalArgumentException var23) {
               var23.printStackTrace();
            }
         } else {
            buffer.putColorRGBA(buffer.getColorIndex(4), (int)(vertexColorMultiplier[0] * r * 255.0F), (int)(vertexColorMultiplier[0] * g * 255.0F), (int)(vertexColorMultiplier[0] * b * 255.0F), a);
            buffer.putColorRGBA(buffer.getColorIndex(3), (int)(vertexColorMultiplier[1] * r * 255.0F), (int)(vertexColorMultiplier[1] * g * 255.0F), (int)(vertexColorMultiplier[1] * b * 255.0F), a);
            buffer.putColorRGBA(buffer.getColorIndex(2), (int)(vertexColorMultiplier[2] * r * 255.0F), (int)(vertexColorMultiplier[2] * g * 255.0F), (int)(vertexColorMultiplier[2] * b * 255.0F), a);
            buffer.putColorRGBA(buffer.getColorIndex(1), (int)(vertexColorMultiplier[3] * r * 255.0F), (int)(vertexColorMultiplier[3] * g * 255.0F), (int)(vertexColorMultiplier[3] * b * 255.0F), a);
         }
      }

   }

   static {
      try {
         renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
         blockColors = Minecraft.getMinecraft().getBlockColors();

         for(Class<?> clazz : BlockModelRenderer.class.getDeclaredClasses()) {
            if (clazz.getName().equals("net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace")) {
               ambientOcclusionFaceClass = clazz;
               break;
            }
         }

         ambientOcclusionFaceClassConstructor = ReflectionHelper.findConstructor(ambientOcclusionFaceClass, new Class[]{BlockModelRenderer.class});
         vertexColorMultiplierField = ReflectionHelper.findField(ambientOcclusionFaceClass, new String[]{"vertexColorMultiplier", "vertexColorMultiplier"});
         if (FMLClientHandler.instance().hasOptifine()) {
            renderQuadsSmoothMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsSmooth", "renderQuadsSmooth", new Class[]{IBlockAccess.class, IBlockState.class, BlockPos.class, WorldRenderer.class, List.class, OptifineHelper.renderEnvClass});
            renderQuadsFlatMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsFlat", "renderQuadsFlat", new Class[]{IBlockAccess.class, IBlockState.class, BlockPos.class, Integer.TYPE, Boolean.TYPE, WorldRenderer.class, List.class, OptifineHelper.renderEnvClass});
         } else {
            renderQuadsSmoothMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsSmooth", "renderQuadsSmooth", new Class[]{IBlockAccess.class, IBlockState.class, BlockPos.class, WorldRenderer.class, List.class, float[].class, BitSet.class, ambientOcclusionFaceClass});
            renderQuadsFlatMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsFlat", "renderQuadsFlat", new Class[]{IBlockAccess.class, IBlockState.class, BlockPos.class, Integer.TYPE, Boolean.TYPE, WorldRenderer.class, List.class, BitSet.class});
         }

         lighterFlatField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, new String[]{"lighterFlat"});
         lighterSmoothField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, new String[]{"lighterSmooth"});
         wrFlatField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, new String[]{"wrFlat", "consumerFlat"});
         wrSmoothField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, new String[]{"wrSmooth", "consumerSmooth"});

         try {
            setBufferMethod = ReflectionHelper.findMethod(VertexBufferConsumer.class, "setBuffer", "setBuffer", new Class[]{WorldRenderer.class});
         } catch (UnableToFindMethodException var4) {
            setBufferMethod = null;
         }

         blockInfoField = ReflectionHelper.findField(VertexLighterFlat.class, new String[]{"blockInfo"});
         fullBlockInfoField = ReflectionHelper.findField(BlockInfo.class, new String[]{"full"});
         sArrayBlockInfoField = ReflectionHelper.findField(BlockInfo.class, new String[]{"s"});
         bArrayBlockInfoField = ReflectionHelper.findField(BlockInfo.class, new String[]{"b"});
         combineBlockInfoMethod = ReflectionHelper.findMethod(BlockInfo.class, "combine", "combine", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE});
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      singletonList = ThreadLocal.withInitial(() -> {
         return new SingletonList((Object)null);
      });
   }
}
