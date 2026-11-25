package knightminer.inspirations.utility.client;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.utility.inventory.ContainerPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.gui.GuiMultiModule;

public class GuiPipe extends GuiMultiModule {
   private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/pipe.png");

   public GuiPipe(ContainerPipe container) {
      super(container);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.drawBackground(BACKGROUND);
      super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
   }

   protected void drawPlayerInventoryName() {
      String localizedName = Minecraft.getMinecraft().player.inventory.getDisplayName().getUnformattedText();
      this.fontRenderer.drawString(localizedName, 8, this.ySize - 129 + 2, 4210752);
   }
}
