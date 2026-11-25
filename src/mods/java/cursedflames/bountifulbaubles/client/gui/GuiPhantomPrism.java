package cursedflames.bountifulbaubles.client.gui;

import cursedflames.bountifulbaubles.container.ContainerPhantomPrism;
import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiPhantomPrism extends GuiContainer {
   private static final int WIDTH = 194;
   private static final int HEIGHT = 162;
   private int guiLeft;
   private int guiTop;
   private ContainerPhantomPrism container;
   private static final ResourceLocation background = new ResourceLocation("bountifulbaubles", "textures/gui/phantom_prism.png");

   public GuiPhantomPrism(ContainerPhantomPrism container) {
      super(container);
      this.container = container;
   }

   public void initGui() {
      this.guiLeft = (this.width - 194) / 2;
      this.guiTop = (this.height - 162) / 2;
      this.xSize = 194;
      this.ySize = 162;
      super.initGui();
      int id = 0;

      for(int i = 0; i < 11; ++i) {
         int x;
         int y;
         if (i < 7) {
            x = 8 + (i % 4 + 5) * 18;
            y = i < 4 ? 8 : 62;
         } else {
            x = 26;
            y = 8 + (10 - i) * 18;
         }

         this.buttonList.add(new GuiToggleButton(id++, this.guiLeft + x, this.guiTop + y, "visible", true));
      }

      this.updateButtons();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.container.guiDirty) {
         this.container.guiDirty = false;
         this.updateButtons();
      }

      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void updateButtons() {
      for(int i = 0; i < 11; ++i) {
         GuiToggleButton button = (GuiToggleButton)this.buttonList.get(i);
         button.pinned = this.container.currentStates[i] > 0;
         button.visible = this.container.currentStates[i] != 0;
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id < 11) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setInteger("index", button.id);
         PacketHandler.INSTANCE.sendToServer(new NBTPacket(tag, PacketHandler.HandlerIds.PRISM_TOGGLE_VISIBLE.id));
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.drawDefaultBackground();
      this.mc.getTextureManager().bindTexture(background);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      int x = this.guiLeft + 51 + 18;
      int y = this.guiTop + 75;
      GuiInventory.drawEntityOnScreen(x, y, 30, (float)(x - mouseX), (float)(y - mouseY - 50), this.mc.player);
   }
}
