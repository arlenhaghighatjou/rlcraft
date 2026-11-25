package net.mcft.copy.backpacks.client.gui;

import java.util.Stack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiContext {
   public static boolean DEBUG = false;
   public static boolean DEBUG_TEXT = false;
   private GuiElementBase _focused;
   private GuiElementBase _pressed;
   private GuiContainerScreen _screen;
   private Stack<GuiContext.ScissorRegion> _scissorStack = new Stack();

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
      if (region != null) {
         Minecraft mc = Minecraft.getMinecraft();
         int scale = (new ScaledResolution(mc)).getScaleFactor();
         GL11.glScissor(region.globalX * scale, mc.displayHeight - (region.globalY + region.height) * scale, region.width * scale, region.height * scale);
         GL11.glEnable(3089);
      } else {
         GL11.glDisable(3089);
      }

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
