package net.blay09.mods.waystones.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.blay09.mods.waystones.WaystoneConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageConfig implements IMessage {
   public MessageConfig() {
   }

   public void fromBytes(ByteBuf buf) {
      WaystoneConfig.read(buf);
   }

   public void toBytes(ByteBuf buf) {
      WaystoneConfig.write(buf);
   }
}
