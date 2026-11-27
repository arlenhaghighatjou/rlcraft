package snownee.kiwi.network;

import java.util.function.Supplier;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.kiwi.Kiwi;

public final class NetworkChannel {
   public static final NetworkChannel INSTANCE = new NetworkChannel();
   private final Object2IntMap<Class<? extends PacketMod>> mapping = new Object2IntArrayMap();
   private final Int2ObjectMap<Supplier<? extends PacketMod>> mappingReverse = new Int2ObjectArrayMap();
   private int nextIndex = 0;
   private final FMLEventChannel channel;

   private NetworkChannel() {
      this.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("kiwi");
      this.channel.register(ServerCustomPacketEvent.class, this::onServerPacketIncoming);
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.channel.register(ClientCustomPacketEvent.class, this::onClientPacketIncoming);
      }

   }

   @SubscribeEvent
   public void onServerPacketIncoming(ServerCustomPacketEvent event) {
      this.handleOnServer(this.decodeData(event.getPacket().payload()), ((NetHandlerPlayServer)event.getHandler()).player);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onClientPacketIncoming(ClientCustomPacketEvent event) {
      this.handleOnClient(this.decodeData(event.getPacket().payload()), Minecraft.getMinecraft().player);
   }

   public void sendToAll(PacketMod packet) {
      this.channel.sendToAll(new FMLProxyPacket(new PacketBuffer(this.unpack(packet)), "kiwi"));
   }

   public void sendToAllTracking(PacketMod packet, int dim, BlockPos pos) {
      this.channel.sendToAllTracking(new FMLProxyPacket(new PacketBuffer(this.unpack(packet)), "kiwi"), new TargetPoint(dim, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 4.0D));
   }

   public void sendToAllAround(PacketMod packet, int dim, BlockPos pos) {
      this.sendToAllAround(packet, dim, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 16.0D);
   }

   public void sendToAllAround(PacketMod packet, int dim, double x, double y, double z, double range) {
      this.channel.sendToAllAround(new FMLProxyPacket(new PacketBuffer(this.unpack(packet)), "kiwi"), new TargetPoint(dim, x, y, z, range));
   }

   public void sendToDimension(PacketMod packet, int dim) {
      this.channel.sendToDimension(new FMLProxyPacket(new PacketBuffer(this.unpack(packet)), "kiwi"), dim);
   }

   public void sendToPlayer(PacketMod packet, EntityPlayerMP player) {
      this.channel.sendTo(new FMLProxyPacket(new PacketBuffer(this.unpack(packet)), "kiwi"), player);
   }

   public void sendToServer(PacketMod packet) {
      this.channel.sendToServer(new FMLProxyPacket(new PacketBuffer(this.unpack(packet)), "kiwi"));
   }

   public <T extends PacketMod> void register(Class<T> klass, Supplier<T> factory) {
      this.mapping.put(klass, this.nextIndex);
      this.mappingReverse.put(this.nextIndex, factory);
      ++this.nextIndex;
   }

   private int getPacketIndex(Class<? extends PacketMod> klass) {
      return this.mapping.getInt(klass);
   }

   private PacketMod getByIndex(int index) {
      try {
         return (PacketMod)((Supplier)this.mappingReverse.get(index)).get();
      } catch (Exception var3) {
         return null;
      }
   }

   private PacketMod decodeData(ByteBuf buffer) {
      int index = buffer.readInt();
      PacketMod packet = this.getByIndex(index);
      if (packet == null) {
         Kiwi.logger.error("Receiving malformed packet");
         return null;
      } else {
         packet.readDataFrom(buffer);
         return packet;
      }
   }

   @SideOnly(Side.CLIENT)
   private void handleOnClient(PacketMod packet, EntityPlayerSP player) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         try {
            packet.handleClient(player);
         } catch (Exception var3) {
            Kiwi.logger.error(var3);
         }

      });
   }

   private void handleOnServer(PacketMod packet, EntityPlayerMP player) {
      FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
         try {
            packet.handleServer(player);
         } catch (Exception var3) {
            Kiwi.logger.error(var3);
         }

      });
   }

   private ByteBuf unpack(PacketMod packet) {
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeInt(this.getPacketIndex(packet.getClass()));

      try {
         packet.writeDataTo(buffer);
      } catch (Exception var4) {
         Kiwi.logger.error(var4);
      }

      return buffer;
   }
}
