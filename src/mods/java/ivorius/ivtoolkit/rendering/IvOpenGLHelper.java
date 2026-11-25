package ivorius.ivtoolkit.rendering;

import java.lang.reflect.Field;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

public class IvOpenGLHelper {
   public static int GL_VALIDATE_STATUS = 35715;

   public IvOpenGLHelper() {
   }

   public static void glValidateProgram(int program) {
      Field useARBField = ReflectionHelper.findField(OpenGlHelper.class, new String[]{"arbShaders", "arbShaders"});
      useARBField.setAccessible(true);
      boolean useARB = false;

      try {
         useARB = useARBField.getBoolean((Object)null);
      } catch (IllegalAccessException var4) {
         var4.printStackTrace();
      }

      if (useARB) {
         ARBShaderObjects.glValidateProgramARB(program);
      } else {
         GL20.glValidateProgram(program);
      }

   }

   public static int genStandardTexture() {
      int textureIndex = GlStateManager.generateTexture();
      GlStateManager.bindTexture(textureIndex);
      GL11.glTexParameterf(3553, 10242, 33071.0F);
      GL11.glTexParameterf(3553, 10243, 33071.0F);
      GL11.glTexParameterf(3553, 10240, 9729.0F);
      GL11.glTexParameterf(3553, 10241, 9729.0F);
      return textureIndex;
   }

   public static void setUpOpenGLStandard2D(int screenWidth, int screenHeight) {
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 1000.0D, 3000.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   public static void checkGLError(Logger logger, String category) {
      int error;
      while((error = GL11.glGetError()) != 0) {
         String s1 = GLU.gluErrorString(error);
         logger.error("########## GL ERROR ##########");
         logger.error("@ " + category);
         logger.error(error + ": " + s1);
      }

   }
}
