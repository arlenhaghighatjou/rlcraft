package net.mcft.copy.backpacks.client;

import net.mcft.copy.backpacks.container.ContainerBackpack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBackpack extends GuiContainer {
   private static final GuiTextureResource CONTAINER_TEX = new GuiTextureResource("backpack", 512, 512);
   private final ContainerBackpack _container;

   public GuiBackpack(ContainerBackpack container) {
      super(container);
      this._container = container;
      this.xSize = container.getWidth();
      this.ySize = container.getHeight();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String title = this._container.titleLocalized ? this._container.title : I18n.format(this._container.title, new Object[0]);
      this.fontRenderer.drawString(title, this._container.getBorderSide() + 1, 6, 4210752);
      int invTitleX = this._container.getPlayerInvXOffset() + 1;
      int invTitleY = this._container.getBorderTop() + this._container.getContainerInvHeight() + 3;
      this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), invTitleX, invTitleY, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      int b = this._container.getBorderSide();
      int bTop = this._container.getBorderTop();
      int bBot = this._container.getBorderBottom();
      int w = this.xSize - b * 2;
      int h = this._container.getContainerInvHeight();
      int pw = this._container.getPlayerInvWidth();
      int ph = this._container.getPlayerInvHeight();
      int maxw = this._container.getMaxColumns() * 18;
      int maxh = this._container.getMaxRows() * 18;
      int bufi = this._container.getBufferInventory();
      int x2 = x + b;
      int x3 = x + this.xSize - b;
      int px = x + this._container.getPlayerInvXOffset();
      int tx1 = 4;
      int tx2 = tx1 + b + 2;
      int tx3 = tx2 + maxw + 2;
      int ty = 4;
      int tpx = 2 + b + 2 + (maxw - (pw + b * 2)) / 2 + 2;
      CONTAINER_TEX.bind();
      CONTAINER_TEX.drawQuad(x, y, tx1, ty, b, bTop);
      CONTAINER_TEX.drawQuad(x2, y, tx2, ty, w, bTop);
      CONTAINER_TEX.drawQuad(x3, y, tx3, ty, b, bTop);
      y = y + bTop;
      ty = ty + bTop + 2;
      CONTAINER_TEX.drawQuad(x, y, tx1, ty, b, h);
      CONTAINER_TEX.drawQuad(x2, y, tx2, ty, w, h);
      CONTAINER_TEX.drawQuad(x3, y, tx3, ty, b, h);
      CONTAINER_TEX.drawQuad(x + this._container.getContainerInvXOffset(), y, tx2, 256, this._container.getContainerInvWidth(), h);
      y = y + h;
      ty = ty + maxh + 2;
      if (this._container.size.getColumns() > 9) {
         int sw = (w - (pw + b * 2)) / 2;
         CONTAINER_TEX.drawQuad(x, y, tx1 - 2, ty, b, bBot);
         CONTAINER_TEX.drawQuad(x2, y, tx1 + b, ty, sw, bBot);
         CONTAINER_TEX.drawQuad(px - b, y, tpx, ty, pw + b * 2, bufi);
         CONTAINER_TEX.drawQuad(px + pw + b, y, tx3 - sw, ty, sw, bBot);
         CONTAINER_TEX.drawQuad(x3, y, tx3 + 2, ty, b, bBot);
      }

      ty = ty + bufi + 2;
      if (this._container.size.getColumns() <= 9) {
         CONTAINER_TEX.drawQuad(x, y, tpx, ty, pw + b * 2, bufi);
      }

      y = y + bufi;
      ty = ty + bufi + 2;
      CONTAINER_TEX.drawQuad(px - b, y, tpx, ty, pw + b * 2, ph + bBot);
   }
}
