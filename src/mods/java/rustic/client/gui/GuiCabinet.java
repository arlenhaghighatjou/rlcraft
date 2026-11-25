package rustic.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import rustic.common.tileentity.ContainerCabinet;
import rustic.common.tileentity.TileEntityCabinet;
import vazkii.quark.api.IChestButtonCallback;
import vazkii.quark.api.IItemSearchBar;

@InterfaceList({@Interface(
   modid = "quark",
   iface = "vazkii.quark.api.IItemSearchBar",
   striprefs = true
), @Interface(
   modid = "quark",
   iface = "vazkii.quark.api.IChestButtonCallback",
   striprefs = true
)})
public class GuiCabinet extends GuiContainer implements IItemSearchBar, IChestButtonCallback {
   public static final int WIDTH = 176;
   public static final int HEIGHT = 166;
   private static final ResourceLocation background = new ResourceLocation("rustic", "textures/gui/generic_27.png");
   private TileEntityCabinet te;
   private InventoryPlayer playerInv;

   public GuiCabinet(ContainerCabinet container, InventoryPlayer playerInv) {
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

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.mc.getTextureManager().bindTexture(background);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
      this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }

   public void onSearchBarAdded(GuiTextField guiTextField) {
      ++guiTextField.y;
   }

   public boolean onAddChestButton(GuiButton guiButton, int i) {
      return true;
   }
}
