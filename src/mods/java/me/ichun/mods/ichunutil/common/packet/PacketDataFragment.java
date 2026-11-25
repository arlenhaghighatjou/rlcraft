package me.ichun.mods.ichunutil.common.packet;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public abstract class PacketDataFragment extends AbstractPacket {
   public String fileName;
   public short packetTotal;
   public short packetNumber;
   public int fragmentSize;
   public byte[] data;

   public PacketDataFragment() {
   }

   public PacketDataFragment(String fileName, int packetTotal, int packetNumber, int fragmentSize, byte[] data) {
      this.fileName = fileName;
      this.packetTotal = (short)packetTotal;
      this.packetNumber = (short)packetNumber;
      this.fragmentSize = fragmentSize;
      this.data = data;
   }

   public void writeTo(ByteBuf buffer) {
      ByteBufUtils.writeUTF8String(buffer, this.fileName);
      buffer.writeShort(this.packetTotal);
      buffer.writeShort(this.packetNumber);
      buffer.writeInt(this.fragmentSize);
      buffer.writeBytes(this.data);
   }

   public void readFrom(ByteBuf buffer) {
      this.fileName = ByteBufUtils.readUTF8String(buffer);
      this.packetTotal = buffer.readShort();
      this.packetNumber = buffer.readShort();
      this.fragmentSize = buffer.readInt();
      this.data = new byte[this.fragmentSize];
      buffer.readBytes(this.data);
   }

   public void execute(Side side, EntityPlayer player) {
      this.execution(side, player);
   }

   public abstract void execution(Side var1, EntityPlayer var2);
}
