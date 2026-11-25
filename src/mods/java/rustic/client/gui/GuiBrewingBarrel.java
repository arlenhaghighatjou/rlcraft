package rustic.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import rustic.client.util.FluidClientUtil;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.tileentity.ContainerBrewingBarrel;
import rustic.common.tileentity.TileEntityBrewingBarrel;

public class GuiBrewingBarrel extends GuiContainer {
   public static final int WIDTH = 176;
   public static final int HEIGHT = 166;
   private static final ResourceLocation background = new ResourceLocation("rustic", "textures/gui/brewing_barrel.png");
   private TileEntityBrewingBarrel te;

   public GuiBrewingBarrel(ContainerBrewingBarrel container, IInventory playerInv) {
      super(container);
      this.te = container.getTile();
      this.xSize = 176;
      this.ySize = 166;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      if (this.isPointInRegion(62, 27, 16, 32, mouseX, mouseY)) {
         this.drawFluidTooltip(this.te.getInputFluid(), this.te.getInputCapacity(), mouseX, mouseY);
      }

      if (this.isPointInRegion(116, 27, 16, 32, mouseX, mouseY)) {
         this.drawFluidTooltip(this.te.getOutputFluid(), this.te.getOutputCapacity(), mouseX, mouseY);
      }

      if (this.isPointInRegion(26, 35, 16, 16, mouseX, mouseY)) {
         this.drawFluidTooltip(this.te.getAuxiliaryFluid(), this.te.getAuxiliaryCapacity(), mouseX, mouseY);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(background);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      if (this.te.getInputAmount() > 0) {
         FluidClientUtil.drawGuiLiquid(new FluidStack(this.te.getInputFluid(), this.te.getInputAmount()), this.te.getInputCapacity(), this.guiLeft + 62, this.guiTop + 27, 16, 32, background);
      }

      if (this.te.getOutputAmount() > 0) {
         FluidClientUtil.drawGuiLiquid(new FluidStack(this.te.getOutputFluid(), this.te.getOutputAmount()), this.te.getOutputCapacity(), this.guiLeft + 116, this.guiTop + 27, 16, 32, background);
      }

      if (this.te.getAuxiliaryAmount() > 0) {
         FluidClientUtil.drawGuiLiquid(new FluidStack(this.te.getAuxiliaryFluid(), this.te.getAuxiliaryAmount()), this.te.getAuxiliaryCapacity(), this.guiLeft + 26, this.guiTop + 35, 16, 16, background);
      }

      if (this.te.slot0Empty()) {
         this.drawTexturedModalRect(this.guiLeft + 62, this.guiTop + 7, 176, 54, 16, 16);
      }

      if (this.te.slot1Empty()) {
         this.drawTexturedModalRect(this.guiLeft + 116, this.guiTop + 7, 176, 70, 16, 16);
      }

      if (this.te.slot2Empty()) {
         this.drawTexturedModalRect(this.guiLeft + 26, this.guiTop + 15, 176, 70, 16, 16);
      }

      if (this.te.isBrewing()) {
         int k = this.getBubbles();
         this.drawTexturedModalRect(this.guiLeft + 139, this.guiTop + 29 + 28 - k, 176, 28 - k, 11, k);
      }

      int l = this.getBrewProgressScaled(24);
      this.drawTexturedModalRect(this.guiLeft + 85, this.guiTop + 35, 176, 28, l, 16);
      int m = this.getBrewProgressScaled(10);
      this.drawTexturedModalRect(this.guiLeft + 45, this.guiTop + 38, 176, 44, m, 10);
   }

   private int getBrewProgressScaled(int pixels) {
      int i = this.te.getBrewTime();
      int j = this.te.getMaxBrewTime();
      return j != 0 && i != 0 ? i * pixels / j + 1 : 0;
   }

   private int getBubbles() {
      int i = this.te.getBrewTime();
      int j = this.te.getMaxBrewTime();
      return j != 0 && i != 0 ? (int)((float)i / (float)j * 5600.0F) % 28 : 0;
   }

   protected void drawFluidTooltip(FluidStack fluid, int capacity, int x, int y) {
      List<String> lines = new ArrayList();
      if (fluid != null && fluid.getFluid() != null && fluid.amount > 0) {
         lines.add(fluid.getFluid().getRarity(fluid).rarityColor + fluid.getLocalizedName());
         lines.add(TextFormatting.GRAY + "" + fluid.amount + "/" + capacity);
         if (fluid.getFluid() instanceof FluidBooze && fluid.tag != null && fluid.tag.hasKey("Quality", 5)) {
            float quality = fluid.tag.getFloat("Quality");
            lines.add(TextFormatting.GOLD + "" + I18n.format("tooltip.rustic.quality", new Object[0]) + quality);
         }
      } else {
         lines.add(TextFormatting.GRAY + I18n.format("tooltip.rustic.empty", new Object[0]));
         lines.add(TextFormatting.GRAY + "" + 0 + "/" + capacity);
      }

      this.drawHoveringText(lines, x - this.guiLeft, y - this.guiTop);
   }
}
