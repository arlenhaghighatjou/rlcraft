package org.orecruncher.dsurround.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.handlers.DiagnosticHandler;
import org.orecruncher.dsurround.client.handlers.EffectManager;

public class PacketServerData implements IMessage {
   private double meanTickTime;
   private Int2DoubleOpenHashMap tMap;
   private int free;
   private int total;
   private int max;

   public PacketServerData() {
   }

   public PacketServerData(@Nonnull Int2DoubleOpenHashMap tps, double meanTickTime, int memFree, int memTotal, int memMax) {
      this.meanTickTime = meanTickTime;
      this.tMap = tps;
      this.free = memFree;
      this.total = memTotal;
      this.max = memMax;
   }

   public void fromBytes(@Nonnull ByteBuf buf) {
      this.meanTickTime = buf.readDouble();
      int len = buf.readInt();
      this.tMap = new Int2DoubleOpenHashMap(len);

      while(len-- != 0) {
         this.tMap.put(buf.readInt(), buf.readDouble());
      }

      this.free = buf.readInt();
      this.total = buf.readInt();
      this.max = buf.readInt();
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeDouble(this.meanTickTime);
      buf.writeInt(this.tMap.size());
      this.tMap.int2DoubleEntrySet().forEach((entry) -> {
         buf.writeInt(entry.getIntKey());
         buf.writeDouble(entry.getDoubleValue());
      });
      buf.writeInt(this.free);
      buf.writeInt(this.total);
      buf.writeInt(this.max);
   }

   @Nonnull
   private static TextFormatting getTpsFormatPrefix(int tps) {
      if (tps <= 10) {
         return TextFormatting.RED;
      } else {
         return tps <= 15 ? TextFormatting.YELLOW : TextFormatting.GREEN;
      }
   }

   public static class PacketHandler implements IMessageHandler<PacketServerData, IMessage> {
      public PacketHandler() {
      }

      @Nullable
      public IMessage onMessage(@Nonnull PacketServerData message, @Nullable MessageContext ctx) {
         if (ctx != null) {
            ModBase.proxy().getThreadListener(ctx).addScheduledTask(() -> {
               ArrayList<String> data = new ArrayList();
               int diff = message.total - message.free;
               data.add(TextFormatting.GOLD + "Server Information");
               data.add(String.format("Mem: %d%% %03d/%3dMB", diff * 100 / message.max, diff, message.max));
               data.add(String.format("Allocated: %d%% %3dMB", message.total * 100 / message.max, message.total));
               int tps = (int)Math.min(1000.0D / message.meanTickTime, 20.0D);
               data.add(String.format("Ticktime Overall:%s %5.3fms (%d TPS)", PacketServerData.getTpsFormatPrefix(tps), message.meanTickTime, tps));
               message.tMap.int2DoubleEntrySet().forEach((entry) -> {
                  String dimName = DimensionManager.getProviderType(entry.getIntKey()).getName();
                  int tps1 = (int)Math.min(1000.0D / entry.getDoubleValue(), 20.0D);
                  data.add(String.format("%s (%d):%s %7.3fms (%d TPS)", dimName, entry.getIntKey(), PacketServerData.getTpsFormatPrefix(tps1), entry.getDoubleValue(), tps1));
               });
               Collections.sort(data.subList(4, data.size()));
               DiagnosticHandler handler = (DiagnosticHandler)EffectManager.instance().lookupService(DiagnosticHandler.class);
               handler.setServerTPSReport(data);
            });
         }

         return null;
      }
   }
}
