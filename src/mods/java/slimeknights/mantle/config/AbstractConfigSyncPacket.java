package slimeknights.mantle.config;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import slimeknights.mantle.network.AbstractPacket;

public abstract class AbstractConfigSyncPacket extends AbstractPacket {
   private List<AbstractConfigFile> config;

   public AbstractConfigSyncPacket() {
   }

   protected abstract AbstractConfig getConfig();

   public IMessage handleClient(NetHandlerPlayClient netHandler) {
      this.sync();
      return null;
   }

   public IMessage handleServer(NetHandlerPlayServer netHandler) {
      throw new UnsupportedOperationException("Trying to sync client configs to the server. You registered the packet for the wrong side.");
   }

   protected boolean sync() {
      return AbstractConfig.syncConfig(this.getConfig(), this.config);
   }

   public void fromBytes(ByteBuf buf) {
      this.config = new ArrayList();

      for(AbstractConfigFile configFile : this.getConfig().configFileList) {
         int length = buf.readInt();
         byte[] data = new byte[length];
         buf.readBytes(data);
         this.config.add(configFile.loadFromPacket(data));
      }

   }

   public void toBytes(ByteBuf buf) {
      for(AbstractConfigFile configFile : this.getConfig().configFileList) {
         byte[] data = configFile.getPacketData();
         buf.writeInt(data.length);
         buf.writeBytes(data);
      }

   }
}
