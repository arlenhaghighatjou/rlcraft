package xzeroair.trinkets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.trinketcontainer.OpenTrinketGui;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.baubles.BaublesHelperFunctions;
import xzeroair.trinkets.util.config.gui.ConfigGuiButtonShared;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class TrinketGuiButton extends GuiButton {
   public static ResourceLocation openButtonTex = null;
   public static ResourceLocation closeButtonTex = null;
   private final GuiContainer parentGui;
   private final ColorHelper color;

   public TrinketGuiButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height, String buttonText) {
      super(buttonId, x, parentGui.getGuiTop() + y, width, height, buttonText);
      this.parentGui = parentGui;
      this.color = new ColorHelper();
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      boolean flag = Minecraft.getMinecraft().player.getRecipeBook().isGuiOpen();
      boolean pressed = flag ? false : super.mousePressed(mc, mouseX - this.parentGui.getGuiLeft(), mouseY);
      boolean baublesLoaded = Loader.isModLoaded("baubles");
      if (pressed) {
         if (this.parentGui instanceof GuiInventory) {
            NetworkHandler var10000 = NetworkHandler.INSTANCE;
            NetworkHandler.sendToServer(new OpenTrinketGui());
         } else if (baublesLoaded) {
            BaublesHelperFunctions.mousePressedHelper(this.parentGui, this.id);
         } else if (this.id != 9999) {
            ((TrinketGui)this.parentGui).displayNormalInventory();
            NetworkHandler.sendToServer(new OpenTrinketGui(99));
         }
      }

      return pressed;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible && !Minecraft.getMinecraft().player.getRecipeBook().isGuiOpen()) {
         int x = this.x + this.parentGui.getGuiLeft();
         int ID = TrinketsConfig.CLIENT.GUI.button.ID;
         if (this.id == ID) {
            FontRenderer fontrenderer = mc.fontRenderer;
            this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GlStateManager.pushMatrix();
            openButtonTex = new ResourceLocation(TrinketsConfig.CLIENT.GUI.button.open.image);
            closeButtonTex = new ResourceLocation(TrinketsConfig.CLIENT.GUI.button.close.image);
            ConfigGuiButtonShared open = TrinketsConfig.CLIENT.GUI.button.open;
            ConfigGuiButtonShared close = TrinketsConfig.CLIENT.GUI.button.close;
            float[] rgbOpen = ColorHelper.getRGBColor(open.color);
            float[] rgbClose = ColorHelper.getRGBColor(close.color);
            if (k == 1) {
               if (this.parentGui instanceof TrinketGui) {
                  mc.getTextureManager().bindTexture(closeButtonTex);
                  DrawingHelper.Draw((double)x, (double)this.y, 0.0D, (float)close.x, (float)close.y, close.texWidth, close.texHeight, (double)close.width, (double)close.height, (float)close.texSizeWidth, (float)close.texSizeHeight, rgbClose[0], rgbClose[1], rgbClose[2], 1.0F);
               } else {
                  mc.getTextureManager().bindTexture(openButtonTex);
                  DrawingHelper.Draw((double)x, (double)this.y, 0.0D, (float)open.x, (float)open.y, open.texWidth, open.texHeight, (double)open.width, (double)open.height, (float)open.texSizeWidth, (float)open.texSizeHeight, rgbOpen[0], rgbOpen[1], rgbOpen[2], 1.0F);
               }
            } else {
               if (this.parentGui instanceof TrinketGui) {
                  mc.getTextureManager().bindTexture(openButtonTex);
                  DrawingHelper.Draw((double)x, (double)this.y, 0.0D, (float)open.x, (float)open.y, open.texWidth, open.texHeight, (double)open.width, (double)open.height, (float)open.texSizeWidth, (float)open.texSizeHeight, rgbOpen[0], rgbOpen[1], rgbOpen[2], 1.0F);
               } else {
                  mc.getTextureManager().bindTexture(closeButtonTex);
                  DrawingHelper.Draw((double)x, (double)this.y, 0.0D, (float)close.x, (float)close.y, close.texWidth, close.texHeight, (double)close.width, (double)close.height, (float)close.texSizeWidth, (float)close.texSizeHeight, rgbClose[0], rgbClose[1], rgbClose[2], 1.0F);
               }

               this.drawCenteredString(fontrenderer, I18n.format(this.displayString, new Object[0]), x + 5, this.y + this.height, 16777215);
            }

            GlStateManager.popMatrix();
         }

         if (Loader.isModLoaded("baubles") && this.id == 55) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BaublesHelperFunctions.getBaublesResourceLocation());
            this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 200.0F);
            if (k == 1) {
               this.drawTexturedModalRect(x, this.y, 200, 48, 10, 10);
            } else {
               this.drawTexturedModalRect(x, this.y, 210, 48, 10, 10);
               this.drawCenteredString(fontrenderer, I18n.format(this.displayString, new Object[0]), x + 5, this.y + this.height, 16777215);
            }

            GlStateManager.popMatrix();
         }

         this.mouseDragged(mc, mouseX, mouseY);
      }

   }
}
