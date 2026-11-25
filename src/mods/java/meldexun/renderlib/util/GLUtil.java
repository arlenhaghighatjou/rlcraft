package meldexun.renderlib.util;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import meldexun.matrixutil.Matrix4f;
import meldexun.renderlib.RenderLib;
import meldexun.renderlib.config.RenderLibConfig;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.KHRDebugCallback;
import org.lwjgl.opengl.KHRDebugCallback.Handler;

public class GLUtil {
   public static ContextCapabilities CAPS;
   private static final UnsafeBuffer<FloatBuffer> FLOAT_BUFFER = new UnsafeBuffer<FloatBuffer>(BufferUtil.allocateFloat(16));

   public GLUtil() {
   }

   public static void init() {
      CAPS = GLContext.getCapabilities();
      RenderLib.LOGGER.info("OpenGL Vendor: {}", GL11.glGetString(7936));
      RenderLib.LOGGER.info("OpenGL Renderer: {}", GL11.glGetString(7937));
      RenderLib.LOGGER.info("OpenGL Version: {}", GL11.glGetString(7938));
      RenderLib.LOGGER.info("OpenGL Extensions: {}", GL11.glGetString(7939));

      try {
         for(Field f : ContextCapabilities.class.getFields()) {
            if (f.getType() == Boolean.TYPE) {
               RenderLib.LOGGER.info("{} {}", f.getName(), f.get(CAPS));
            }
         }
      } catch (IllegalAccessException | IllegalArgumentException var4) {
         var4.printStackTrace();
      }

   }

   public static void updateDebugOutput() {
      if (CAPS != null) {
         RenderLib.LOGGER.info("OpenGL Debug: supported={}, enabled={}", Boolean.valueOf(CAPS.OpenGL43), Boolean.valueOf(RenderLibConfig.openGLDebugOutput.enabled));
         if (CAPS.OpenGL43) {
            if (RenderLibConfig.openGLDebugOutput.enabled) {
               GL11.glEnable(37600);
               GL11.glEnable(33346);
               GL43.glDebugMessageCallback(new KHRDebugCallback((source, type, id, severity, message) -> {
                  if (type == 33356) {
                     throw new GLException(String.format("OpenGL Error: %s %s %s %s %s", getSource(source), getType(type), getSeverity(severity), id, message));
                  } else {
                     RenderLib.LOGGER.info("OpenGL Debug: {} {} {} {} {}", getSource(source), getType(type), getSeverity(severity), Integer.valueOf(id), message);
                  }
               }));
               GL43.glDebugMessageControl(4352, 4352, 4352, (IntBuffer)null, false);
               GL43.glDebugMessageControl(4352, 33356, 4352, (IntBuffer)null, true);
               if (RenderLibConfig.openGLDebugOutput.logHighSeverity) {
                  GL43.glDebugMessageControl(4352, 4352, 37190, (IntBuffer)null, true);
               }

               if (RenderLibConfig.openGLDebugOutput.logMediumSeverity) {
                  GL43.glDebugMessageControl(4352, 4352, 37191, (IntBuffer)null, true);
               }

               if (RenderLibConfig.openGLDebugOutput.logLowSeverity) {
                  GL43.glDebugMessageControl(4352, 4352, 37192, (IntBuffer)null, true);
               }

               if (RenderLibConfig.openGLDebugOutput.logNotificationSeverity) {
                  GL43.glDebugMessageControl(4352, 4352, 33387, (IntBuffer)null, true);
               }
            } else {
               GL11.glDisable(37600);
               GL11.glDisable(33346);
            }

         }
      }
   }

   private static String getSource(int source) {
      switch(source) {
      case 33350:
         return "API";
      case 33351:
         return "WINDOW SYSTEM";
      case 33352:
         return "SHADER COMPILER";
      case 33353:
         return "THIRD PARTY";
      case 33354:
         return "APPLICATION";
      case 33355:
         return "OTHER";
      default:
         return "?";
      }
   }

   private static String getType(int type) {
      switch(type) {
      case 33356:
         return "ERROR";
      case 33357:
         return "DEPRECATED BEHAVIOR";
      case 33358:
         return "UNDEFINED BEHAVIOR";
      case 33359:
         return "PORTABILITY";
      case 33360:
         return "PERFORMANCE";
      case 33361:
         return "OTHER";
      case 33384:
         return "MARKER";
      default:
         return "?";
      }
   }

   private static String getSeverity(int severity) {
      switch(severity) {
      case 33387:
         return "NOTIFICATION";
      case 37190:
         return "HIGH";
      case 37191:
         return "MEDIUM";
      case 37192:
         return "LOW";
      default:
         return "?";
      }
   }

   public static MemoryAccess getFloat(int pname) {
      GL11.glGetFloat(pname, FLOAT_BUFFER.getBuffer());
      return FLOAT_BUFFER;
   }

   public static Matrix4f getMatrix(int matrix) {
      GL11.glGetFloat(matrix, FLOAT_BUFFER.getBuffer());
      Matrix4f m = new Matrix4f();
      m.load(FLOAT_BUFFER.getAddress());
      return m;
   }

   public static void setMatrix(int uniform, Matrix4f matrix) {
      matrix.store(FLOAT_BUFFER.getAddress());
      GL20.glUniformMatrix4(uniform, false, FLOAT_BUFFER.getBuffer());
   }

   public static int createBuffer(long size, int flags, int usage) {
      if (CAPS.OpenGL45) {
         int buffer = GL45.glCreateBuffers();
         GL45.glNamedBufferStorage(buffer, size, flags);
         return buffer;
      } else if (CAPS.OpenGL44) {
         int buffer = GL15.glGenBuffers();
         GL15.glBindBuffer(34962, buffer);
         GL44.glBufferStorage(34962, size, flags);
         GL15.glBindBuffer(34962, 0);
         return buffer;
      } else {
         int buffer = GL15.glGenBuffers();
         GL15.glBindBuffer(34962, buffer);
         GL15.glBufferData(34962, size, usage);
         GL15.glBindBuffer(34962, 0);
         return buffer;
      }
   }

   public static ByteBuffer map(int buffer, long length, int accessRange, int access, @Nullable ByteBuffer oldBuffer) {
      if (CAPS.OpenGL45) {
         return GL45.glMapNamedBufferRange(buffer, 0L, length, accessRange, oldBuffer);
      } else if (CAPS.OpenGL30) {
         if (!CAPS.OpenGL44) {
            accessRange &= -193;
         }

         GL15.glBindBuffer(34962, buffer);
         ByteBuffer byteBuffer = GL30.glMapBufferRange(34962, 0L, length, accessRange, oldBuffer);
         GL15.glBindBuffer(34962, 0);
         return byteBuffer;
      } else {
         GL15.glBindBuffer(34962, buffer);
         ByteBuffer byteBuffer = GL15.glMapBuffer(34962, access, length, oldBuffer);
         GL15.glBindBuffer(34962, 0);
         return byteBuffer;
      }
   }

   public static void unmap(int buffer) {
      if (CAPS.OpenGL45) {
         GL45.glUnmapNamedBuffer(buffer);
      } else {
         GL15.glBindBuffer(34962, buffer);
         GL15.glUnmapBuffer(34962);
         GL15.glBindBuffer(34962, 0);
      }

   }
}
