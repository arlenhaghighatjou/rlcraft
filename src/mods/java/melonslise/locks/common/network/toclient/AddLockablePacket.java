package melonslise.locks.common.network.toclient;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AddLockablePacket implements IMessage {
   private Lockable lockable;

   public AddLockablePacket() {
   }

   public AddLockablePacket(Lockable lockable) {
      this.lockable = lockable;
   }

   public void fromBytes(ByteBuf buf) {
      this.lockable = LocksUtil.readLockableFromBuffer(buf);
   }

   public void toBytes(ByteBuf buf) {
      LocksUtil.writeLockableToBuffer(buf, this.lockable);
   }

   public static class Handler implements IMessageHandler<AddLockablePacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(final AddLockablePacket pkt, MessageContext ctx) {
         final Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(new Runnable() {
            public void run() {
               if (!((ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null)).add(pkt.lockable) && Locks.debug) {
                  String message = "Lock Failed: " + pkt.lockable.toString();
                  mc.player.sendMessage(new TextComponentString(message));
               }

            }
         });
         return null;
      }
   }
}
