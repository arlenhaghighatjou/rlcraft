package xzeroair.trinkets.client.gui.entityPropertiesGui;

import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;

public class GuiPropertiesButton extends GuiButton {
   protected static ResourceLocation TEXTURE;
   private BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed;
   private BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre;
   private BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPost;

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPost, ResourceLocation buttonTex) {
      super(buttonId, x, y, width, height, buttonText);
      this.whenPressed = whenPressed;
      this.renderPre = renderPre;
      this.renderPost = renderPost;
      if (buttonTex != null) {
         TEXTURE = buttonTex;
      }

   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, buttonText, whenPressed, renderPre, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, buttonText, whenPressed, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, buttonText, (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPost) {
      this(buttonId, x, y, width, height, buttonText, whenPressed, renderPre, renderPost, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre) {
      this(buttonId, x, y, width, height, buttonText, whenPressed, renderPre, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed) {
      this(buttonId, x, y, width, height, buttonText, whenPressed, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, String buttonText) {
      this(buttonId, x, y, width, height, buttonText, (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, "", whenPressed, renderPre, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, "", whenPressed, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, ResourceLocation buttonTex) {
      this(buttonId, x, y, width, height, "", (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, buttonTex);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPost) {
      this(buttonId, x, y, width, height, "", whenPressed, renderPre, renderPost, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> renderPre) {
      this(buttonId, x, y, width, height, "", whenPressed, renderPre, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height, BiConsumer<GuiPropertiesButton, GuiPropertiesButton.ButtonFunctionWrapper> whenPressed) {
      this(buttonId, x, y, width, height, "", whenPressed, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public GuiPropertiesButton(int buttonId, int x, int y, int width, int height) {
      this(buttonId, x, y, width, height, "", (BiConsumer)null, (BiConsumer)null, (BiConsumer)null, (ResourceLocation)null);
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      boolean flag = mc.player.getRecipeBook().isGuiOpen();
      boolean pressed = flag ? false : super.mousePressed(mc, mouseX, mouseY);
      if (pressed && this.whenPressed != null) {
         this.whenPressed.accept(this, new GuiPropertiesButton.ButtonFunctionWrapper(mc, mouseX, mouseY, 0.0F));
      }

      return pressed;
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
         int i = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
         this.mouseDragged(mc, mouseX, mouseY);
         int j = 14737632;
         if (this.packedFGColour != 0) {
            j = this.packedFGColour;
         } else if (!this.enabled) {
            j = 10526880;
         } else if (this.hovered) {
            j = 16777120;
         }

         this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
         if (this.renderPost != null) {
            this.renderPost.accept(this, new GuiPropertiesButton.ButtonFunctionWrapper(mc, mouseX, mouseY, partialTicks));
         }
      }

   }

   public int mousedOver() {
      return this.getHoverState(this.hovered);
   }

   static {
      TEXTURE = BUTTON_TEXTURES;
   }

   public static class ButtonFunctionWrapper {
      public Minecraft mc;
      public int mouseX;
      public int mouseY;
      public float partialTicks;

      public ButtonFunctionWrapper(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         this.mc = mc;
         this.mouseX = mouseX;
         this.mouseY = mouseY;
         this.partialTicks = partialTicks;
      }
   }
}
