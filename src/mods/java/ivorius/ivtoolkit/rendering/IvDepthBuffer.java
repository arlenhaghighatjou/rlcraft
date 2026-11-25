package ivorius.ivtoolkit.rendering;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class IvDepthBuffer {
   public Logger logger;
   private boolean setUp;
   private int depthTextureIndex;
   private int depthFB;
   private int parentFB;
   private int textureWidth;
   private int textureHeight;

   public IvDepthBuffer(int width, int height, Logger logger) {
      this.setParentFB(0);
      this.setSize(width, height);
      this.logger = logger;
   }

   public boolean allocate() {
      this.deallocate();
      if (OpenGlHelper.framebufferSupported && this.textureWidth > 0 && this.textureHeight > 0) {
         this.depthTextureIndex = genDefaultDepthTexture(this.textureWidth, this.textureHeight);
         this.depthFB = OpenGlHelper.glGenFramebuffers();
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.depthFB);
         OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, 3553, this.depthTextureIndex, 0);
         GL11.glDrawBuffer(0);
         GL11.glReadBuffer(0);
         int status = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
         if (status != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
            this.logger.error("Depth FBO failed setting up! (" + getFramebufferStatusString(status) + ")");
         } else {
            this.setUp = true;
         }

         this.unbind();
      }

      return this.setUp;
   }

   public static int genDefaultDepthTexture(int textureWidth, int textureHeight) {
      int depthTextureIndex = GlStateManager.generateTexture();
      GlStateManager.bindTexture(depthTextureIndex);
      GL11.glTexParameterf(3553, 10242, 33071.0F);
      GL11.glTexParameterf(3553, 10243, 33071.0F);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 34891, 32841);
      GL11.glTexParameteri(3553, 34892, 34894);
      GL11.glTexParameteri(3553, 34893, 515);
      GL11.glTexImage2D(3553, 0, 33190, textureWidth, textureHeight, 0, 6402, 5126, (ByteBuffer)null);
      return depthTextureIndex;
   }

   public void deallocate() {
      this.setUp = false;
      if (this.depthTextureIndex > 0) {
         GlStateManager.deleteTexture(this.depthTextureIndex);
         this.depthTextureIndex = 0;
      }

      if (this.depthFB > 0) {
         OpenGlHelper.glDeleteFramebuffers(this.depthFB);
         this.depthFB = 0;
      }

   }

   public int getDepthFBObject() {
      return this.isAllocated() ? this.depthFB : 0;
   }

   public int getDepthTextureIndex() {
      return this.isAllocated() ? this.depthTextureIndex : 0;
   }

   public void bind() {
      if (this.isAllocated()) {
         this.bindTextureForDestination();
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.depthFB);
         GL11.glDrawBuffer(0);
         GL11.glReadBuffer(0);
      }

   }

   public void unbind() {
      if (this.isAllocated()) {
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
         GL11.glDrawBuffer(1029);
         GL11.glReadBuffer(1029);
         if (this.parentFB > 0) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.parentFB);
         }
      }

   }

   public boolean isAllocated() {
      return this.setUp;
   }

   public void setSize(int width, int height) {
      if (this.textureWidth != width || this.textureHeight != height) {
         this.textureWidth = width;
         this.textureHeight = height;
         if (this.isAllocated()) {
            this.allocate();
         }
      }

   }

   public int getParentFB() {
      return this.parentFB;
   }

   public void setParentFB(int parentFB) {
      this.parentFB = parentFB > 0 ? parentFB : 0;
   }

   public static void bindTextureForSource(int glTexture, int textureIndex) {
      GlStateManager.bindTexture(textureIndex);
      GL11.glTexParameteri(3553, 34892, 0);
      GL11.glTexParameteri(3553, 34891, 6409);
      OpenGlHelper.setActiveTexture(glTexture);
      GlStateManager.bindTexture(textureIndex);
      OpenGlHelper.setActiveTexture(33984);
   }

   public static void bindTextureForDestination(int textureIndex) {
      GlStateManager.bindTexture(textureIndex);
      GL11.glTexParameteri(3553, 34892, 34894);
      GL11.glTexParameteri(3553, 34891, 32841);
   }

   public void bindTextureForSource(int glTexture) {
      bindTextureForSource(glTexture, this.getDepthTextureIndex());
   }

   public void bindTextureForDestination() {
      bindTextureForDestination(this.getDepthTextureIndex());
   }

   public int getTextureWidth() {
      return this.textureWidth;
   }

   public int getTextureHeight() {
      return this.textureHeight;
   }

   public static String getFramebufferStatusString(int code) {
      return code + ": " + GLU.gluErrorString(code);
   }
}
