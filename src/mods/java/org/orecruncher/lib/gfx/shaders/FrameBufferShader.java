package org.orecruncher.lib.gfx.shaders;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class FrameBufferShader {
   protected final ShaderProgram shader;
   protected final ShaderProgram.IShaderUseCallback callback;
   protected Framebuffer blit = null;
   private static final FloatBuffer CLEAR_COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(16);

   public FrameBufferShader(ShaderProgram program, ShaderProgram.IShaderUseCallback callback) {
      this.shader = program;
      this.callback = callback;
   }

   public void setSize(int width, int height) {
      if (this.blit != null && (this.blit.framebufferWidth != width || this.blit.framebufferHeight != height)) {
         this.release();
      }

      if (this.blit == null) {
         this.blit = new Framebuffer(width, height, false);
         this.blit.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      }

   }

   public void bindTexture() {
      if (this.blit == null) {
         throw new RuntimeException("Texture not available");
      } else {
         this.blit.bindFramebufferTexture();
      }
   }

   public void release() {
      if (this.blit != null) {
         this.blit.deleteFramebuffer();
         this.blit = null;
      }

   }

   public void render() throws ShaderException {
      if (this.blit == null) {
         throw new RuntimeException("Framebuffer not initialized");
      } else {
         this.blit.framebufferClear();
         Framebuffer mcFrameBuffer = Minecraft.getMinecraft().getFramebuffer();
         this.blit.bindFramebuffer(true);
         float renderHeight = (float)this.blit.framebufferHeight;
         float renderWidth = (float)this.blit.framebufferWidth;
         GL11.glPushAttrib(7072);
         int prevShaderProgram = GL11.glGetInteger(35725);
         GL11.glGetFloat(3106, CLEAR_COLOR_BUFFER);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, (double)renderWidth, (double)renderHeight, 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         this.shader.use(this.callback);
         GlStateManager.enableTexture2D();
         GlStateManager.glBegin(4);
         GlStateManager.glTexCoord2f(0.0F, 0.0F);
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glTexCoord2f(0.0F, 1.0F);
         GlStateManager.glVertex3f(0.0F, renderHeight, 0.0F);
         GlStateManager.glTexCoord2f(1.0F, 1.0F);
         GlStateManager.glVertex3f(renderWidth, renderHeight, 0.0F);
         GlStateManager.glTexCoord2f(1.0F, 1.0F);
         GlStateManager.glVertex3f(renderWidth, renderHeight, 0.0F);
         GlStateManager.glTexCoord2f(1.0F, 0.0F);
         GlStateManager.glVertex3f(renderWidth, 0.0F, 0.0F);
         GlStateManager.glTexCoord2f(0.0F, 0.0F);
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glEnd();
         this.shader.unUse();
         OpenGlHelper.glUseProgram(prevShaderProgram);
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         GlStateManager.popAttrib();
         GlStateManager.clearColor(CLEAR_COLOR_BUFFER.get(0), CLEAR_COLOR_BUFFER.get(1), CLEAR_COLOR_BUFFER.get(2), CLEAR_COLOR_BUFFER.get(3));
         GlStateManager.popMatrix();
         mcFrameBuffer.bindFramebuffer(true);
      }
   }
}
