package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiNBTPacket extends CreativeCorePacket {
   public NBTTagCompound value;

   public GuiNBTPacket(NBTTagCompound nbt) {
      this.value = nbt;
   }

   public GuiNBTPacket() {
   }

   public void writeBytes(ByteBuf buf) {
      writeNBT(buf, this.value);
   }

   public void readBytes(ByteBuf buf) {
      this.value = readNBT(buf);
   }

   public void executeClient(EntityPlayer player) {
      if (player.openContainer instanceof ContainerSub) {
         ((ContainerSub)player.openContainer).gui.getTopLayer().receiveContainerPacket(this.value);
      }

   }

   public void executeServer(EntityPlayer player) {
   }
}
