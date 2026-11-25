package com.creativemd.itemphysic.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.EventHandler;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DropPacket extends CreativeCorePacket {
   public int power;

   public DropPacket() {
      this.power = 0;
   }

   public DropPacket(int power) {
      this.power = power;
   }

   public void writeBytes(ByteBuf bytes) {
      bytes.writeInt(this.power);
   }

   public void readBytes(ByteBuf bytes) {
      this.power = bytes.readInt();
   }

   @SideOnly(Side.CLIENT)
   public void executeClient(EntityPlayer player) {
   }

   public void executeServer(EntityPlayer player) {
      EventHandler.Droppower = this.power;
   }
}
