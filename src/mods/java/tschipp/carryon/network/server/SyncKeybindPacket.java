package tschipp.carryon.network.server;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SyncKeybindPacket implements IMessage {
   private int p;
   public boolean pressed;

   public SyncKeybindPacket() {
   }

   public SyncKeybindPacket(boolean pressed) {
      this.p = pressed ? 1 : 0;
   }

   public void fromBytes(ByteBuf buf) {
      this.p = ByteBufUtils.readVarInt(buf, 4);
      this.pressed = this.p == 1;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.p, 4);
   }
}
