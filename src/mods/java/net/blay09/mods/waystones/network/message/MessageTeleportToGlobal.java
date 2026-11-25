package net.blay09.mods.waystones.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageTeleportToGlobal implements IMessage {
   private String waystoneName;

   public MessageTeleportToGlobal() {
   }

   public MessageTeleportToGlobal(String waystoneName) {
      this.waystoneName = waystoneName;
   }

   public void fromBytes(ByteBuf buf) {
      this.waystoneName = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.waystoneName);
   }

   public String getWaystoneName() {
      return this.waystoneName;
   }
}
