package mezz.jei.network.packets;

import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import mezz.jei.network.IPacketId;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public abstract class PacketJei {
   private final IPacketId id = this.getPacketId();

   public PacketJei() {
   }

   public final FMLProxyPacket getPacket() {
      PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
      packetBuffer.writeByte(this.id.ordinal());
      this.writePacketData(packetBuffer);
      return new FMLProxyPacket(packetBuffer, "jei");
   }

   public abstract IPacketId getPacketId();

   public abstract void writePacketData(PacketBuffer var1);
}
