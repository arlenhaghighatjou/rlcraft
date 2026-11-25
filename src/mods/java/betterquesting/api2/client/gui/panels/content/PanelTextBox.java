package betterquesting.api2.client.gui.panels.content;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class PanelTextBox implements IGuiPanel {
   private final PanelTextBox.GuiRectText transform;
   private final GuiRectangle refRect;
   private boolean enabled;
   private String text;
   private boolean shadow;
   private IGuiColor color;
   private final boolean autoFit;
   private int align;
   private int fontScale;
   private int lines;
   private final float relScale;

   public PanelTextBox(IGuiRect rect, String text) {
      this(rect, text, false);
   }

   public PanelTextBox(IGuiRect rect, String text, boolean autoFit) {
      this.refRect = new GuiRectangle(0, 0, 0, 0, 0);
      this.enabled = true;
      this.text = "";
      this.shadow = false;
      this.color = new GuiColorStatic(255, 255, 255, 255);
      this.align = 0;
      this.fontScale = 12;
      this.lines = 1;
      this.relScale = 12.0F;
      this.transform = new PanelTextBox.GuiRectText(rect, autoFit);
      this.setText(text);
      this.autoFit = autoFit;
   }

   public PanelTextBox setText(String text) {
      this.text = text;
      this.refreshText();
      return this;
   }

   public PanelTextBox setColor(IGuiColor color) {
      this.color = color;
      return this;
   }

   public PanelTextBox setAlignment(int align) {
      this.align = MathHelper.clamp(align, 0, 2);
      return this;
   }

   public PanelTextBox setFontSize(int size) {
      this.fontScale = size;
      return this;
   }

   public PanelTextBox enableShadow(boolean enable) {
      this.shadow = enable;
      return this;
   }

   private void refreshText() {
      IGuiRect bounds = this.getTransform();
      FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
      float scale = (float)this.fontScale / 12.0F;
      if (!this.autoFit) {
         this.lines = (int)Math.floor((double)((float)bounds.getHeight() / ((float)fr.FONT_HEIGHT * scale))) - 1;
      } else {
         int w = (int)Math.floor((double)((float)bounds.getWidth() / scale));
         List<String> sl = w > 8 ? fr.listFormattedStringToWidth(this.text, w) : Collections.emptyList();
         this.lines = sl.size() - 1;
         this.transform.h = (int)Math.floor((double)((float)(fr.FONT_HEIGHT * sl.size()) * scale));
         this.refRect.x = bounds.getX();
         this.refRect.y = bounds.getY();
         this.refRect.w = bounds.getWidth();
         this.refRect.h = bounds.getHeight();
      }
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void initPanel() {
      this.refreshText();
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (!this.isRectEqual(this.refRect, this.getTransform())) {
         this.refreshText();
      }

      if (this.lines >= 0) {
         IGuiRect bounds = this.getTransform();
         FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
         double s = (double)((float)this.fontScale / 12.0F);
         int bw = (int)Math.ceil((double)bounds.getWidth() / s);
         double w = Math.min((double)bw, (double)RenderUtils.getStringWidth(this.text, fr) * s);
         if (bw > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)bounds.getX(), (float)bounds.getY(), 1.0F);
            if (this.align == 1) {
               GlStateManager.translate((double)bounds.getWidth() / 2.0D - w / 2.0D, 0.0D, 0.0D);
            }

            if (this.align == 2) {
               GlStateManager.translate((double)bounds.getWidth() - w, 0.0D, 0.0D);
            }

            GlStateManager.scale(s, s, 1.0D);
            if (this.align == 2) {
               RenderUtils.drawSplitString(fr, this.text, 0, 0, bw, this.color.getRGB(), this.shadow, 0, this.lines);
            } else if (this.align == 1) {
               RenderUtils.drawSplitString(fr, this.text, 0, 0, bw, this.color.getRGB(), this.shadow, 0, this.lines);
            } else {
               RenderUtils.drawSplitString(fr, this.text, 0, 0, bw, this.color.getRGB(), this.shadow, 0, this.lines);
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public boolean onMouseClick(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyTyped(char c, int keycode) {
      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }

   private boolean isRectEqual(IGuiRect r1, IGuiRect r2) {
      return r1.getX() == r2.getX() && r1.getY() == r2.getY() && r1.getWidth() == r2.getWidth() && r1.getHeight() == r2.getHeight();
   }

   private static class GuiRectText implements IGuiRect {
      private final IGuiRect proxy;
      private final boolean useH;
      private int h;

      public GuiRectText(IGuiRect proxy, boolean useH) {
         this.proxy = proxy;
         this.useH = useH;
      }

      public int getX() {
         return this.proxy.getX();
      }

      public int getY() {
         return this.proxy.getY();
      }

      public int getWidth() {
         return this.proxy.getWidth();
      }

      public int getHeight() {
         return this.useH ? this.h : this.proxy.getHeight();
      }

      public int getDepth() {
         return this.proxy.getDepth();
      }

      public IGuiRect getParent() {
         return this.proxy.getParent();
      }

      public void setParent(IGuiRect rect) {
         this.proxy.setParent(rect);
      }

      public boolean contains(int x, int y) {
         int x1 = this.getX();
         int x2 = x1 + this.getWidth();
         int y1 = this.getY();
         int y2 = y1 + this.getHeight();
         return x >= x1 && x < x2 && y >= y1 && y < y2;
      }

      public int compareTo(@Nonnull IGuiRect o) {
         return this.proxy.compareTo(o);
      }
   }
}
