package net.mcft.copy.backpacks.client.gui;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import java.util.Stack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContext {
   public static boolean DEBUG = false;
   public static boolean DEBUG_TEXT = false;
   private GuiElementBase _focused;
   private GuiElementBase _pressed;
   private GuiContainerScreen _screen;
   private Stack<GuiContext.ScissorRegion> _scissorStack = new Stack();
   private boolean _clipping = false;

   public GuiContext() {
   }

   public GuiElementBase getFocused() {
      return this._focused;
   }

   void setFocused(GuiElementBase value) {
      this._focused = value;
   }

   public GuiElementBase getPressed() {
      return this._pressed;
   }

   void setPressed(GuiElementBase value) {
      this._pressed = value;
   }

   public GuiContainerScreen getScreen() {
      return this._screen;
   }

   void setScreen(GuiContainerScreen value) {
      this._screen = value;
   }

   public void pushScissor(GuiElementBase element, int x, int y, int width, int height) {
      ElementInfo info = (ElementInfo)ElementInfo.getElementHierarchy(element).getFirst();
      this.pushScissor(info.globalX + x, info.globalY + y, width, height);
   }

   public void pushScissor(int globalX, int globalY, int width, int height) {
      GuiContext.ScissorRegion region = !this._scissorStack.empty() ? ((GuiContext.ScissorRegion)this._scissorStack.peek()).merge(globalX, globalY, width, height) : new GuiContext.ScissorRegion(globalX, globalY, width, height);
      this._scissorStack.push(region);
      this.setScissor(region);
   }

   public void popScissor() {
      this._scissorStack.pop();
      this.setScissor(!this._scissorStack.empty() ? (GuiContext.ScissorRegion)this._scissorStack.peek() : null);
   }

   private void setScissor(GuiContext.ScissorRegion region) {
      if (this._clipping) {
         GlStateManager.depthMask(true);
         GlStateManager.clear(RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
         GlStateManager.disableDepth();
         this._clipping = false;
      }

      if (region != null) {
         ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
         int screenWidth = resolution.getScaledWidth();
         int screenHeight = resolution.getScaledHeight();
         int x = region.globalX;
         int y = region.globalY;
         GlStateManager.clear(RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(RealOpenGLEnums.GL_ALWAYS);
         GlStateManager.colorMask(false, false, false, false);
         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer buffer = tessellator.getBuffer();
         buffer.begin(RealOpenGLEnums.GL_QUADS, DefaultVertexFormats.POSITION);
         clipQuad(buffer, 0, 0, screenWidth, y);
         clipQuad(buffer, 0, y + region.height, screenWidth, screenHeight);
         clipQuad(buffer, 0, y, x, y + region.height);
         clipQuad(buffer, x + region.width, y, screenWidth, y + region.height);
         tessellator.draw();
         GlStateManager.enableTexture2D();
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.depthFunc(RealOpenGLEnums.GL_LEQUAL);
         GlStateManager.depthMask(false);
         this._clipping = true;
      }

   }

   private static void clipQuad(WorldRenderer buffer, int left, int top, int right, int bottom) {
      buffer.pos((double)left, (double)top, 950.0D).endVertex();
      buffer.pos((double)left, (double)bottom, 950.0D).endVertex();
      buffer.pos((double)right, (double)bottom, 950.0D).endVertex();
      buffer.pos((double)right, (double)top, 950.0D).endVertex();
   }

   private static class ScissorRegion {
      public final int globalX;
      public final int globalY;
      public final int width;
      public final int height;

      public ScissorRegion(int globalX, int globalY, int width, int height) {
         this.globalX = globalX;
         this.globalY = globalY;
         this.width = width;
         this.height = height;
      }

      public GuiContext.ScissorRegion merge(int globalX, int globalY, int width, int height) {
         return new GuiContext.ScissorRegion(Math.max(globalX, this.globalX), Math.max(globalY, this.globalY), Math.min(globalX + width, this.globalX + this.width) - Math.max(globalX, this.globalX), Math.min(globalY + height, this.globalY + this.height) - Math.max(globalY, this.globalY));
      }
   }
}
