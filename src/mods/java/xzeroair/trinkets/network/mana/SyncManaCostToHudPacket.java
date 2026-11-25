package xzeroair.trinkets.network.mana;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import xzeroair.trinkets.client.events.ScreenOverlayEvents;
import xzeroair.trinkets.network.ThreadSafePacket;

public class SyncManaCostToHudPacket extends ThreadSafePacket {
   private float cost;

   public SyncManaCostToHudPacket() {
   }

   public void fromBytes(ByteBuf buf) {
      this.cost = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeFloat(this.cost);
   }

   public SyncManaCostToHudPacket(float cost) {
      this.cost = cost;
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      ScreenOverlayEvents.instance.SyncCost(this.cost);
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
   }
}
