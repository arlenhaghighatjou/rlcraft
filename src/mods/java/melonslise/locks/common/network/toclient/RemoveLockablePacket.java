package melonslise.locks.common.network.toclient;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveLockablePacket implements IMessage {
   private int networkID;

   public RemoveLockablePacket() {
   }

   public RemoveLockablePacket(int networkID) {
      this.networkID = networkID;
   }

   public void fromBytes(ByteBuf buf) {
      this.networkID = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.networkID);
   }

   public static class Handler implements IMessageHandler<RemoveLockablePacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(final RemoveLockablePacket pkt, MessageContext ctx) {
         final Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(new Runnable() {
            public void run() {
               ((ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null)).remove(pkt.networkID);
            }
         });
         return null;
      }
   }
}
