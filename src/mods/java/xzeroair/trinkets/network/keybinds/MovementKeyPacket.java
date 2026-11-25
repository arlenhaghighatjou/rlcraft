package xzeroair.trinkets.network.keybinds;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.network.ThreadSafePacket;

public class MovementKeyPacket extends ThreadSafePacket {
   private int entityID;
   private String key;
   private int state;

   public MovementKeyPacket() {
   }

   public MovementKeyPacket(EntityLivingBase entity, String key, int state) {
      this.entityID = entity.getEntityId();
      this.key = key;
      this.state = state;
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeInt(this.entityID);
      ByteBufUtils.writeUTF8String(buffer, this.key);
      buffer.writeInt(this.state);
   }

   public void fromBytes(ByteBuf buffer) {
      this.entityID = buffer.readInt();
      this.key = ByteBufUtils.readUTF8String(buffer);
      this.state = buffer.readInt();
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      Entity entity = server.player.getEntityWorld().getEntityByID(this.entityID);
      Capabilities.getEntityProperties(entity, (prop) -> {
         prop.getKeybindHandler().pressKey(entity, this.key, this.state);
      });
   }
}
