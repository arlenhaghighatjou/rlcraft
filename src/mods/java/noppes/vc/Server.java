package noppes.vc;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.vc.constants.PacketClient;

public class Server {
   public Server() {
   }

   public static void sendData(final EntityPlayerMP player, final PacketClient enu, final Object... obs) {
      player.getServer().addScheduledTask(new Runnable() {
         public void run() {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

            try {
               if (!Server.fillBuffer(buffer, enu, obs)) {
                  return;
               }

               VariedCommodities.Channel.sendTo(new FMLProxyPacket(buffer, "variedcommodities"), player);
            } catch (IOException var3) {
               VariedCommodities.Log.error(enu + " Errored", var3);
            }

         }
      });
   }

   public static boolean sendDataChecked(EntityPlayerMP player, PacketClient enu, Object... obs) {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

      try {
         if (!fillBuffer(buffer, enu, obs)) {
            return false;
         }

         VariedCommodities.Channel.sendTo(new FMLProxyPacket(buffer, "variedcommodities"), player);
      } catch (IOException var5) {
         VariedCommodities.Log.error(enu + " Errored", var5);
      }

      return true;
   }

   public static void sendAssociatedData(Entity entity, final PacketClient enu, final Object... obs) {
      final List<EntityPlayerMP> list = entity.world.getEntitiesWithinAABB(EntityPlayerMP.class, entity.getEntityBoundingBox().grow(160.0D, 160.0D, 160.0D));
      if (!list.isEmpty()) {
         entity.getServer().addScheduledTask(new Runnable() {
            public void run() {
               ByteBuf buffer = Unpooled.buffer();

               try {
                  if (!Server.fillBuffer(buffer, enu, obs)) {
                     return;
                  }

                  for(EntityPlayerMP player : list) {
                     VariedCommodities.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "variedcommodities"), player);
                  }
               } catch (IOException var4) {
                  VariedCommodities.Log.error(enu + " Errored", var4);
               }

            }
         });
      }
   }

   public static void sendToAll(MinecraftServer server, final PacketClient enu, final Object... obs) {
      final List<EntityPlayerMP> list = new ArrayList(server.getPlayerList().getPlayers());
      server.addScheduledTask(new Runnable() {
         public void run() {
            ByteBuf buffer = Unpooled.buffer();

            try {
               if (!Server.fillBuffer(buffer, enu, obs)) {
                  return;
               }

               for(EntityPlayerMP player : list) {
                  VariedCommodities.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "variedcommodities"), player);
               }
            } catch (IOException var4) {
               VariedCommodities.Log.error(enu + " Errored", var4);
            }

         }
      });
   }

   public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException {
      buffer.writeInt(enu.ordinal());

      for(Object ob : obs) {
         if (ob != null) {
            if (ob instanceof Map) {
               Map<String, Integer> map = (Map)ob;
               buffer.writeInt(map.size());

               for(String key : map.keySet()) {
                  int value = ((Integer)map.get(key)).intValue();
                  buffer.writeInt(value);
                  writeString(buffer, key);
               }
            } else if (ob instanceof MerchantRecipeList) {
               ((MerchantRecipeList)ob).writeToBuf(new PacketBuffer(buffer));
            } else if (ob instanceof List) {
               List<String> list = (List)ob;
               buffer.writeInt(list.size());

               for(String s : list) {
                  writeString(buffer, s);
               }
            } else if (ob instanceof Enum) {
               buffer.writeInt(((Enum)ob).ordinal());
            } else if (ob instanceof Integer) {
               buffer.writeInt(((Integer)ob).intValue());
            } else if (ob instanceof Boolean) {
               buffer.writeBoolean(((Boolean)ob).booleanValue());
            } else if (ob instanceof String) {
               writeString(buffer, (String)ob);
            } else if (ob instanceof Float) {
               buffer.writeFloat(((Float)ob).floatValue());
            } else if (ob instanceof Long) {
               buffer.writeLong(((Long)ob).longValue());
            } else if (ob instanceof Double) {
               buffer.writeDouble(((Double)ob).doubleValue());
            } else if (ob instanceof NBTTagCompound) {
               writeNBT(buffer, (NBTTagCompound)ob);
            }
         }
      }

      if (buffer.array().length >= 32767) {
         VariedCommodities.Log.error("Packet " + enu + " was too big to be send");
         return false;
      } else {
         return true;
      }
   }

   public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

      try {
         CompressedStreamTools.write(compound, dataoutputstream);
      } finally {
         dataoutputstream.close();
      }

      byte[] bytes = bytearrayoutputstream.toByteArray();
      buffer.writeShort((short)bytes.length);
      buffer.writeBytes(bytes);
   }

   public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
      byte[] bytes = new byte[buffer.readShort()];
      buffer.readBytes(bytes);
      DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));

      NBTTagCompound var3;
      try {
         var3 = CompressedStreamTools.read(datainputstream, new NBTSizeTracker(2097152L));
      } finally {
         datainputstream.close();
      }

      return var3;
   }

   public static void writeString(ByteBuf buffer, String s) {
      byte[] bytes = s.getBytes(Charsets.UTF_8);
      buffer.writeShort((short)bytes.length);
      buffer.writeBytes(bytes);
   }

   public static String readString(ByteBuf buffer) {
      try {
         byte[] bytes = new byte[buffer.readShort()];
         buffer.readBytes(bytes);
         return new String(bytes, Charsets.UTF_8);
      } catch (IndexOutOfBoundsException var2) {
         return null;
      }
   }
}
