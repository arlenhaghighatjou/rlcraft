package com.bloodnbonesgaming.lib.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.relauncher.Side;

public class BNBMessageContext {
   public final INetHandler netHandler;
   public final Side side;

   public BNBMessageContext(INetHandler netHandler, Side side) {
      this.netHandler = netHandler;
      this.side = side;
   }

   public NetHandlerPlayServer getServerHandler() {
      return (NetHandlerPlayServer)this.netHandler;
   }

   public NetHandlerPlayClient getClientHandler() {
      return (NetHandlerPlayClient)this.netHandler;
   }
}
