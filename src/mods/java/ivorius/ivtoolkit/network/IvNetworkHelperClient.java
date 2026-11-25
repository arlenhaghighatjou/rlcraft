package ivorius.ivtoolkit.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class IvNetworkHelperClient {
   public IvNetworkHelperClient() {
   }

   /** @deprecated */
   @Deprecated
   public static <ETileEntity extends TileEntity & ClientEventHandler> void sendTileEntityUpdatePacket(ETileEntity tileEntity, String context, SimpleNetworkWrapper network, Object... params) {
      sendTileEntityEventPacket(tileEntity, context, network, params);
   }

   public static <ETileEntity extends TileEntity & ClientEventHandler> void sendTileEntityEventPacket(ETileEntity tileEntity, String context, SimpleNetworkWrapper network, Object... params) {
      if (!tileEntity.getWorld().isRemote) {
         throw new UnsupportedOperationException();
      } else {
         network.sendToServer(PacketTileEntityClientEvent.packetEntityData(tileEntity, context, params));
      }
   }
}
