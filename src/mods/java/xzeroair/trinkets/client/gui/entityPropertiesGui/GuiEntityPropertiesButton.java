package xzeroair.trinkets.client.gui.entityPropertiesGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.gui.ConfigGuiButtonShared;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class GuiEntityPropertiesButton extends GuiButton {
   public static ResourceLocation buttonTex = null;
   private final GuiContainer parentGui;

   public GuiEntityPropertiesButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height, String buttonText) {
      super(buttonId, x, parentGui.getGuiTop() + y, width, height, buttonText);
      this.parentGui = parentGui;
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      boolean flag = mc.player.getRecipeBook().isGuiOpen();
      boolean pressed = flag ? false : super.mousePressed(mc, mouseX - this.parentGui.getGuiLeft(), mouseY);
      if (pressed) {
         mc.player.openGui(Trinkets.instance, 2, mc.player.world, 0, 0, 0);
      }

      return pressed;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible && !mc.player.getRecipeBook().isGuiOpen()) {
         int x = this.x + this.parentGui.getGuiLeft();
         int ID = TrinketsConfig.CLIENT.GUI.button.ID;
         if (this.id == 69) {
            FontRenderer fontrenderer = mc.fontRenderer;
            this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GlStateManager.pushMatrix();
            ConfigGuiButtonShared config = TrinketsConfig.CLIENT.raceProperties.button.texture;
            buttonTex = new ResourceLocation(TrinketsConfig.CLIENT.raceProperties.button.texture.image);
            int X = config.x;
            int Y = config.y;
            int width = config.width;
            int height = config.height;
            int texWidth = config.texWidth;
            int texHeight = config.texHeight;
            float[] rgb = ColorHelper.getRGBColor(TrinketsConfig.CLIENT.raceProperties.button.texture.color);
            if (k == 1) {
               mc.getTextureManager().bindTexture(buttonTex);
               DrawingHelper.Draw((double)x, (double)this.y, 0.0D, (float)X, (float)Y, texWidth, texHeight, (double)width, (double)height, (float)config.texSizeWidth, (float)config.texSizeHeight, rgb[0], rgb[1], rgb[2], 1.0F);
            } else {
               mc.getTextureManager().bindTexture(buttonTex);
               DrawingHelper.Draw((double)x, (double)this.y, 0.0D, (float)X, (float)(Y + texHeight), texWidth, texHeight, (double)width, (double)height, (float)config.texSizeWidth, (float)config.texSizeHeight, rgb[0], rgb[1], rgb[2], 1.0F);
               this.drawCenteredString(fontrenderer, I18n.format(this.displayString, new Object[0]), x + 5, this.y + height, 16777215);
            }

            GlStateManager.popMatrix();
         }

         this.mouseDragged(mc, mouseX, mouseY);
      }

   }
}
