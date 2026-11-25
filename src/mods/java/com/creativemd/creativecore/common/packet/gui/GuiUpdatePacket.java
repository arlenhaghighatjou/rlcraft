package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiUpdatePacket extends CreativeCorePacket {
   public NBTTagCompound value;
   public boolean isOpening;
   public int layer;

   public GuiUpdatePacket() {
   }

   public GuiUpdatePacket(NBTTagCompound value, boolean isOpening, int layer) {
      this.value = value;
      this.isOpening = isOpening;
      this.layer = layer;
   }

   public void writeBytes(ByteBuf bytes) {
      ByteBufUtils.writeTag(bytes, this.value);
      bytes.writeBoolean(this.isOpening);
      bytes.writeInt(this.layer);
   }

   public void readBytes(ByteBuf bytes) {
      this.value = ByteBufUtils.readTag(bytes);
      this.isOpening = bytes.readBoolean();
      this.layer = bytes.readInt();
   }

   @SideOnly(Side.CLIENT)
   public void executeClient(EntityPlayer player) {
      if (player.openContainer instanceof ContainerSub) {
         ((SubContainer)((ContainerSub)player.openContainer).layers.get(this.layer)).receivePacket(this.value);
      }

   }

   public void executeServer(EntityPlayer player) {
      if (player.openContainer instanceof ContainerSub) {
         ((SubContainer)((ContainerSub)player.openContainer).layers.get(this.layer)).receivePacket(this.value);
      }

   }
}
