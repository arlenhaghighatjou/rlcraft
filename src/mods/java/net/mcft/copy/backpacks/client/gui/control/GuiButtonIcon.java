package net.mcft.copy.backpacks.client.gui.control;

import net.mcft.copy.backpacks.client.GuiTextureResource;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonIcon extends GuiButton {
   public static final int ICON_SPACING = getStringWidth(" ");
   public static final int DEFAULT_WIDTH = 20;
   private GuiButtonIcon.Icon _icon;

   public GuiButtonIcon(GuiButtonIcon.Icon icon) {
      this(20, icon);
   }

   public GuiButtonIcon(int width, GuiButtonIcon.Icon icon) {
      this(width, 20, icon);
   }

   public GuiButtonIcon(int width, int height, GuiButtonIcon.Icon icon) {
      this(width, height, icon, "");
   }

   public GuiButtonIcon(GuiButtonIcon.Icon icon, String text) {
      this(icon.width + ICON_SPACING + getStringWidth(text) + 20, icon, text);
   }

   public GuiButtonIcon(int width, GuiButtonIcon.Icon icon, String text) {
      this(width, 20, icon, text);
   }

   public GuiButtonIcon(int width, int height, GuiButtonIcon.Icon icon, String text) {
      this(0, 0, width, height, icon, text);
   }

   public GuiButtonIcon(int x, int y, int width, int height, GuiButtonIcon.Icon icon, String text) {
      super(x, y, width, height, text);
      this.setIcon(icon);
   }

   public void setIcon(GuiButtonIcon.Icon icon) {
      this._icon = icon;
   }

   protected void drawButtonForeground(boolean isHighlighted, float partialTicks) {
      String text = this.getText();
      FontRenderer fontRenderer = getFontRenderer();
      int contentWidth = this._icon.width;
      if (!text.isEmpty()) {
         int textWidth = fontRenderer.getStringWidth(text);
         int maxTextWidth = this.getWidth() - this._icon.width - ICON_SPACING - 6;
         if (textWidth > maxTextWidth && textWidth > ELLIPSIS_WIDTH) {
            text = fontRenderer.trimStringToWidth(text, maxTextWidth - ELLIPSIS_WIDTH).trim() + "...";
            textWidth = fontRenderer.getStringWidth(text);
         }

         contentWidth += ICON_SPACING + textWidth;
      }

      int x = (this.getWidth() - contentWidth) / 2;
      int y = (this.getHeight() - this._icon.height) / 2;
      this._icon.texture.bind();
      this._icon.texture.drawQuad(x, y, this._icon.u, this._icon.v, this._icon.width, this._icon.height);
      if (!text.isEmpty()) {
         fontRenderer.drawStringWithShadow(text, (float)(x + this._icon.width + ICON_SPACING), (float)((this.getHeight() - 8) / 2), this.getTextColor(isHighlighted));
      }

   }

   public static final class Icon {
      public final GuiTextureResource texture;
      public final int u;
      public final int v;
      public final int width;
      public final int height;

      public Icon(GuiTextureResource texture, int u, int v, int width, int height) {
         this.texture = texture;
         this.u = u;
         this.v = v;
         this.width = width;
         this.height = height;
      }
   }
}
