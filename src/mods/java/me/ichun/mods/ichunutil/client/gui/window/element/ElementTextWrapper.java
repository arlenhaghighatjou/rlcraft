package me.ichun.mods.ichunutil.client.gui.window.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class ElementTextWrapper extends Element {
   public ArrayList<String> text = new ArrayList();
   public double sliderProg;
   public boolean showBorder;
   public int spacerL;
   public int spacerR;
   public int spacerU;
   public int spacerD;

   public ElementTextWrapper(Window window, int x, int y, int w, int h, int ID, boolean igMin, boolean border, String... lines) {
      super(window, x, y, w, h, ID, igMin);
      this.showBorder = border;
      this.spacerL = x;
      this.spacerR = this.parent.width - x - this.width;
      this.spacerU = y;
      this.spacerD = this.parent.height - y - this.height;
      Collections.addAll(this.text, lines);
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      RendererHelper.endGlScissor();
      RendererHelper.startGlScissor(this.getPosX(), this.getPosY(), this.width, this.height);
      GlStateManager.pushMatrix();
      int lineHeight = 0;

      for(int i = 0; i < this.text.size(); ++i) {
         String msg = (String)this.text.get(i);
         List list = this.parent.workspace.getFontRenderer().listFormattedStringToWidth(msg, this.width - 12);

         for(int j = 0; j < list.size(); ++j) {
            lineHeight += this.parent.workspace.getFontRenderer().FONT_HEIGHT + 2;
         }
      }

      lineHeight = lineHeight + 4;
      if (lineHeight > this.height) {
         GlStateManager.translate(0.0F, (float)((double)(this.height - lineHeight) * this.sliderProg), 0.0F);
      }

      this.drawText(mouseX, mouseY, hover);
      GlStateManager.popMatrix();
      RendererHelper.endGlScissor();
      if (this.parent.isTab) {
         RendererHelper.startGlScissor(this.parent.posX + 1, this.parent.posY + 1 + 12, this.parent.getWidth() - 2, this.parent.getHeight() - 2 - 12);
      } else {
         RendererHelper.startGlScissor(this.parent.posX + 1, this.parent.posY + 1, this.parent.getWidth() - 2, this.parent.getHeight() - 2);
      }

      if (this.showBorder) {
         int x1 = this.getPosX();
         int x2 = this.getPosX() + this.width;
         int y1 = this.getPosY();
         int y2 = this.getPosY() + this.height;
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)x1, (double)y1, (double)this.width, 1.0D, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)x1, (double)y1, 1.0D, (double)this.height, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)x1, (double)(y2 - 1), (double)this.width, 1.0D, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x2 - 1), (double)y1, 1.0D, (double)this.height, 0.0D);
      }

   }

   public void drawText(int mouseX, int mouseY, boolean hover) {
      for(int i = 0; i < this.text.size(); ++i) {
         String msg = (String)this.text.get(i);
         List list = this.parent.workspace.getFontRenderer().listFormattedStringToWidth(msg, this.width - 12);

         for(int j = 0; j < list.size(); ++j) {
            if (j == 0) {
               this.parent.workspace.getFontRenderer().drawString((String)list.get(j), (float)(this.getPosX() + 4), (float)(this.getPosY() + 4), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
            } else {
               this.parent.workspace.getFontRenderer().drawString((String)list.get(j), (float)(this.getPosX() + 12), (float)(this.getPosY() + 4), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
            }

            GlStateManager.translate(0.0F, (float)(this.parent.workspace.getFontRenderer().FONT_HEIGHT + 2), 0.0F);
         }
      }

   }

   public int getLineCount() {
      int lines = 0;

      for(int i = 0; i < this.text.size(); ++i) {
         String msg = (String)this.text.get(i);
         List list = this.parent.workspace.getFontRenderer().listFormattedStringToWidth(msg, this.width - 12);
         lines += list.size();
      }

      return lines;
   }

   public void resized() {
      this.posX = this.spacerL;
      this.width = this.parent.width - this.posX - this.spacerR;
      this.posY = this.spacerU;
      this.height = this.parent.height - this.posY - this.spacerD;
   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      if (!this.text.isEmpty()) {
         this.sliderProg += 0.1D * MathHelper.clamp((double)(this.height / (this.parent.workspace.getFontRenderer().FONT_HEIGHT + 2) / this.getLineCount()), 1.0D, 0.0D) * (double)(-k);
         this.sliderProg = MathHelper.clamp(this.sliderProg, 0.0D, 1.0D);
      }

      return false;
   }
}
