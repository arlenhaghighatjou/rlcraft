package rustic.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import rustic.client.util.FluidClientUtil;
import rustic.common.tileentity.ContainerCondenser;
import rustic.common.tileentity.TileEntityCondenser;

public class GuiCondenser extends GuiContainer {
   public static final int WIDTH = 176;
   public static final int HEIGHT = 166;
   private static final ResourceLocation background = new ResourceLocation("rustic", "textures/gui/condenser.png");
   TileEntityCondenser te;
   InventoryPlayer playerInv;

   public GuiCondenser(ContainerCondenser container, InventoryPlayer playerInv) {
      super(container);
      this.playerInv = playerInv;
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
      this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
      this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
      if (this.isPointInRegion(133, 27, 16, 32, mouseX, mouseY)) {
         FluidStack fluid = null;
         if (this.te.getFluid() != null && this.te.getAmount() > 0) {
            fluid = new FluidStack(this.te.getFluid(), this.te.getAmount());
         }

         this.drawFluidTooltip(fluid, this.te.getCapacity(), mouseX, mouseY);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(background);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      if (this.te.getAmount() > 0) {
         FluidClientUtil.drawGuiLiquid(new FluidStack(this.te.getFluid(), this.te.getAmount()), this.te.getCapacity(), this.guiLeft + 133, this.guiTop + 27, 16, 32, background);
      }

      if (this.te.isBurning()) {
         int k = this.getBurnLeftScaled(13);
         this.drawTexturedModalRect(this.guiLeft + 67, this.guiTop + 46 + 12 - k, 176, 12 - k, 14, k + 1);
      }

      int l = this.getBrewProgressScaled(50);
      this.drawTexturedModalRect(this.guiLeft + 44, this.guiTop + 29, 176, 14, l, 28);
   }

   private int getBrewProgressScaled(int pixels) {
      int i = this.te.brewTime;
      int j = this.te.totalBrewTime;
      return j != 0 && i != 0 ? i * pixels / j + 1 : 0;
   }

   private int getBurnLeftScaled(int pixels) {
      int i = this.te.currentItemBurnTime;
      if (i == 0) {
         i = 200;
      }

      return this.te.condenserBurnTime * pixels / i;
   }

   protected void drawFluidTooltip(FluidStack fluid, int capacity, int x, int y) {
      List<String> lines = new ArrayList();
      if (fluid != null && fluid.getFluid() != null && fluid.amount > 0) {
         lines.add(fluid.getFluid().getRarity(fluid).rarityColor + fluid.getLocalizedName());
         lines.add(TextFormatting.GRAY + "" + fluid.amount + "/" + capacity);
      } else {
         lines.add(TextFormatting.GRAY + I18n.format("tooltip.rustic.empty", new Object[0]));
         lines.add(TextFormatting.GRAY + "" + 0 + "/" + capacity);
      }

      this.drawHoveringText(lines, x - this.guiLeft, y - this.guiTop);
   }
}
