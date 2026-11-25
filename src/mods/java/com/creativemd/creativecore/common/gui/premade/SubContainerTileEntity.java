package com.creativemd.creativecore.common.gui.premade;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class SubContainerTileEntity extends SubContainer {
   protected int currenTick;
   public TileEntity te;

   public SubContainerTileEntity(EntityPlayer player, TileEntity te) {
      super(player);
      this.te = te;
   }

   public void writeOpeningNBT(NBTTagCompound nbt) {
      this.writeToNBTUpdate(nbt);
   }

   public void writeToNBTUpdate(NBTTagCompound nbt) {
      nbt.setBoolean("teUpdate", true);
      this.te.writeToNBT(nbt);
   }

   public void receivePacket(NBTTagCompound nbt) {
      if (nbt.getBoolean("teUpdate")) {
         this.te.readFromNBT(nbt);
      } else {
         super.receivePacket(nbt);
      }

   }

   public void onOpened() {
      super.onOpened();
      this.container.coord = this.te.getPos();
   }

   public boolean shouldTick() {
      return true;
   }

   public int getUpdateTickRate() {
      return 10;
   }

   public void onTick() {
      if (this.shouldTick()) {
         ++this.currenTick;
         if (this.currenTick > this.getUpdateTickRate()) {
            this.sendUpdate();
            this.currenTick = 0;
         }
      }

   }

   public void updateEqualContainers() {
      for(int i = 0; i < GuiHandler.openContainers.size(); ++i) {
         if (((ContainerSub)GuiHandler.openContainers.get(i)).coord.equals(this.container.coord)) {
            for(int j = 0; j < ((ContainerSub)GuiHandler.openContainers.get(i)).layers.size(); ++j) {
               ((SubContainer)((ContainerSub)GuiHandler.openContainers.get(i)).layers.get(j)).onOpened();
            }
         }
      }

   }
}
