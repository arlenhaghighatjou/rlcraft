package lumien.custommainmenu.gui;

import java.util.List;
import lumien.custommainmenu.configuration.elements.Button;
import lumien.custommainmenu.lib.StringReplacer;
import lumien.custommainmenu.lib.textures.ITexture;
import lumien.custommainmenu.util.LogicUtil;
import lumien.custommainmenu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class GuiCustomButton extends GuiButton {
   public Button b;
   ITexture texture;
   int normalText;
   int hoverText;

   public GuiCustomButton(int buttonId, Button b) {
      super(buttonId, b.posX, b.posY, b.width, b.height, I18n.format(b.text.get(), new Object[0]));
      this.texture = b.texture;
      this.normalText = b.normalTextColor;
      this.hoverText = b.hoverTextColor;
      this.b = b;
   }

   public void drawTooltip(Minecraft mc, int mouseX, int mouseY) {
      FontRenderer fontrenderer = mc.fontRenderer;
      if (this.hovered && this.b.tooltip != null) {
         this.drawHoveringText(mc, LogicUtil.getTooltip(this.b.tooltip.get()), mouseX, mouseY, fontrenderer);
      }

   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         if (this.b.name.equals("language") && this.texture == null) {
            mc.getTextureManager().bindTexture(GuiButton.BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovering = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = 106;
            if (hovering) {
               k += this.height;
            }

            this.drawTexturedModalRect(this.x, this.y, 0, k, this.width, this.height);
            return;
         }

         FontRenderer fontrenderer = mc.fontRenderer;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean newHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         if (newHovered && !this.hovered && this.b.hoverSound != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(this.b.hoverSound), SoundCategory.MASTER, 1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
         }

         this.hovered = newHovered;
         int k = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.blendFunc(770, 771);
         if (this.texture != null) {
            this.texture.bind();
            RenderUtil.drawPartialImage(this.texture, this.x, this.y, 0, (k - 1) * this.b.imageHeight, this.b.width, this.b.height, this.b.imageWidth, this.b.imageHeight);
         } else {
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
         }

         this.mouseDragged(mc, mouseX, mouseY);
         int l = this.normalText;
         if (this.packedFGColour != 0) {
            l = this.packedFGColour;
         } else if (!this.enabled) {
            l = 10526880;
         } else if (this.hovered) {
            l = this.hoverText;
         }

         this.drawCenteredString(fontrenderer, this.hovered ? I18n.format(StringReplacer.replacePlaceholders(this.b.hoverText.get()), new Object[0]) : I18n.format(StringReplacer.replacePlaceholders(this.b.text.get()), new Object[0]), this.x + this.width / 2 + this.b.textOffsetX, this.y + (this.height - 8) / 2 + this.b.textOffsetY, l, this.b.shadow);
      }

   }

   protected void drawHoveringText(Minecraft mc, List<String> textLines, int x, int y, FontRenderer font) {
      if (!textLines.isEmpty()) {
         int width = mc.currentScreen.width;
         int height = mc.currentScreen.height;
         GlStateManager.disableDepth();
         int k = 0;

         for(String s : textLines) {
            int l = font.getStringWidth(s);
            if (l > k) {
               k = l;
            }
         }

         int j2 = x + 12;
         int k2 = y - 12;
         int i1 = 8;
         if (textLines.size() > 1) {
            i1 += 2 + (textLines.size() - 1) * 10;
         }

         if (j2 + k > width) {
            j2 -= 28 + k;
         }

         if (k2 + i1 + 6 > height) {
            k2 = this.height - i1 - 6;
         }

         this.zLevel = 300.0F;
         int j1 = -267386864;
         this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
         this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
         this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
         this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
         this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
         int k1 = 1347420415;
         int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
         this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
         this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
         this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
         this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

         for(int i2 = 0; i2 < textLines.size(); ++i2) {
            String s1 = (String)textLines.get(i2);
            font.drawStringWithShadow(s1, (float)j2, (float)k2, -1);
            if (i2 == 0) {
               k2 += 2;
            }

            k2 += 10;
         }

         this.zLevel = 0.0F;
         GlStateManager.enableDepth();
      }

   }

   public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color, boolean shadow) {
      if (shadow) {
         fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
      } else {
         fontRendererIn.drawString(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color, false);
      }

   }

   public void playPressSound(SoundHandler soundHandlerIn) {
      if (this.b.pressSound != null) {
         soundHandlerIn.playSound(new PositionedSoundRecord(new ResourceLocation(this.b.pressSound), SoundCategory.MASTER, 1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
      } else {
         super.playPressSound(soundHandlerIn);
      }

   }
}
