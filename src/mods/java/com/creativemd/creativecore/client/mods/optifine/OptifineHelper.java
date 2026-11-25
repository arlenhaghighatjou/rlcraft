package com.creativemd.creativecore.client.mods.optifine;

import com.creativemd.creativecore.CreativeCore;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class OptifineHelper {
   private static boolean active = FMLClientHandler.instance().hasOptifine();
   private static Minecraft mc = Minecraft.getMinecraft();
   private static boolean ltInstalled = Loader.isModLoaded("littletiles");
   private static ThreadLocal<Object> renderEnv;
   private static boolean loadedOptifineReflections = false;
   private static Method getCustomizedQuads;
   private static Method getCustomizedModel;
   public static Class renderEnvClass;
   private static Class blockModelCustomizer;
   private static Constructor<?> renderEnvConstructor;
   private static Method resetEnv;
   private static Method getEnv;
   private static Method getAoFace;
   private static Method getColorMultiplier;
   private static Method isAnisotropicFiltering;
   private static Method isAntialiasing;
   private static Method isFastRender;
   private static Field isEmissive;
   private static Method getQuadEmissive;
   private static boolean newVersion = false;
   private static Method isShadersMethod = null;
   private static Method isRenderRegions = null;
   private static Field regionX;
   private static Field regionZ;

   public OptifineHelper() {
   }

   private static void loadOptifineReflections() {
      try {
         loadedOptifineReflections = true;
         renderEnvClass = Class.forName("net.optifine.render.RenderEnv");

         try {
            renderEnvConstructor = renderEnvClass.getConstructor(IBlockAccess.class, IBlockState.class, BlockPos.class);
            resetEnv = ReflectionHelper.findMethod(renderEnvClass, "reset", "reset", new Class[]{IBlockAccess.class, IBlockState.class, BlockPos.class});
            getEnv = ReflectionHelper.findMethod(WorldRenderer.class, "getRenderEnv", "getRenderEnv", new Class[]{IBlockAccess.class, IBlockState.class, BlockPos.class});
         } catch (NoSuchMethodException var2) {
            newVersion = true;
            renderEnvConstructor = renderEnvClass.getConstructor(IBlockState.class, BlockPos.class);
            resetEnv = ReflectionHelper.findMethod(renderEnvClass, "reset", "reset", new Class[]{IBlockState.class, BlockPos.class});
            getEnv = ReflectionHelper.findMethod(WorldRenderer.class, "getRenderEnv", "getRenderEnv", new Class[]{IBlockState.class, BlockPos.class});
         }

         getAoFace = ReflectionHelper.findMethod(renderEnvClass, "getAoFace", "getAoFace", new Class[0]);
         blockModelCustomizer = Class.forName("net.optifine.model.BlockModelCustomizer");
         getCustomizedModel = ReflectionHelper.findMethod(blockModelCustomizer, "getRenderModel", "getRenderModel", new Class[]{IBakedModel.class, IBlockState.class, renderEnvClass});
         getCustomizedQuads = ReflectionHelper.findMethod(blockModelCustomizer, "getRenderQuads", "getRenderQuads", new Class[]{List.class, IBlockAccess.class, IBlockState.class, BlockPos.class, EnumFacing.class, BlockRenderLayer.class, Long.TYPE, renderEnvClass});
         Class customColorsClass = Class.forName("net.optifine.CustomColors");
         getColorMultiplier = ReflectionHelper.findMethod(customColorsClass, "getColorMultiplier", "getColorMultiplier", new Class[]{BakedQuad.class, IBlockState.class, IBlockAccess.class, BlockPos.class, renderEnvClass});
         renderEnv = ThreadLocal.withInitial(new Supplier<Object>() {
            public Object get() {
               try {
                  return OptifineHelper.newVersion ? OptifineHelper.renderEnvConstructor.newInstance(null, null) : OptifineHelper.renderEnvConstructor.newInstance(null, null, null);
               } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var2) {
                  throw new RuntimeException(var2);
               }
            }
         });
         Class configClass = Class.forName("Config");
         isShadersMethod = configClass.getMethod("isShaders");
         isRenderRegions = configClass.getMethod("isRenderRegions");
         isAnisotropicFiltering = configClass.getMethod("isAnisotropicFiltering");
         isAntialiasing = configClass.getMethod("isAntialiasing");
         isFastRender = configClass.getMethod("isFastRender");
         regionX = ReflectionHelper.findField(RenderChunk.class, new String[]{"regionX"});
         regionZ = ReflectionHelper.findField(RenderChunk.class, new String[]{"regionZ"});
         isEmissive = ReflectionHelper.findField(EaglerTextureAtlasSprite.class, new String[]{"isEmissive"});
         getQuadEmissive = ReflectionHelper.findMethod(BakedQuad.class, "getQuadEmissive", "getQuadEmissive", new Class[0]);
      } catch (NoSuchMethodException | SecurityException | ClassNotFoundException var3) {
         active = false;
         var3.printStackTrace();
      }

   }

   public static IBakedModel getRenderModel(IBakedModel model, IBlockAccess world, IBlockState state, BlockPos pos) {
      if (!active) {
         return model;
      } else {
         try {
            return (IBakedModel)getCustomizedModel.invoke((Object)null, model, state, getEnv(world, state, pos));
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
            var5.printStackTrace();
            throw new RuntimeException(var5);
         }
      }
   }

   public static Object getEnv(WorldRenderer builder, IBlockAccess world, IBlockState state, BlockPos pos) {
      try {
         return newVersion ? getEnv.invoke(builder, state, pos) : getEnv.invoke(builder, world, state, pos);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public static Object getEnv(IBlockAccess world, IBlockState state, BlockPos pos) {
      try {
         Object env = renderEnv.get();
         if (newVersion) {
            resetEnv.invoke(env, state, pos);
         } else {
            resetEnv.invoke(env, world, state, pos);
         }

         return env;
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
         var4.printStackTrace();
         throw new RuntimeException(var4);
      }
   }

   public static List<BakedQuad> getRenderQuads(List<BakedQuad> quads, IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing, BlockRenderLayer layer, long rand) {
      if (active && world != null && layer != null && CreativeCore.config.useOptifineCompat) {
         try {
            if (ltInstalled) {
               quads = (List)getCustomizedQuads.invoke((Object)null, quads, world, state, pos, facing, layer, rand, getEnv(world, state, pos));
            }

            int size = quads.size();

            for(int i = 0; i < size; ++i) {
               BakedQuad emissive = (BakedQuad)getQuadEmissive.invoke(quads.get(i));
               if (emissive != null) {
                  quads.add(emissive);
               }
            }

            return quads;
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var11) {
            throw new RuntimeException(var11);
         }
      } else {
         return quads;
      }
   }

   public static int getColorMultiplier(BakedQuad quad, IBlockState state, IBlockAccess world, BlockPos pos) {
      try {
         return ((Integer)getColorMultiplier.invoke((Object)null, quad, state, world, pos, getEnv(world, state, pos))).intValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
         var5.printStackTrace();
         throw new RuntimeException(var5);
      }
   }

   public static Object getAoFace(Object renderEnv) {
      try {
         return getAoFace.invoke(renderEnv);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static boolean isActive() {
      return active;
   }

   public static boolean isShaders() {
      try {
         return ((Boolean)isShadersMethod.invoke((Object)null)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var1) {
         var1.printStackTrace();
         return false;
      }
   }

   public static boolean isRenderRegions() {
      try {
         return ((Boolean)isRenderRegions.invoke((Object)null)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var1) {
         var1.printStackTrace();
         return false;
      }
   }

   public static boolean isAnisotropicFiltering() {
      try {
         return ((Boolean)isAnisotropicFiltering.invoke((Object)null)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var1) {
         var1.printStackTrace();
         return false;
      }
   }

   public static boolean isAntialiasing() {
      try {
         return ((Boolean)isAntialiasing.invoke((Object)null)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var1) {
         var1.printStackTrace();
         return false;
      }
   }

   public static boolean isFastRender() {
      try {
         return ((Boolean)isFastRender.invoke((Object)null)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var1) {
         var1.printStackTrace();
         return false;
      }
   }

   public static int getRenderChunkRegionX(RenderChunk chunk) {
      try {
         return regionX.getInt(chunk);
      } catch (IllegalAccessException | IllegalArgumentException var2) {
         var2.printStackTrace();
         return -1;
      }
   }

   public static int getRenderChunkRegionZ(RenderChunk chunk) {
      try {
         return regionZ.getInt(chunk);
      } catch (IllegalAccessException | IllegalArgumentException var2) {
         var2.printStackTrace();
         return -1;
      }
   }

   public static boolean isEmissive(EaglerTextureAtlasSprite sprite) {
      if (!active) {
         return false;
      } else {
         try {
            return isEmissive.getBoolean(sprite);
         } catch (IllegalAccessException | IllegalArgumentException var2) {
            var2.printStackTrace();
            return false;
         }
      }
   }

   public static BakedQuad getQuadEmissive(BakedQuad quad) {
      if (!active) {
         return quad;
      } else {
         try {
            BakedQuad emissive = (BakedQuad)getQuadEmissive.invoke(quad);
            if (emissive != null) {
               return emissive;
            }
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
            var2.printStackTrace();
         }

         return quad;
      }
   }

   static {
      if (active) {
         loadOptifineReflections();
      }

   }
}
