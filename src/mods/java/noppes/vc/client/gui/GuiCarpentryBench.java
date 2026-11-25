package noppes.vc.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.vc.containers.ContainerCarpentryBench;
import noppes.vc.shared.gui.GuiButtonNop;
import noppes.vc.shared.gui.GuiContainerBasic;

public class GuiCarpentryBench extends GuiContainerBasic {
   private final ResourceLocation resource = new ResourceLocation("variedcommodities", "textures/gui/carpentry.png");
   private ContainerCarpentryBench container;
   private GuiButtonNop button;

   public GuiCarpentryBench(ContainerCarpentryBench container) {
      super(container);
      this.container = container;
      this.allowUserInput = false;
      this.closeOnEsc = true;
      this.ySize = 180;
   }

   public void initGui() {
      super.initGui();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(this.resource);
      int l = (this.width - this.xSize) / 2;
      int i1 = (this.height - this.ySize) / 2;
      String title = I18n.translateToLocal("tile.carpentry_bench.name");
      if (this.container.getType() == 1) {
         title = I18n.translateToLocal("tile.anvil.name");
      }

      this.drawTexturedModalRect(l, i1, 0, 0, this.xSize, this.ySize);
      super.drawGuiContainerBackgroundLayer(f, i, j);
      this.fontRenderer.drawString(title, this.guiLeft + 4, this.guiTop + 4, 4210752);
      this.fontRenderer.drawString(I18n.translateToLocal("container.inventory"), this.guiLeft + 4, this.guiTop + 87, 4210752);
   }

   public void save() {
   }
}
