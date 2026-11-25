package me.ichun.mods.ichunutil.client.render;

import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.ARBShaderObjects;

public final class ShaderHelper {
   private static final int VERT = 35633;
   private static final int FRAG = 35632;
   private static TIntFloatHashMap prevTime = new TIntFloatHashMap();
   private static TIntIntHashMap timeUniformLocation = new TIntIntHashMap();

   public ShaderHelper() {
   }

   public static void useShader(int shader, ShaderHelper.IShaderCallback callback) {
      if (supportsShaders()) {
         ARBShaderObjects.glUseProgramObjectARB(shader);
         if (shader != 0) {
            float frameTime = (float)iChunUtil.eventHandlerClient.ticks + iChunUtil.eventHandlerClient.renderTick;
            boolean newFrame = frameTime != prevTime.get(shader);
            if (!timeUniformLocation.containsKey(shader)) {
               timeUniformLocation.put(shader, ARBShaderObjects.glGetUniformLocationARB(shader, "time"));
            }

            if (newFrame && timeUniformLocation.get(shader) >= 0) {
               ARBShaderObjects.glUniform1fARB(timeUniformLocation.get(shader), frameTime);
               prevTime.put(shader, frameTime);
            }

            if (callback != null) {
               callback.call(shader, newFrame);
            }
         }

      }
   }

   public static void useShader(int shader) {
      useShader(shader, (ShaderHelper.IShaderCallback)null);
   }

   public static void releaseShader() {
      useShader(0);
   }

   public static boolean supportsShaders() {
      return OpenGlHelper.shadersSupported;
   }

   public static int createProgram(String vert, String frag) {
      if (!supportsShaders()) {
         return -1;
      } else {
         int vertId = 0;
         int fragId = 0;
         int program = 0;
         if (vert != null) {
            vertId = createShader(vert, 35633);
         }

         if (frag != null) {
            fragId = createShader(frag, 35632);
         }

         program = ARBShaderObjects.glCreateProgramObjectARB();
         if (program == 0) {
            return 0;
         } else {
            if (vert != null) {
               ARBShaderObjects.glAttachObjectARB(program, vertId);
            }

            if (frag != null) {
               ARBShaderObjects.glAttachObjectARB(program, fragId);
            }

            ARBShaderObjects.glLinkProgramARB(program);
            if (ARBShaderObjects.glGetObjectParameteriARB(program, 35714) == 0) {
               iChunUtil.LOGGER.error(getLogInfo(program));
               ARBShaderObjects.glDeleteObjectARB(program);
               return 0;
            } else {
               ARBShaderObjects.glValidateProgramARB(program);
               if (ARBShaderObjects.glGetObjectParameteriARB(program, 35715) == 0) {
                  iChunUtil.LOGGER.error(getLogInfo(program));
                  ARBShaderObjects.glDeleteObjectARB(program);
                  return 0;
               } else {
                  prevTime.put(program, -1.0F);
                  return program;
               }
            }
         }
      }
   }

   private static int createShader(String shaderString, int shaderType) {
      int shader = 0;

      try {
         shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
         if (shader == 0) {
            return 0;
         } else {
            ARBShaderObjects.glShaderSourceARB(shader, shaderString);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
               throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            } else {
               return shader;
            }
         }
      } catch (Exception var4) {
         ARBShaderObjects.glDeleteObjectARB(shader);
         var4.printStackTrace();
         return -1;
      }
   }

   private static String getLogInfo(int obj) {
      return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, 35716));
   }

   public interface IShaderCallback {
      void call(int var1, boolean var2);
   }
}
