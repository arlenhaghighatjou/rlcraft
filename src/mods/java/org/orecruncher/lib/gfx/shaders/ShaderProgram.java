package org.orecruncher.lib.gfx.shaders;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.io.Streams;

public class ShaderProgram {
   protected final String name;
   protected int programId = -1;
   protected boolean isDrawing = false;
   protected int textureIndex = 0;

   public ShaderProgram(String name) {
      if (StringUtils.isEmpty(name)) {
         this.name = "UNKNOWN";
      } else {
         this.name = name;
      }

   }

   protected void initialize(String vertexSrc, String fragmentSrc) throws ShaderException {
      int vertShader = 0;
      int fragShader = 0;
      vertShader = this.createShader(vertexSrc, 35633);
      fragShader = this.createShader(fragmentSrc, 35632);
      if (vertShader != 0 && fragShader != 0) {
         this.programId = ARBShaderObjects.glCreateProgramObjectARB();
         if (this.programId != 0) {
            ARBShaderObjects.glAttachObjectARB(this.programId, vertShader);
            ARBShaderObjects.glAttachObjectARB(this.programId, fragShader);
            ARBShaderObjects.glLinkProgramARB(this.programId);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.programId, 35714) == 0) {
               throw new ShaderException(this.name, getLogInfo(this.programId));
            }

            ARBShaderObjects.glValidateProgramARB(this.programId);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.programId, 35715) == 0) {
               throw new ShaderException(this.name, getLogInfo(this.programId));
            }
         }

      } else {
         throw new ShaderException(this.name, "Unable to intialize shader!");
      }
   }

   public final String getName() {
      return this.name;
   }

   public final int getProgramId() {
      return this.programId;
   }

   public final boolean isValid() {
      return this.programId != 0;
   }

   public final void use() throws ShaderException {
      this.use((ShaderProgram.IShaderUseCallback)null);
   }

   public final void unUse() throws ShaderException {
      try {
         String log = getLogInfo(this.programId);
         if (!StringUtils.isEmpty(log)) {
            throw new ShaderException(this.name, log);
         }
      } finally {
         ARBShaderObjects.glUseProgramObjectARB(0);
      }

   }

   public final void delete() {
      if (this.isValid()) {
         ARBShaderObjects.glUseProgramObjectARB(0);
         ARBShaderObjects.glDeleteObjectARB(this.programId);
      }

   }

   public final void use(ShaderProgram.IShaderUseCallback callback) throws ShaderException {
      this.textureIndex = 0;
      ARBShaderObjects.glUseProgramObjectARB(this.programId);
      if (callback != null) {
         callback.call(this);
      }

   }

   protected final int getUniform(String name) throws ShaderException {
      if (!this.isValid()) {
         throw new ShaderException(this.name, "ShaderProgram is not valid!");
      } else {
         int id = ARBShaderObjects.glGetUniformLocationARB(this.programId, name);
         if (id == -1) {
            throw new ShaderException(this.name, String.format("Unknown uniform '%s'", name));
         } else {
            return id;
         }
      }
   }

   public void set(String name, float... value) throws ShaderException {
      if (value.length != 0 && value.length <= 4) {
         int id = this.getUniform(name);
         if (value.length == 1) {
            ARBShaderObjects.glUniform1fARB(this.getUniform(name), value[0]);
         } else {
            FloatBuffer buff = BufferUtils.createFloatBuffer(value.length);

            for(int i = 0; i < value.length; ++i) {
               buff.put(value[i]);
            }

            buff.flip();
            switch(value.length) {
            case 1:
               ARBShaderObjects.glUniform1ARB(id, buff);
               break;
            case 2:
               ARBShaderObjects.glUniform2ARB(id, buff);
               break;
            case 3:
               ARBShaderObjects.glUniform3ARB(id, buff);
               break;
            case 4:
               ARBShaderObjects.glUniform4ARB(id, buff);
            }
         }

      } else {
         throw new ShaderException(this.name, "Invalid number of elements");
      }
   }

   public void setMatrix4(String name, FloatBuffer matrix) throws ShaderException {
      ARBShaderObjects.glUniformMatrix4ARB(this.getUniform(name), false, matrix);
   }

   public void set(String name, Color color) throws ShaderException {
      this.set(name, color, 1.0F);
   }

   public void set(String name, Color color, float alpha) throws ShaderException {
      this.set(name, color.red, color.green, color.blue, alpha);
   }

   public void set(String name, Vec3d vector) throws ShaderException {
      this.set(name, (float)vector.x, (float)vector.y, (float)vector.z);
   }

   public void set(String name, Vec3i vector) throws ShaderException {
      this.set(name, (float)vector.getX(), (float)vector.getY(), (float)vector.getZ());
   }

   public void set(String name, Vec2f vector) throws ShaderException {
      this.set(name, vector.x, vector.y);
   }

   public void set(String name, int value) throws ShaderException {
      int id = this.getUniform(name);
      ARBShaderObjects.glUniform1iARB(id, value);
   }

   public void set(String name, ResourceLocation texture) throws ShaderException {
      int textureId = this.textureIndex++;
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + textureId);
      GlStateManager.enableTexture2D();
      Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
   }

   protected void initialize(ResourceLocation vertexResource, ResourceLocation fragmentResource) throws Exception {
      String vertex = Streams.readResourceAsString(vertexResource);
      String fragment = Streams.readResourceAsString(fragmentResource);
      this.initialize(vertex, fragment);
   }

   public static ShaderProgram createProgram(String name, ResourceLocation vertex, ResourceLocation fragment) throws Exception {
      ShaderProgram prog = new ShaderProgram(name);
      prog.initialize(vertex, fragment);
      return prog;
   }

   private int createShader(String src, int shaderType) throws ShaderException {
      int shader = 0;

      try {
         shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
         if (shader == 0) {
            return 0;
         } else {
            ARBShaderObjects.glShaderSourceARB(shader, src);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
               throw new ShaderException(this.name, getLogInfo(shader));
            } else {
               return shader;
            }
         }
      } catch (Exception var5) {
         ARBShaderObjects.glDeleteObjectARB(shader);
         throw var5;
      }
   }

   private static String getLogInfo(int obj) {
      return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, 35716));
   }

   public interface IShaderUseCallback {
      void call(ShaderProgram var1) throws ShaderException;
   }
}
