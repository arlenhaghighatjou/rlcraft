package com.alcatrazescapee.alcatrazcore.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerCore extends GuiContainer {
   private final ResourceLocation background;
   private final String translationKey;
   protected InventoryPlayer playerInv;

   public GuiContainerCore(Container container, InventoryPlayer playerInv, ResourceLocation background, String titleKey) {
      super(container);
      this.playerInv = playerInv;
      this.background = background;
      this.translationKey = titleKey;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.drawSimpleForeground();
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.drawSimpleBackground();
   }

   protected final void drawSimpleForeground() {
      String name = I18n.format(this.translationKey + ".name", new Object[0]);
      this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
      this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 94, 4210752);
   }

   protected final void drawSimpleBackground() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(this.background);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
   }
}
