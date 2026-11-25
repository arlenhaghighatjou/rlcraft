package de.Whitedraco.switchbow.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArrowButton extends GuiButton {
   private ResourceLocation BUTTON_TEXTURES;
   private int TexturePosX;
   private int TexturePosY;

   public ArrowButton(int buttonId, int x, int y, String buttonText) {
      super(buttonId, x, y, buttonText);
      this.BUTTON_TEXTURES = new ResourceLocation("switchbow:textures/gui/container/element/arrow.png");
   }

   public ArrowButton(int buttonId, int x, int y, int TexturPosX, int TexturPosY) {
      this(buttonId, x, y, 9, 7, TexturPosX, TexturPosY);
   }

   public ArrowButton(int buttonId, int x, int y, int widthIn, int heightIn, int TexturPosX, int TexturPosY) {
      super(buttonId, x, y, "");
      this.BUTTON_TEXTURES = new ResourceLocation("switchbow:textures/gui/container/element/arrow.png");
      this.enabled = true;
      this.visible = true;
      this.id = buttonId;
      this.x = x;
      this.y = y;
      this.width = widthIn;
      this.height = heightIn;
      this.TexturePosX = TexturPosX;
      this.TexturePosY = TexturPosY;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(this.BUTTON_TEXTURES);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int i = this.getHoverState(this.hovered) - 1;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, this.TexturePosX, this.TexturePosY + i * 7, this.width, this.height);
      }

   }
}
