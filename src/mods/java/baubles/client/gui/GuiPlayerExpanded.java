package baubles.client.gui;

import baubles.client.ClientProxy;
import baubles.common.container.ContainerPlayerExpanded;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiPlayerExpanded extends InventoryEffectRenderer {
   public static final ResourceLocation background = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
   private float oldMouseX;
   private float oldMouseY;

   public GuiPlayerExpanded(EntityPlayer player) {
      super(new ContainerPlayerExpanded(player.inventory, !player.getEntityWorld().isRemote, player));
      this.allowUserInput = true;
   }

   private void resetGuiLeft() {
      this.guiLeft = (this.width - this.xSize) / 2;
   }

   public void updateScreen() {
      ((ContainerPlayerExpanded)this.inventorySlots).baubles.setEventBlock(false);
      this.updateActivePotionEffects();
      this.resetGuiLeft();
   }

   public void initGui() {
      this.buttonList.clear();
      super.initGui();
      this.resetGuiLeft();
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 115, 8, 4210752);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.oldMouseX = (float)mouseX;
      this.oldMouseY = (float)mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(background);
      int k = this.guiLeft;
      int l = this.guiTop;
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

      for(int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
         Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
         if (slot.getHasStack() && slot.getSlotStackLimit() == 1) {
            this.drawTexturedModalRect(k + slot.xPos, l + slot.yPos, 200, 0, 16, 16);
         }
      }

      GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float)(k + 51) - this.oldMouseX, (float)(l + 75 - 50) - this.oldMouseY, this.mc.player);
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         ;
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
      }

   }

   protected void keyTyped(char par1, int par2) throws IOException {
      if (par2 == ClientProxy.KEY_BAUBLES.getKeyCode()) {
         this.mc.player.closeScreen();
      } else {
         super.keyTyped(par1, par2);
      }

   }

   public void displayNormalInventory() {
      GuiInventory gui = new GuiInventory(this.mc.player);
      gui.oldMouseX = this.oldMouseX;
      gui.oldMouseY = this.oldMouseY;
      this.mc.displayGuiScreen(gui);
   }
}
