package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Mouse;

public class ElementHoriSlider extends Element {
   public double prevSliderProg;
   public double sliderProg;
   public int spacerL;
   public int spacerR;
   public String tooltip;
   public String label;

   public ElementHoriSlider(Window window, int x, int y, int w, int ID, boolean igMin, String title) {
      this(window, x, y, w, ID, igMin, title, "");
   }

   public ElementHoriSlider(Window window, int x, int y, int w, int ID, boolean igMin, String title, String lab) {
      super(window, x, y, w, 12, ID, igMin);
      this.sliderProg = 1.0D;
      this.spacerL = x;
      this.spacerR = this.parent.width - x - this.width;
      this.tooltip = title;
      this.label = lab;
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      double x1 = (double)(this.getPosX() + 4);
      double x2 = x1 + (double)this.width - 8.0D;
      double y1 = (double)(this.getPosY() + 5);
      double y2 = y1 + (double)this.height - 10.0D;
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, x1, y1, x2 - x1, y2 - y1, 0.0D);
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)this.getPosX() + (x2 - (double)this.parent.workspace.getFontRenderer().getStringWidth(this.label) - x1) * this.sliderProg, (double)this.getPosY(), (double)(8 + this.parent.workspace.getFontRenderer().getStringWidth(this.label)), (double)this.height, 0.0D);
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBar[0], this.parent.workspace.currentTheme.elementTreeScrollBar[1], this.parent.workspace.currentTheme.elementTreeScrollBar[2], 255, (double)this.getPosX() + (x2 - (double)this.parent.workspace.getFontRenderer().getStringWidth(this.label) - x1) * this.sliderProg + 1.0D, (double)(this.getPosY() + 1), (double)(6 + this.parent.workspace.getFontRenderer().getStringWidth(this.label)), (double)(this.height - 2), 0.0D);
      if (!this.label.isEmpty()) {
         this.parent.workspace.getFontRenderer().drawString(I18n.translateToLocal(this.label), (float)((double)this.getPosX() + (x2 - (double)this.parent.workspace.getFontRenderer().getStringWidth(this.label) - x1) * this.sliderProg) + 4.0F, (float)(this.getPosY() + this.height - this.height / 2 - this.parent.workspace.getFontRenderer().FONT_HEIGHT / 2), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
      }

      if (this.parent.workspace.elementDragged == this && Mouse.isButtonDown(0) && mouseX >= this.posX && mouseX <= this.posX + this.width && mouseY >= this.posY && mouseY <= this.posY + this.height) {
         if (this.parent.workspace.elementSelected != null && this.parent.workspace.elementSelected != this) {
            this.parent.workspace.elementSelected.deselected();
         }

         double sx1 = (double)(this.posX + 4);
         double sx2 = (double)(this.posX + this.width - 8);
         this.sliderProg = MathHelper.clamp(((double)mouseX - sx1) / (sx2 - sx1), 0.0D, 1.0D);
         if (this.sliderProg != this.prevSliderProg) {
            this.parent.elementTriggered(this);
         }
      }

      this.prevSliderProg = this.sliderProg;
   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      this.sliderProg = MathHelper.clamp(this.sliderProg + (double)(GuiScreen.isShiftKeyDown() ? k * 10 : k) * 0.001D, 0.0D, 1.0D);
      if (this.sliderProg != this.prevSliderProg) {
         this.parent.elementTriggered(this);
      }

      return true;
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      return id == 0;
   }

   public void resized() {
      this.posX = this.spacerL;
      this.width = this.parent.width - this.posX - this.spacerR;
   }

   public String tooltip() {
      return this.tooltip;
   }
}
