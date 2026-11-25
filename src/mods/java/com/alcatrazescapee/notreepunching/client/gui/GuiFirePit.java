package com.alcatrazescapee.notreepunching.client.gui;

import com.alcatrazescapee.alcatrazcore.client.gui.GuiContainerTileCore;
import com.alcatrazescapee.notreepunching.common.tile.TileFirePit;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiFirePit extends GuiContainerTileCore<TileFirePit> {
   public GuiFirePit(TileFirePit tile, Container container, InventoryPlayer playerInv, ResourceLocation background, String titleKey) {
      super(tile, container, playerInv, background, titleKey);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      int burnTime = ((TileFirePit)this.tile).getScaledBurnTicks();
      int cookTime = ((TileFirePit)this.tile).getScaledCookTime();
      if (burnTime > 0) {
         this.drawTexturedModalRect(x + 81, y + 54 - burnTime, 176, 13 - burnTime, 14, burnTime);
      }

      if (cookTime > 0) {
         this.drawTexturedModalRect(x + 77, y + 23, 190, 0, cookTime, 16);
      }

   }
}
