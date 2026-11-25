package net.blay09.mods.waystones.network.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWaystones implements IMessage {
   private WaystoneEntry[] entries;
   private long lastFreeWarp;
   private long lastWarpStoneUse;

   public MessageWaystones() {
   }

   public MessageWaystones(WaystoneEntry[] entries, long lastFreeWarp, long lastWarpStoneUse) {
      this.entries = entries;
      this.lastFreeWarp = lastFreeWarp;
      this.lastWarpStoneUse = lastWarpStoneUse;
   }

   public void fromBytes(ByteBuf buf) {
      this.entries = new WaystoneEntry[buf.readShort()];

      for(int i = 0; i < this.entries.length; ++i) {
         this.entries[i] = WaystoneEntry.read(buf);
      }

      this.lastFreeWarp = buf.readLong();
      this.lastWarpStoneUse = buf.readLong();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeShort(this.entries.length);

      for(WaystoneEntry entry : this.entries) {
         entry.write(buf);
      }

      buf.writeLong(this.lastFreeWarp);
      buf.writeLong(Math.max(0L, (long)(WaystoneConfig.general.warpStoneCooldown * 1000) - (System.currentTimeMillis() - this.lastWarpStoneUse)));
   }

   public WaystoneEntry[] getEntries() {
      return this.entries;
   }

   public long getLastFreeWarp() {
      return this.lastFreeWarp;
   }

   public long getLastWarpStoneUse() {
      return this.lastWarpStoneUse;
   }
}
