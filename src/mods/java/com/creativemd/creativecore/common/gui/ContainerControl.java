package com.creativemd.creativecore.common.gui;

import com.creativemd.creativecore.common.gui.container.ContainerParent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ContainerControl extends CoreControl {
   @SideOnly(Side.CLIENT)
   protected GuiControl guiControl;

   public ContainerControl(String name) {
      super(name);
   }

   public ContainerParent getParent() {
      return (ContainerParent)this.parent;
   }

   public void sendUpdate() {
      this.getParent().updateEqualContainers();
      NBTTagCompound nbt = new NBTTagCompound();
      this.writeToNBTUpdate(nbt);
      this.sendPacket(nbt);
   }

   public void sendPacket(NBTTagCompound nbt) {
      if (this.parent != null) {
         this.getParent().sendNBTUpdate(this, nbt);
      }

   }

   public abstract void writeToNBTUpdate(NBTTagCompound var1);

   public void receivePacket(NBTTagCompound nbt) {
      this.onPacketReceive(nbt);
   }

   public abstract void onPacketReceive(NBTTagCompound var1);

   @SideOnly(Side.CLIENT)
   protected abstract GuiControl createGuiControl();

   @SideOnly(Side.CLIENT)
   public GuiControl getGuiControl() {
      if (this.guiControl == null) {
         this.guiControl = this.createGuiControl();
      }

      return this.guiControl;
   }
}
