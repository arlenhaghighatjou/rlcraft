package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiLayerPacket extends CreativeCorePacket {
   public NBTTagCompound nbt;
   public int layer;
   public boolean closed;

   public GuiLayerPacket() {
   }

   public GuiLayerPacket(NBTTagCompound nbt, int layer, boolean closed) {
      this.nbt = nbt;
      this.layer = layer;
      this.closed = closed;
   }

   public void writeBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.nbt);
      buf.writeInt(this.layer);
      buf.writeBoolean(this.closed);
   }

   public void readBytes(ByteBuf buf) {
      this.nbt = ByteBufUtils.readTag(buf);
      this.layer = buf.readInt();
      this.closed = buf.readBoolean();
   }

   @SideOnly(Side.CLIENT)
   public void executeClient(EntityPlayer player) {
      if (player.openContainer instanceof ContainerSub) {
         if (this.closed) {
            ((SubGui)((ContainerSub)player.openContainer).gui.getLayers().get(this.layer)).closeLayer(this.nbt, true);
         } else {
            ((SubGui)((ContainerSub)player.openContainer).gui.getLayers().get(this.layer)).openNewLayer(this.nbt, true);
         }
      }

   }

   public void executeServer(EntityPlayer player) {
      if (player.openContainer instanceof ContainerSub) {
         if (this.closed) {
            ((SubContainer)((ContainerSub)player.openContainer).layers.get(this.layer)).closeLayer(this.nbt, true);
         } else {
            ((SubContainer)((ContainerSub)player.openContainer).layers.get(this.layer)).openNewLayer(this.nbt, true);
         }
      }

   }
}
