package xzeroair.trinkets.network.configsync;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xzeroair.trinkets.network.ThreadSafePacket;
import xzeroair.trinkets.util.TrinketsConfig;

public class PacketConfigSync extends ThreadSafePacket {
   Map<String, String> map;
   int size;

   public PacketConfigSync() {
   }

   public PacketConfigSync(Map<String, String> configMap) {
      if (configMap != null && !configMap.isEmpty()) {
         this.map = configMap;
         this.size = this.map.size();
      } else {
         this.map = new HashMap();
         this.size = 0;
      }

   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeInt(this.size);

      for(Entry<String, String> config : this.map.entrySet()) {
         ByteBufUtils.writeUTF8String(buffer, (String)config.getKey());
         ByteBufUtils.writeUTF8String(buffer, (String)config.getValue());
      }

   }

   public void fromBytes(ByteBuf buffer) {
      this.size = buffer.readInt();
      if (this.size > 0) {
         this.map = new HashMap();

         for(int i = 0; i < this.size; ++i) {
            String key = ByteBufUtils.readUTF8String(buffer);
            String value = ByteBufUtils.readUTF8String(buffer);
            this.map.put(key, value);
         }
      }

   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      TrinketsConfig.readConfigMap(this.map);
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
   }
}
