package baubles.client.gui;

import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import baubles.common.network.PacketOpenNormalInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiBaublesButton extends GuiButton {
   private final GuiContainer parentGui;

   public GuiBaublesButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height, String buttonText) {
      super(buttonId, x, parentGui.getGuiTop() + y, width, height, buttonText);
      this.parentGui = parentGui;
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      boolean pressed = super.mousePressed(mc, mouseX - this.parentGui.getGuiLeft(), mouseY);
      if (pressed) {
         if (this.parentGui instanceof GuiInventory) {
            PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
         } else {
            ((GuiPlayerExpanded)this.parentGui).displayNormalInventory();
            PacketHandler.INSTANCE.sendToServer(new PacketOpenNormalInventory());
         }
      }

      return pressed;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         int x = this.x + this.parentGui.getGuiLeft();
         FontRenderer fontrenderer = mc.fontRenderer;
         mc.getTextureManager().bindTexture(GuiPlayerExpanded.background);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
         int k = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 200.0F);
         if (k == 1) {
            this.drawTexturedModalRect(x, this.y, 200, 48, 10, 10);
         } else {
            this.drawTexturedModalRect(x, this.y, 210, 48, 10, 10);
            this.drawCenteredString(fontrenderer, I18n.format(this.displayString, new Object[0]), x + 5, this.y + this.height, 16777215);
         }

         GlStateManager.popMatrix();
         this.mouseDragged(mc, mouseX, mouseY);
      }

   }
}
