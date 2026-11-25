package vazkii.quark.oddities.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.network.message.MessageHandleBackpack;
import vazkii.quark.oddities.feature.Backpacks;
import vazkii.quark.oddities.inventory.ContainerBackpack;

public class GuiBackpackInventory extends GuiInventory {
   private static final ResourceLocation BACKPACK_INVENTORY_BACKGROUND = new ResourceLocation("quark", "textures/misc/backpack_gui.png");
   private final EntityPlayer player;
   private GuiButton recipeButton;
   private int recipeButtonY;
   private boolean closeHack = false;

   public GuiBackpackInventory(EntityPlayer player) {
      super(player);
      this.player = player;
      this.inventorySlots = new ContainerBackpack(player);
   }

   public void initGui() {
      this.ySize = 224;
      super.initGui();

      for(GuiButton button : this.buttonList) {
         if (button.id == 10) {
            button.y -= 29;
            this.recipeButton = button;
            this.recipeButtonY = button.y;
         }
      }

   }

   public void updateScreen() {
      super.updateScreen();
      this.recipeButton.y = this.recipeButtonY;
      if (!Backpacks.isEntityWearingBackpack(this.player)) {
         ItemStack curr = this.player.inventory.getItemStack();
         ContainerBackpack.saveCraftingInventory(this.player);
         this.closeHack = true;
         NetworkHandler.INSTANCE.sendToServer(new MessageHandleBackpack(false));
         this.mc.displayGuiScreen(new GuiInventory(this.player));
         this.player.inventory.setItemStack(curr);
      }

   }

   public void onGuiClosed() {
      if (this.closeHack) {
         this.closeHack = false;
      } else {
         super.onGuiClosed();
      }
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BACKPACK_INVENTORY_BACKGROUND);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - (float)mouseX, (float)(j + 75 - 50) - (float)mouseY, this.mc.player);
   }
}
