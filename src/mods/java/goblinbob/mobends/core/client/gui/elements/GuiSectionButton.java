package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.gui.CustomFont;
import goblinbob.mobends.core.client.gui.CustomFontRenderer;
import goblinbob.mobends.core.util.Color;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.GuiHelper;
import goblinbob.mobends.core.util.IColorRead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiSectionButton {
   public static final ResourceLocation BUTTONS_TEXTURE = new ResourceLocation("mobends", "textures/gui/buttons.png");
   public static final float HOVER_ICON_ANIMATION_DURATION = 2.0F;
   public static final float HOVER_BG_ANIMATION_DURATION = 2.0F;
   protected final Minecraft mc;
   private String label;
   private int x;
   private int y;
   private int width;
   private int height;
   private int bgTextureU;
   private int bgTextureV;
   private Color neutralColor;
   private Color bgColor;
   private GuiSectionButton.SectionIcon leftIcon;
   private GuiSectionButton.SectionIcon rightIcon;
   private CustomFontRenderer fontRenderer;
   private boolean hover;
   private boolean pressed;
   private float ticksAfterHovered;

   public GuiSectionButton(int x, int y, String label, IColorRead bgColor) {
      this.leftIcon = null;
      this.rightIcon = null;
      this.ticksAfterHovered = 0.0F;
      this.mc = Minecraft.getMinecraft();
      this.label = label;
      this.x = x;
      this.y = y;
      this.width = 318;
      this.height = 43;
      this.bgTextureU = 0;
      this.bgTextureV = 0;
      this.neutralColor = new Color(-8947849);
      this.bgColor = new Color(bgColor);
      this.hover = false;
      this.pressed = false;
      this.fontRenderer = new CustomFontRenderer();
      this.fontRenderer.setFont(CustomFont.BOLD);
   }

   public GuiSectionButton(String label, IColorRead bgColor) {
      this(0, 0, label, bgColor);
   }

   public GuiSectionButton(String label, int bgColor) {
      this(0, 0, label, Color.fromHex(bgColor));
   }

   public GuiSectionButton setLeftIcon(int u, int v, int width, int height) {
      this.leftIcon = new GuiSectionButton.SectionIcon(u, v, width, height);
      return this;
   }

   public GuiSectionButton setRightIcon(int u, int v, int width, int height) {
      this.rightIcon = new GuiSectionButton.SectionIcon(u, v, width, height);
      return this;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      boolean nowHover = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
      if (nowHover && !this.hover) {
         this.onHover();
      }

      this.hover = nowHover;
   }

   public boolean mouseClicked(int mouseX, int mouseY, int event) {
      if (this.hover) {
         GuiHelper.playButtonSound(this.mc.getSoundHandler());
         this.pressed = true;
      }

      return this.pressed;
   }

   public void mouseReleased(int mouseX, int mouseY, int event) {
      this.pressed = false;
   }

   public void onHover() {
      this.ticksAfterHovered = 0.0F;
   }

   public void display() {
      this.ticksAfterHovered += DataUpdateHandler.ticksPerFrame;
      if (this.hover) {
         GlStateManager.color(this.bgColor.r, this.bgColor.g, this.bgColor.b, this.bgColor.a);
      } else {
         GlStateManager.color(this.neutralColor.r, this.neutralColor.g, this.neutralColor.b, this.neutralColor.a);
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(BUTTONS_TEXTURE);
      int tX = this.bgTextureU;
      int tY = this.bgTextureV;
      float uScale = 0.001953125F;
      float vScale = 0.0078125F;
      if (this.hover) {
         GlStateManager.color(this.bgColor.r, this.bgColor.g, this.bgColor.b, this.bgColor.a);
      } else {
         GlStateManager.color(this.neutralColor.r, this.neutralColor.g, this.neutralColor.b, this.neutralColor.a);
      }

      GlStateManager.disableTexture2D();
      Draw.rectangle((float)this.x, (float)this.y, (float)this.width, (float)this.height);
      GlStateManager.enableTexture2D();
      float bgt = 1.0F;
      if (this.hover) {
         if (this.ticksAfterHovered < 2.0F) {
            bgt = 1.0F - this.ticksAfterHovered / 2.0F;
            bgt = bgt * bgt * bgt;
         } else {
            bgt = 0.0F;
         }
      }

      int mountainOffsetY = (int)(bgt * 10.0F);
      Draw.texturedRectangle(this.x, this.y + mountainOffsetY, this.width, this.height - 2 - mountainOffsetY, (float)tX * uScale, (float)tY * vScale, (float)(tX + this.width) * uScale, (float)(tY + this.height - 2 - mountainOffsetY) * vScale);
      Draw.texturedRectangle(this.x, this.y + this.height - 2, this.width, 2, (float)tX * uScale, (float)(tY + this.height - 2) * vScale, (float)(tX + this.width) * uScale, (float)(tY + 2) * vScale);
      if (this.hover) {
         float scale = 1.0F;
         if (this.ticksAfterHovered < 2.0F) {
            float PI = 3.1415927F;
            float t = this.ticksAfterHovered / 2.0F;
            scale = 1.0F - MathHelper.cos(t * PI * 1.5F);
            scale = MathHelper.sqrt(scale);
         }

         int iconSpacing = 30;
         if (this.leftIcon != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(this.x + iconSpacing), (float)this.y + (float)this.height / 2.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            this.leftIcon.draw(uScale, vScale);
            GlStateManager.popMatrix();
         }

         if (this.rightIcon != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(this.x + this.width - iconSpacing), (float)this.y + (float)this.height / 2.0F, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            this.rightIcon.draw(uScale, vScale);
            GlStateManager.popMatrix();
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.fontRenderer.drawCenteredText(this.label, this.x + this.width / 2, this.y + this.height / 2 + 6);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void setPosition(int i, int j) {
      this.x = i;
      this.y = j;
   }

   private static class SectionIcon {
      private int texU;
      private int texV;
      private int texWidth;
      private int texHeight;

      public SectionIcon(int u, int v, int width, int height) {
         this.texU = u;
         this.texV = v;
         this.texWidth = width;
         this.texHeight = height;
      }

      public void draw(float uScale, float vScale) {
         Draw.texturedRectangle(-this.texWidth / 2, -this.texHeight / 2, this.texWidth, this.texHeight, (float)this.texU * uScale, (float)this.texV * vScale, (float)this.texWidth * uScale, (float)this.texHeight * vScale);
      }
   }
}
