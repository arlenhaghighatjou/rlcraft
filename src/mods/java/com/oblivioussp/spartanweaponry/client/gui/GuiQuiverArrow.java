package com.oblivioussp.spartanweaponry.client.gui;

import com.oblivioussp.spartanweaponry.inventory.ContainerQuiverArrow;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GuiQuiverArrow extends GuiContainer {
   protected final ResourceLocation texture;
   protected final ItemStack quiver;
   protected final InventoryPlayer playerInventory;

   public GuiQuiverArrow(ContainerQuiverArrow container, InventoryPlayer invPlayer, ItemStack quiverStack) {
      super(container);
      this.playerInventory = invPlayer;
      this.quiver = quiverStack;
      int arrowSlots = 4;
      if (this.quiver.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
         arrowSlots = ((IItemHandler)this.quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).getSlots();
      }

      if (arrowSlots == 4) {
         this.texture = new ResourceLocation("spartanweaponry", "textures/gui/quiver_light.png");
      } else if (arrowSlots == 6) {
         this.texture = new ResourceLocation("spartanweaponry", "textures/gui/quiver_medium.png");
      } else {
         this.texture = new ResourceLocation("spartanweaponry", "textures/gui/quiver_heavy.png");
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.mc.getTextureManager().bindTexture(this.texture);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      int offhandY = 104;
      this.drawTexturedModalRect(this.guiLeft - 27, this.guiTop + offhandY, 178, offhandY, 27, 29);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String name = this.quiver.getDisplayName();
      this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 5, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, 42, 4210752);
   }
}
