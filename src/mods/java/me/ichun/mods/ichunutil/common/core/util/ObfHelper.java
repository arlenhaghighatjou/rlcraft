package me.ichun.mods.ichunutil.common.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.block.BlockCompactPorkchop;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ObfHelper {
   private static final String OBF_VERSION = "1.12.2";
   private static boolean isObfuscated;
   public static final String[] gameProfile = new String[]{"gameProfile", "gameProfile"};
   public static final String[] mainModel = new String[]{"mainModel", "mainModel"};
   public static final String[] resourceDomain = new String[]{"resourceDomain", "namespace"};
   public static final String[] resourcePath = new String[]{"resourcePath", "path"};
   public static final String getHurtSoundObf = "getHurtSound";
   public static final String getHurtSoundDeobf = "getHurtSound";
   public static final String getDeathSoundObf = "func_188593_c";
   public static final String getDeathSoundDeobf = "getDeathSound";
   public static final String setSizeObf = "setSize";
   public static final String setSizeDeobf = "setSize";
   public static final String preRenderCallbackObf = "preRenderCallback";
   public static final String preRenderCallbackDeobf = "preRenderCallback";
   public static final String getEntityTextureObf = "getEntityTexture";
   public static final String getEntityTextureDeobf = "getEntityTexture";

   public ObfHelper() {
   }

   public static void obfWarning() {
      iChunUtil.LOGGER.warn("Error with some obfuscation!");
   }

   public static void detectObfuscation() {
      isObfuscated = true;

      try {
         Field[] fields = Class.forName("net.minecraft.item.ItemBlock").getDeclaredFields();

         for(Field f : fields) {
            f.setAccessible(true);
            if (f.getName().equalsIgnoreCase("block")) {
               isObfuscated = false;
               if (!"1.12.2".equals("1.12.2")) {
                  iChunUtil.LOGGER.warn("ObfHelper strings are not updated!");
                  throw new RuntimeException("Bad iChun! Update obfuscation strings!");
               }

               return;
            }
         }

         BlockCompactPorkchop.class.getDeclaredMethod("setBlockUnbreakable");
      } catch (NoSuchMethodException var5) {
         throw new RuntimeException("You're running the deobf version of iChunUtil in an obfuscated environment! Don't do this!");
      } catch (Exception var6) {
         ;
      }

   }

   public static boolean obfuscated() {
      return isObfuscated;
   }

   @SideOnly(Side.CLIENT)
   public static <T extends RenderLivingBase<V>, V extends EntityLivingBase> void invokePreRenderCallback(T rend, Class clz, V ent, float rendTick) {
      try {
         Method m = clz.getDeclaredMethod(isObfuscated ? "preRenderCallback" : "preRenderCallback", EntityLivingBase.class, Float.TYPE);
         m.setAccessible(true);
         m.invoke(rend, ent, rendTick);
      } catch (NoSuchMethodException var5) {
         if (clz != RenderLivingBase.class) {
            invokePreRenderCallback(rend, clz.getSuperclass(), ent, rendTick);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static <T extends Render<V>, V extends Entity> ResourceLocation getEntityTexture(T rend, Class clz, V ent) {
      try {
         Method m = clz.getDeclaredMethod(isObfuscated ? "getEntityTexture" : "getEntityTexture", Entity.class);
         m.setAccessible(true);
         return (ResourceLocation)m.invoke(rend, ent);
      } catch (NoSuchMethodException var4) {
         if (clz != Render.class) {
            return getEntityTexture(rend, clz.getSuperclass(), ent);
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return null;
   }
}
