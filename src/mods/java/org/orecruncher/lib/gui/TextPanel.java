package org.orecruncher.lib.gui;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextPanel extends Panel<TextPanel> {
   private final FontRenderer font;
   private List<String> text;

   public TextPanel() {
      this.font = Minecraft.getMinecraft().fontRenderer;
      this.text = ImmutableList.of();
   }

   @Nonnull
   public TextPanel setText(@Nonnull List<String> text) {
      this.text = text;
      this.setHeight((text.size() + 1) * this.font.FONT_HEIGHT);
      int w = 0;

      for(String s : text) {
         w = Math.max(this.font.getStringWidth(s), w);
      }

      w = w + this.font.FONT_HEIGHT;
      this.setWidth(w);
      return this;
   }

   @Nonnull
   public TextPanel resetText() {
      this.text = ImmutableList.of();
      return this;
   }

   public boolean hasText() {
      return !this.text.isEmpty();
   }

   public void render(int locX, int locY, @Nonnull Panel.Reference ref) {
      if (!this.text.isEmpty()) {
         int posY;
         switch(ref) {
         case CENTER:
            posY = locY - this.getCenterY();
            break;
         case TOP_CENTER:
            posY = locY;
            break;
         default:
            posY = locY;
         }

         super.render(locX, locY, ref);
         GlStateManager.color(1.0F, 1.0F, 1.0F, this.alpha);
         GlStateManager.enableBlend();
         int textRGB = this.getForegroundColor().rgbWithAlpha(this.alpha);
         int drawX;
         if (ref != Panel.Reference.CENTER && ref != Panel.Reference.TOP_CENTER) {
            drawX = locX + this.getCenterX();
         } else {
            drawX = locX;
         }

         int drawY = posY + (this.font.FONT_HEIGHT + 1) / 2;

         for(int i = 0; i < this.text.size(); ++i) {
            GuiUtils.drawCenteredString(this.font, (String)this.text.get(i), drawX, drawY, textRGB);
            drawY += this.font.FONT_HEIGHT;
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
