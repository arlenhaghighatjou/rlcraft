package com.fantasticsource.setbonus.client;

import com.fantasticsource.tools.datastructures.Color;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL30;

public class Canvas {
   private int width;
   private int height;
   private int texture;
   private int fbo;
   private boolean destroyed = false;

   public Canvas(int width, int height) {
      this.width = width;
      this.height = height;
      this.fbo = GL30.glGenFramebuffers();
      this.texture = GlStateManager.generateTexture();
      GL30.glBindFramebuffer(36160, this.fbo);
      GlStateManager.bindTexture(this.texture);
      GlStateManager.glTexParameteri(3553, 10240, 9729);
      GlStateManager.glTexParameteri(3553, 10241, 9729);
      GlStateManager.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, (IntBuffer)null);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, this.texture, 0);
      GL30.glBindFramebuffer(36160, 0);
   }

   public void destroy() {
      if (!this.destroyed) {
         GlStateManager.deleteTexture(this.texture);
         GL30.glDeleteFramebuffers(this.fbo);
         this.destroyed = true;
      }

   }

   public void finalize() {
      this.destroy();
   }

   public void setTarget() {
      GL30.glBindFramebuffer(36160, this.fbo);
   }

   public void resetTarget() {
      Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void draw(int x, int y) {
      this.draw(x, y, 0, 0, this.width, this.height, new Color(-1));
   }

   public void draw(int x, int y, Color color) {
      this.draw(x, y, 0, 0, this.width, this.height, color);
   }

   public void draw(int x, int y, int canvasX, int canvasY, int width, int height) {
      this.draw(x, y, canvasX, canvasY, width, height, new Color(-1));
   }

   public void draw(int x, int y, int canvasX, int canvasY, int width, int height, Color color) {
      float tx1 = (float)canvasX / (float)this.width;
      float tx2 = (float)(canvasX + width) / (float)this.width;
      float ty1 = (float)canvasY / (float)this.height;
      float ty2 = (float)(canvasY + height) / (float)this.height;
      GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(this.texture);
      GlStateManager.glBegin(7);
      GlStateManager.glTexCoord2f(tx1, ty2);
      GlStateManager.glVertex3f((float)x, (float)(y + height), 0.0F);
      GlStateManager.glTexCoord2f(tx2, ty2);
      GlStateManager.glVertex3f((float)(x + width), (float)(y + height), 0.0F);
      GlStateManager.glTexCoord2f(tx2, ty1);
      GlStateManager.glVertex3f((float)(x + width), (float)y, 0.0F);
      GlStateManager.glTexCoord2f(tx1, ty1);
      GlStateManager.glVertex3f((float)x, (float)y, 0.0F);
      GlStateManager.glEnd();
      GlStateManager.disableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
