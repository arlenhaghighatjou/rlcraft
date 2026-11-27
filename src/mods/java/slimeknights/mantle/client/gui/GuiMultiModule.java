package slimeknights.mantle.client.gui;

import com.google.common.collect.Lists;
import com.arlenh7.eaglercraft.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.mantle.inventory.SlotWrapper;

@SideOnly(Side.CLIENT)
@Interface(
   iface = "codechicken.nei.api.INEIGuiHandler",
   modid = "NotEnoughItems"
)
public class GuiMultiModule extends GuiContainer {
   protected List<GuiModule> modules = Lists.newArrayList();
   public int cornerX;
   public int cornerY;
   public int realWidth = -1;
   public int realHeight = -1;

   public GuiMultiModule(ContainerMultiModule container) {
      super(container);
   }

   protected void addModule(GuiModule module) {
      this.modules.add(module);
   }

   public List<Rectangle> getModuleAreas() {
      List<Rectangle> areas = new ArrayList(this.modules.size());

      for(GuiModule module : this.modules) {
         areas.add(module.getArea());
      }

      return areas;
   }

   public void initGui() {
      if (this.realWidth > -1) {
         this.xSize = this.realWidth;
         this.ySize = this.realHeight;
      }

      super.initGui();
      this.cornerX = this.guiLeft;
      this.cornerY = this.guiTop;
      this.realWidth = this.xSize;
      this.realHeight = this.ySize;

      for(GuiModule module : this.modules) {
         this.updateSubmodule(module);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      for(GuiModule module : this.modules) {
         module.handleDrawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      }

   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.drawContainerName();
      this.drawPlayerInventoryName();

      for(GuiModule module : this.modules) {
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)(-this.guiLeft), (float)(-this.guiTop), 0.0F);
         GlStateManager.translate((float)module.guiLeft, (float)module.guiTop, 0.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         module.handleDrawGuiContainerForegroundLayer(mouseX, mouseY);
         GlStateManager.popMatrix();
      }

   }

   protected void drawBackground(ResourceLocation background) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(background);
      this.drawTexturedModalRect(this.cornerX, this.cornerY, 0, 0, this.realWidth, this.realHeight);
   }

   protected void drawContainerName() {
      ContainerMultiModule multiContainer = (ContainerMultiModule)this.inventorySlots;
      String localizedName = multiContainer.getInventoryDisplayName();
      if (localizedName != null) {
         this.fontRenderer.drawString(localizedName, 8, 6, 4210752);
      }

   }

   protected void drawPlayerInventoryName() {
      String localizedName = Minecraft.getMinecraft().player.inventory.getDisplayName().getUnformattedText();
      this.fontRenderer.drawString(localizedName, 8, this.ySize - 96 + 2, 4210752);
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      super.setWorldAndResolution(mc, width, height);

      for(GuiModule module : this.modules) {
         module.setWorldAndResolution(mc, width, height);
         this.updateSubmodule(module);
      }

   }

   public void onResize(@Nonnull Minecraft mc, int width, int height) {
      super.onResize(mc, width, height);

      for(GuiModule module : this.modules) {
         module.onResize(mc, width, height);
         this.updateSubmodule(module);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int oldX = this.guiLeft;
      int oldY = this.guiTop;
      int oldW = this.xSize;
      int oldH = this.ySize;
      this.guiLeft = this.cornerX;
      this.guiTop = this.cornerY;
      this.xSize = this.realWidth;
      this.ySize = this.realHeight;
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
      this.guiLeft = oldX;
      this.guiTop = oldY;
      this.xSize = oldW;
      this.ySize = oldH;
   }

   protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
      pointX = pointX - this.cornerX;
      pointY = pointY - this.cornerY;
      return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
   }

   protected void updateSubmodule(GuiModule module) {
      module.updatePosition(this.cornerX, this.cornerY, this.realWidth, this.realHeight);
      if (module.guiLeft < this.guiLeft) {
         this.xSize += this.guiLeft - module.guiLeft;
         this.guiLeft = module.guiLeft;
      }

      if (module.guiTop < this.guiTop) {
         this.ySize += this.guiTop - module.guiTop;
         this.guiTop = module.guiTop;
      }

      if (module.guiRight() > this.guiLeft + this.xSize) {
         this.xSize = module.guiRight() - this.guiLeft;
      }

      if (module.guiBottom() > this.guiTop + this.ySize) {
         this.ySize = module.guiBottom() - this.guiTop;
      }

   }

   public void drawSlot(Slot slotIn) {
      GuiModule module = this.getModuleForSlot(slotIn.slotNumber);
      if (module != null) {
         Slot slot = slotIn;
         if (slotIn instanceof SlotWrapper) {
            slot = ((SlotWrapper)slotIn).parent;
         }

         if (!module.shouldDrawSlot(slot)) {
            return;
         }
      }

      if (slotIn instanceof SlotWrapper) {
         slotIn.xPos = ((SlotWrapper)slotIn).parent.xPos;
         slotIn.yPos = ((SlotWrapper)slotIn).parent.yPos;
      }

      super.drawSlot(slotIn);
   }

   public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
      GuiModule module = this.getModuleForSlot(slotIn.slotNumber);
      if (module != null) {
         Slot slot = slotIn;
         if (slotIn instanceof SlotWrapper) {
            slot = ((SlotWrapper)slotIn).parent;
         }

         if (!module.shouldDrawSlot(slot)) {
            return false;
         }
      }

      return super.isMouseOverSlot(slotIn, mouseX, mouseY);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      GuiModule module = this.getModuleForPoint(mouseX, mouseY);
      if (module == null || !module.handleMouseClicked(mouseX, mouseY, mouseButton)) {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      GuiModule module = this.getModuleForPoint(mouseX, mouseY);
      if (module == null || !module.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
         super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      GuiModule module = this.getModuleForPoint(mouseX, mouseY);
      if (module == null || !module.handleMouseReleased(mouseX, mouseY, state)) {
         super.mouseReleased(mouseX, mouseY, state);
      }
   }

   protected GuiModule getModuleForPoint(int x, int y) {
      for(GuiModule module : this.modules) {
         if (this.isPointInRegion(module.guiLeft, module.guiTop, module.guiRight(), module.guiBottom(), x + this.cornerX, y + this.cornerY)) {
            return module;
         }
      }

      return null;
   }

   protected GuiModule getModuleForSlot(int slotNumber) {
      return this.getModuleForContainer(this.getContainer().getSlotContainer(slotNumber));
   }

   protected GuiModule getModuleForContainer(Container container) {
      for(GuiModule module : this.modules) {
         if (module.inventorySlots == container) {
            return module;
         }
      }

      return null;
   }

   protected ContainerMultiModule getContainer() {
      return (ContainerMultiModule)this.inventorySlots;
   }
}
