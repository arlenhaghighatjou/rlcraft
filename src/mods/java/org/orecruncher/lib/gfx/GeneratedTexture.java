package org.orecruncher.lib.gfx;

import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public abstract class GeneratedTexture {
   protected final DynamicTexture texture;
   protected final ResourceLocation resource;
   protected final int width;
   protected final int height;
   protected final int size;

   public GeneratedTexture(String name, int width, int height) {
      this.width = width;
      this.height = height;
      this.size = width * height;
      this.texture = new DynamicTexture(this.width, this.height);
      this.resource = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(name, this.texture);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getSize() {
      return this.texture.getTextureData().length;
   }

   public void bindTexture() {
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.resource);
   }

   public void release() {
      this.texture.deleteGlTexture();
   }

   public boolean useDepth() {
      return true;
   }

   public void updateTexture() {
      Framebuffer mcFrameBuffer = Minecraft.getMinecraft().getFramebuffer();
      Framebuffer blit = new Framebuffer(this.width, this.height, this.useDepth());
      blit.framebufferClear();
      blit.bindFramebuffer(this.useDepth());
      OpenGlState glState = OpenGlState.push();
      GL11.glPushAttrib(7072);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, (double)this.getWidth(), (double)this.getHeight(), 0.0D, 1000.0D, 3000.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      GlStateManager.enableTexture2D();

      try {
         this.render();
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.popAttrib();
      GlStateManager.popMatrix();
      OpenGlState.pop(glState);

      try {
         blit.bindFramebufferTexture();
         IntBuffer pixelBuffer = BufferUtils.createIntBuffer(this.getSize());
         GlStateManager.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
         pixelBuffer.get(this.texture.getTextureData(), 0, pixelBuffer.remaining());
         this.texture.updateDynamicTexture();
      } catch (Exception var9) {
         var9.printStackTrace();
      } finally {
         blit.deleteFramebuffer();
         if (mcFrameBuffer != null) {
            mcFrameBuffer.bindFramebuffer(true);
         }

      }

   }

   public abstract void render();
}
