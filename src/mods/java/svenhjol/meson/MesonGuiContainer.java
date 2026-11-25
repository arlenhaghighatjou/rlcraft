package svenhjol.meson;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class MesonGuiContainer extends GuiContainer {
   private InventoryPlayer playerInv;
   private String displayName;
   private ResourceLocation BG_TEXTURE;

   public MesonGuiContainer(String displayName, MesonContainer container, InventoryPlayer playerInv, ResourceLocation texture) {
      super(container);
      this.displayName = I18n.format(displayName, new Object[0]);
      MesonTileInventory tile = container.getTile();
      if (tile != null && tile.hasCustomName()) {
         this.displayName = tile.getName();
      }

      this.playerInv = playerInv;
      this.BG_TEXTURE = texture;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(this.BG_TEXTURE);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      super.drawGuiContainerForegroundLayer(mouseX, mouseY);
      this.fontRenderer.drawString(this.displayName, 8, 6, 4210752);
      this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 94, 4210752);
   }
}
