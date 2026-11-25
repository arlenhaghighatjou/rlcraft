package com.creativemd.creativecore.common.gui.mc;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSub extends Container {
   public ArrayList<SubContainer> layers = new ArrayList();
   @SideOnly(Side.CLIENT)
   public GuiContainerSub gui;
   public BlockPos coord = null;
   private boolean first = true;

   public ContainerSub(EntityPlayer player, SubContainer subContainer) {
      subContainer.container = this;
      this.layers.add(subContainer);
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }

   public SubContainer getTopLayer() {
      return (SubContainer)this.layers.get(this.layers.size() - 1);
   }

   public boolean isTopLayer(SubContainer container) {
      return this.getTopLayer() == container;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < this.layers.size(); ++i) {
         if (this.first) {
            ((SubContainer)this.layers.get(i)).onOpened();
            this.first = false;
         }

         ((SubContainer)this.layers.get(i)).onTick();
      }

   }

   public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);

      for(int i = 0; i < this.layers.size(); ++i) {
         ((SubContainer)this.layers.get(i)).onClosed();
      }

      GuiHandler.openContainers.remove(this);
   }
}
