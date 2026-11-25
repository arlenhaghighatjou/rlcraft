package ivorius.ivtoolkit.rendering;

import java.nio.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class IvOpenGLTexturePingPong {
   public Logger logger;
   public int[] cacheTextures = new int[2];
   public int activeBuffer;
   public int pingPongFB;
   public boolean setup = false;
   public boolean setupRealtimeFB = false;
   public boolean setupCacheTextureForTick = false;
   private int screenWidth;
   private int screenHeight;
   private int parentFrameBuffer;
   private boolean useFramebuffer;

   public IvOpenGLTexturePingPong(Logger logger) {
      this.logger = logger;
   }

   public void initialize(boolean useFramebuffer) {
      this.destroy();
      this.useFramebuffer = useFramebuffer;
      boolean fboFailed = false;

      for(int i = 0; i < 2; ++i) {
         this.cacheTextures[i] = IvOpenGLHelper.genStandardTexture();
         GL11.glTexImage2D(3553, 0, 6408, this.screenWidth, this.screenHeight, 0, 6408, 5121, (ByteBuffer)null);
      }

      if (this.cacheTextures[0] > 0 && this.cacheTextures[1] > 0) {
         if (OpenGlHelper.framebufferSupported && useFramebuffer) {
            this.pingPongFB = OpenGlHelper.glGenFramebuffers();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.pingPongFB);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, this.cacheTextures[0], 0);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0 + 1, 3553, this.cacheTextures[1], 0);
            int status = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
            if (status != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
               this.logger.error("PingPong FBO failed setting up! (" + IvDepthBuffer.getFramebufferStatusString(status) + ")");
               fboFailed = true;
            }

            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.parentFrameBuffer);
            this.setup = true;
         } else {
            fboFailed = true;
            this.setup = true;
         }
      } else {
         fboFailed = true;
         this.setup = false;
      }

      if (!fboFailed) {
         this.setupRealtimeFB = true;
      } else {
         this.logger.error("Can not PingPong! Using screen pong workaround");
      }

   }

   public void setScreenSize(int screenWidth, int screenHeight) {
      boolean gen = screenWidth != this.screenWidth || screenHeight != this.screenHeight;
      if (gen) {
         this.screenWidth = screenWidth;
         this.screenHeight = screenHeight;
         if (this.setup) {
            this.initialize(this.useFramebuffer);
         }
      }

   }

   public int getScreenWidth() {
      return this.screenWidth;
   }

   public int getScreenHeight() {
      return this.screenHeight;
   }

   public void setParentFrameBuffer(int parentFrameBuffer) {
      this.parentFrameBuffer = parentFrameBuffer > 0 ? parentFrameBuffer : 0;
   }

   public int getParentFrameBuffer() {
      return this.parentFrameBuffer;
   }

   public void preTick(int screenWidth, int screenHeight) {
      this.setupCacheTextureForTick = false;
      this.setScreenSize(screenWidth, screenHeight);
   }

   public void pingPong() {
      if (this.setupRealtimeFB) {
         if (!this.setupCacheTextureForTick) {
            this.activeBuffer = 0;
            this.bindCurrentTexture();
            GL11.glCopyTexImage2D(3553, 0, 6407, 0, 0, this.screenWidth, this.screenHeight, 0);
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.pingPongFB);
            GlStateManager.pushAttrib();
            this.setupCacheTextureForTick = true;
         } else {
            this.activeBuffer = 1 - this.activeBuffer;
            this.bindCurrentTexture();
         }

         GL11.glDrawBuffer(this.activeBuffer == 1 ? OpenGlHelper.GL_COLOR_ATTACHMENT0 : OpenGlHelper.GL_COLOR_ATTACHMENT0 + 1);
         GlStateManager.viewport(0, 0, this.screenWidth, this.screenHeight);
      } else {
         GlStateManager.bindTexture(this.cacheTextures[0]);
         GL11.glCopyTexImage2D(3553, 0, 6407, 0, 0, this.screenWidth, this.screenHeight, 0);
      }

   }

   public void bindCurrentTexture() {
      GlStateManager.bindTexture(this.cacheTextures[this.activeBuffer]);
   }

   public void postTick() {
      if (this.setupRealtimeFB && this.setupCacheTextureForTick) {
         GlStateManager.popAttrib();
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.parentFrameBuffer);
         this.activeBuffer = 1 - this.activeBuffer;
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         this.bindCurrentTexture();
         IvRenderHelper.drawRectFullScreen(this.screenWidth, this.screenHeight);
      }

   }

   public void destroy() {
      for(int i = 0; i < 2; ++i) {
         if (this.cacheTextures[i] > 0) {
            GlStateManager.deleteTexture(this.cacheTextures[i]);
            this.cacheTextures[i] = 0;
         }
      }

      if (this.pingPongFB > 0) {
         OpenGlHelper.glDeleteFramebuffers(this.pingPongFB);
         this.pingPongFB = 0;
      }

      this.setupRealtimeFB = false;
      this.setup = false;
   }
}
