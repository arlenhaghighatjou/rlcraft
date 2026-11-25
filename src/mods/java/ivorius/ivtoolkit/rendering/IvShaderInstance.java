package ivorius.ivtoolkit.rendering;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

public class IvShaderInstance {
   public Logger logger;
   private int shaderID = 0;
   private boolean shaderActive = false;
   private TObjectIntMap<String> uniformLocations = new TObjectIntHashMap();

   public int getShaderID() {
      return this.shaderID;
   }

   public IvShaderInstance(Logger logger) {
      this.logger = logger;
   }

   public void trySettingUpShader(String vertexShaderFile, String fragmentShaderFile) {
      if (this.shaderID <= 0) {
         this.registerShader(vertexShaderFile, fragmentShaderFile);
      }

   }

   public void registerShader(String vertexShaderCode, String fragmentShaderCode) {
      this.deleteShader();
      int vertShader = -1;
      int fragShader = -1;

      try {
         if (vertexShaderCode != null) {
            vertShader = this.createShader(vertexShaderCode, OpenGlHelper.GL_VERTEX_SHADER);
         }

         if (fragmentShaderCode != null) {
            fragShader = this.createShader(fragmentShaderCode, OpenGlHelper.GL_FRAGMENT_SHADER);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         return;
      }

      this.shaderID = OpenGlHelper.glCreateProgram();
      if (vertShader > 0) {
         OpenGlHelper.glAttachShader(this.shaderID, vertShader);
         OpenGlHelper.glDeleteShader(vertShader);
      }

      if (fragShader > 0) {
         OpenGlHelper.glAttachShader(this.shaderID, fragShader);
         OpenGlHelper.glDeleteShader(fragShader);
      }

      OpenGlHelper.glLinkProgram(this.shaderID);
      if (OpenGlHelper.glGetProgrami(this.shaderID, OpenGlHelper.GL_LINK_STATUS) == 0) {
         this.logger.error(OpenGlHelper.glGetProgramInfoLog(this.shaderID, 32768));
      }

      IvOpenGLHelper.glValidateProgram(this.shaderID);
      if (OpenGlHelper.glGetProgrami(this.shaderID, IvOpenGLHelper.GL_VALIDATE_STATUS) == 0) {
         this.logger.error(OpenGlHelper.glGetProgramInfoLog(this.shaderID, 32768));
      }

   }

   private int createShader(String shaderCode, int shaderType) throws Exception {
      int shader = 0;

      try {
         shader = OpenGlHelper.glCreateShader(shaderType);
         if (shader == 0) {
            return 0;
         } else {
            byte[] shaderCodeBytes = shaderCode.getBytes();
            ByteBuffer shaderCodeBuf = BufferUtils.createByteBuffer(shaderCodeBytes.length);
            shaderCodeBuf.put(shaderCodeBytes);
            shaderCodeBuf.position(0);
            OpenGlHelper.glShaderSource(shader, shaderCodeBuf);
            OpenGlHelper.glCompileShader(shader);
            if (OpenGlHelper.glGetShaderi(shader, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
               throw new RuntimeException("Error creating shader: " + OpenGlHelper.glGetProgramInfoLog(shader, 32768));
            } else {
               return shader;
            }
         }
      } catch (Exception var6) {
         if (shader != 0) {
            OpenGlHelper.glDeleteShader(shader);
         }

         throw new RuntimeException(var6);
      }
   }

   public boolean useShader() {
      if (this.shaderID <= 0 && !this.shaderActive) {
         return false;
      } else {
         this.shaderActive = true;
         OpenGlHelper.glUseProgram(this.shaderID);
         return true;
      }
   }

   public void stopUsingShader() {
      if (this.shaderID > 0 || !this.shaderActive) {
         OpenGlHelper.glUseProgram(0);
         this.shaderActive = false;
      }
   }

   public boolean isShaderActive() {
      return this.shaderActive;
   }

   public boolean setUniformInts(String key, int... ints) {
      return this.setUniformIntsOfType(key, ints.length, ints);
   }

   public boolean setUniformIntsOfType(String key, int typeLength, int... ints) {
      if (this.shaderID > 0 && this.shaderActive) {
         IntBuffer intBuffer = BufferUtils.createIntBuffer(ints.length);
         intBuffer.put(ints);
         intBuffer.position(0);
         switch(typeLength) {
         case 1:
            OpenGlHelper.glUniform1(this.getUniformLocation(key).intValue(), intBuffer);
            break;
         case 2:
            OpenGlHelper.glUniform2(this.getUniformLocation(key).intValue(), intBuffer);
            break;
         case 3:
            OpenGlHelper.glUniform3(this.getUniformLocation(key).intValue(), intBuffer);
            break;
         case 4:
            OpenGlHelper.glUniform4(this.getUniformLocation(key).intValue(), intBuffer);
            break;
         default:
            throw new IllegalArgumentException();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean setUniformFloats(String key, float... floats) {
      return this.setUniformFloatsOfType(key, floats.length, floats);
   }

   public boolean setUniformFloatsOfType(String key, int typeLength, float... floats) {
      if (this.shaderID > 0 && this.shaderActive) {
         FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(floats.length);
         floatBuffer.put(floats);
         floatBuffer.position(0);
         switch(typeLength) {
         case 1:
            OpenGlHelper.glUniform1(this.getUniformLocation(key).intValue(), floatBuffer);
            break;
         case 2:
            OpenGlHelper.glUniform2(this.getUniformLocation(key).intValue(), floatBuffer);
            break;
         case 3:
            OpenGlHelper.glUniform3(this.getUniformLocation(key).intValue(), floatBuffer);
            break;
         case 4:
            OpenGlHelper.glUniform4(this.getUniformLocation(key).intValue(), floatBuffer);
            break;
         default:
            throw new IllegalArgumentException();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean setUniformMatrix(String key, Matrix matrix) {
      if (this.shaderID > 0 && this.shaderActive) {
         int width;
         if (matrix instanceof Matrix2f) {
            width = 2;
         } else if (matrix instanceof Matrix3f) {
            width = 3;
         } else {
            if (!(matrix instanceof Matrix4f)) {
               throw new IllegalArgumentException();
            }

            width = 4;
         }

         FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(width * width);
         matrix.store(floatBuffer);
         floatBuffer.position(0);
         switch(width) {
         case 2:
            OpenGlHelper.glUniformMatrix2(this.getUniformLocation(key).intValue(), false, floatBuffer);
            break;
         case 3:
            OpenGlHelper.glUniformMatrix3(this.getUniformLocation(key).intValue(), false, floatBuffer);
            break;
         default:
            OpenGlHelper.glUniformMatrix4(this.getUniformLocation(key).intValue(), false, floatBuffer);
         }

         return true;
      } else {
         return false;
      }
   }

   public Integer getUniformLocation(String key) {
      if (this.shaderID <= 0) {
         return Integer.valueOf(0);
      } else {
         if (!this.uniformLocations.containsKey(key)) {
            this.uniformLocations.put(key, OpenGlHelper.glGetUniformLocation(this.shaderID, key));
         }

         return this.uniformLocations.get(key);
      }
   }

   public void deleteShader() {
      if (this.shaderActive) {
         this.stopUsingShader();
      }

      if (this.shaderID > 0) {
         OpenGlHelper.glDeleteProgram(this.shaderID);
         this.shaderID = 0;
      }

      this.uniformLocations.clear();
   }

   public static void outputShaderInfo(Logger logger) {
      String renderer = GL11.glGetString(7937);
      String vendor = GL11.glGetString(7936);
      String version = GL11.glGetString(7938);
      boolean fboSupported = OpenGlHelper.framebufferSupported;

      String glslVersion;
      try {
         glslVersion = GL11.glGetString(35724);
      } catch (Exception var10) {
         glslVersion = "? (No GL20)";
      }

      String majorVersion;
      String minorVersion;
      try {
         minorVersion = "" + GL11.glGetInteger(33308);
         majorVersion = "" + GL11.glGetInteger(33307);
      } catch (Exception var9) {
         minorVersion = "?";
         majorVersion = "? (No GL 30)";
      }

      printAlignedInfo("Vendor", vendor, logger);
      printAlignedInfo("Renderer", renderer, logger);
      printAlignedInfo("Version", version, logger);
      printAlignedInfo("Versions", getGLVersions(GLContext.getCapabilities()), logger);
      printAlignedInfo("Version Range", String.format("%s - %s", minorVersion, majorVersion), logger);
      printAlignedInfo("GLSL Version", glslVersion, logger);
      printAlignedInfo("Frame buffer object", fboSupported ? "Supported" : "Unsupported", logger);
   }

   private static void printAlignedInfo(String category, String info, Logger logger) {
      logger.info(String.format("%-20s: %s", category, info));
   }

   private static String getGLVersions(ContextCapabilities cap) {
      String versions = "";

      try {
         if (cap.OpenGL11) {
            versions = versions + ":11";
         }

         if (cap.OpenGL12) {
            versions = versions + ":12";
         }

         if (cap.OpenGL13) {
            versions = versions + ":13";
         }

         if (cap.OpenGL14) {
            versions = versions + ":14";
         }

         if (cap.OpenGL15) {
            versions = versions + ":15";
         }
      } catch (Throwable var5) {
         versions = versions + ":lwjgl-Error-1";
      }

      try {
         if (cap.OpenGL20) {
            versions = versions + ":20";
         }

         if (cap.OpenGL21) {
            versions = versions + ":21";
         }
      } catch (Throwable var4) {
         versions = versions + ":lwjgl-Error-2";
      }

      try {
         if (cap.OpenGL30) {
            versions = versions + ":30";
         }

         if (cap.OpenGL31) {
            versions = versions + ":31";
         }

         if (cap.OpenGL32) {
            versions = versions + ":32";
         }

         if (cap.OpenGL33) {
            versions = versions + ":33";
         }
      } catch (Throwable var3) {
         versions = versions + ":lwjgl-Error-3";
      }

      if (versions.length() > 0) {
         versions = versions.substring(1);
      }

      return versions;
   }
}
