package net.mcft.copy.backpacks.client.gui.control;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonGlyph extends GuiButton {
   public static final int GLYPH_SPACING = getStringWidth(" ");
   public static final int DEFAULT_WIDTH = 20;
   public static final float DEFAULT_GLYPH_SCALE = 2.0F;
   private String _glyph;
   private float _glyphScale;
   private int _glyphWidth;

   public GuiButtonGlyph(String glyph) {
      this(glyph, 2.0F);
   }

   public GuiButtonGlyph(String glyph, float glyphScale) {
      this(20, glyph, glyphScale);
   }

   public GuiButtonGlyph(int width, String glyph, float glyphScale) {
      this(width, 20, glyph, glyphScale);
   }

   public GuiButtonGlyph(int width, int height, String glyph, float glyphScale) {
      this(width, height, glyph, glyphScale, "");
   }

   public GuiButtonGlyph(String glyph, String text) {
      this((int)((float)getStringWidth(glyph) * 2.0F) + GLYPH_SPACING + getStringWidth(text) + 20, glyph, text);
   }

   public GuiButtonGlyph(int width, String glyph, String text) {
      this(width, 20, glyph, text);
   }

   public GuiButtonGlyph(int width, int height, String glyph, String text) {
      this(width, height, glyph, 2.0F, text);
   }

   public GuiButtonGlyph(int width, String glyph, float glyphScale, String text) {
      this(width, 20, glyph, glyphScale, text);
   }

   public GuiButtonGlyph(int width, int height, String glyph, float glyphScale, String text) {
      this(0, 0, width, height, glyph, glyphScale, text);
   }

   public GuiButtonGlyph(int x, int y, int width, int height, String glyph, float glyphScale, String text) {
      super(x, y, width, height, text);
      this.setGlyph(glyph, glyphScale);
   }

   public String getGlyph() {
      return this._glyph;
   }

   public float getGlyphScale() {
      return this._glyphScale;
   }

   public int getGlyphWidth() {
      return this._glyphWidth;
   }

   public void setGlyph(String glyph) {
      this.setGlyph(glyph, this.getGlyphScale());
   }

   public void setGlyph(String glyph, float glyphScale) {
      this._glyph = glyph;
      this._glyphScale = glyphScale;
      this._glyphWidth = (int)((float)getStringWidth(glyph) * glyphScale);
   }

   protected void drawButtonForeground(boolean isHighlighted, float partialTicks) {
      String text = this.getText();
      FontRenderer fontRenderer = getFontRenderer();
      int contentWidth = this.getGlyphWidth();
      if (!text.isEmpty()) {
         int textWidth = fontRenderer.getStringWidth(text);
         int maxTextWidth = this.getWidth() - this.getGlyphWidth() - GLYPH_SPACING - 6;
         if (textWidth > maxTextWidth && textWidth > ELLIPSIS_WIDTH) {
            text = fontRenderer.trimStringToWidth(text, maxTextWidth - ELLIPSIS_WIDTH).trim() + "...";
            textWidth = fontRenderer.getStringWidth(text);
         }

         contentWidth += GLYPH_SPACING + textWidth;
      }

      float scale = this.getGlyphScale();
      GlStateManager.pushMatrix();
      GlStateManager.scale(scale, scale, 1.0F);
      fontRenderer.drawStringWithShadow(this.getGlyph(), (float)((int)((float)((this.getWidth() - contentWidth) / 2) / scale)), (float)((int)(((float)(this.getHeight() - 8) / scale / 2.0F - 1.0F) / scale)), this.getTextColor(isHighlighted));
      GlStateManager.popMatrix();
      if (!text.isEmpty()) {
         fontRenderer.drawStringWithShadow(text, (float)((this.getWidth() - contentWidth) / 2 + this.getGlyphWidth() + GLYPH_SPACING), (float)((this.getHeight() - 8) / 2), this.getTextColor(isHighlighted));
      }

   }
}
