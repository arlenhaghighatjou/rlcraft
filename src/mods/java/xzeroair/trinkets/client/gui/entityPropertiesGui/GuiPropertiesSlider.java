package xzeroair.trinkets.client.gui.entityPropertiesGui;

import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xzeroair.trinkets.util.helpers.ColorHelper;

public class GuiPropertiesSlider extends GuiButton {
   protected static ResourceLocation TEXTURE;
   public float sliderValue;
   public float sliderMaxValue;
   public float sliderMinValue;
   public boolean dragging;
   GuiEntityProperties gui;
   GuiTextField field;
   public ColorHelper color;
   private BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed;
   private BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre;
   private BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPost;

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPost, ResourceLocation buttonTex) {
      super(buttonId, x, y, width, height, buttonText + ": " + (int)(startingValue * maxValue * 255.0F));
      this.sliderValue = 1.0F;
      this.sliderMaxValue = 1.0F;
      this.sliderMinValue = 1.0F;
      this.dragging = false;
      this.sliderValue = startingValue;
      this.sliderMaxValue = maxValue;
      this.sliderMinValue = minValue;
      this.whenPressed = whenPressed;
      this.renderPre = renderPre;
      this.renderPost = renderPost;
      if (buttonTex != null) {
         TEXTURE = buttonTex;
      }

   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, whenPressed, renderPre, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, whenPressed, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPost) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, whenPressed, renderPre, renderPost, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, whenPressed, renderPre, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, whenPressed, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, String buttonText, float startingValue, float maxValue, float minValue) {
      this(buttonId, x, y, width, height, buttonText, startingValue, maxValue, minValue, (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, whenPressed, renderPre, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, whenPressed, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPost) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, whenPressed, renderPre, renderPost, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> renderPre) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, whenPressed, renderPre, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue, BiConsumer<GuiPropertiesSlider, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, whenPressed, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesSlider(int buttonId, int x, int y, int width, int height, float startingValue, float maxValue, float minValue) {
      this(buttonId, x, y, width, height, "", startingValue, maxValue, minValue, (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public float getSliderValue() {
      return this.sliderValue;
   }

   protected int getHoverState(boolean par1) {
      return 0;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         if (this.renderPre != null) {
            this.renderPre.accept(this, new GuiPropertiesButton.ButtonFunctionWrapper(mc, mouseX, mouseY, partialTicks));
         }

         FontRenderer fontrenderer = mc.fontRenderer;
         mc.getTextureManager().bindTexture(TEXTURE);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int k = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.blendFunc(770, 771);
         this.drawTexturedModalRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
         this.mouseDragged(mc, mouseX, mouseY);
         int l = 14737632;
         if (this.packedFGColour != 0) {
            l = this.packedFGColour;
         } else if (!this.enabled) {
            l = 10526880;
         } else if (this.hovered) {
            l = 16777120;
         }

         this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
         if (this.renderPost != null) {
            this.renderPost.accept(this, new GuiPropertiesButton.ButtonFunctionWrapper(mc, mouseX, mouseY, partialTicks));
         }
      }

   }

   protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      if (this.enabled && this.visible && this.packedFGColour == 0) {
         if (this.dragging) {
            this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            if (this.sliderValue < 0.0F) {
               this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
               this.sliderValue = 1.0F;
            }

            if (this.whenPressed != null) {
               this.whenPressed.accept(this, new GuiPropertiesButton.ButtonFunctionWrapper(mc, mouseX, mouseY, 0.0F));
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
      }

   }

   public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
      if (super.mousePressed(par1Minecraft, par2, par3)) {
         this.sliderValue = (float)(par2 - (this.x + 4)) / (float)(this.width - 8);
         if (this.sliderValue < 0.0F) {
            this.sliderValue = 0.0F;
         }

         if (this.sliderValue > 1.0F) {
            this.sliderValue = 1.0F;
         }

         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   public void mouseReleased(int par1, int par2) {
      this.dragging = false;
   }

   static {
      TEXTURE = BUTTON_TEXTURES;
   }
}
