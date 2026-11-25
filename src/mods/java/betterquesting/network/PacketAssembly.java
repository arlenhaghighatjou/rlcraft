package betterquesting.network;

import betterquesting.core.BetterQuesting;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public final class PacketAssembly {
   public static final PacketAssembly INSTANCE = new PacketAssembly();
   private final HashMap<EaglercraftUUID, byte[]> buffer = new HashMap();
   private byte[] serverBuf = null;
   private static final int bufSize = 20480;

   public PacketAssembly() {
   }

   public List<NBTTagCompound> splitPacket(NBTTagCompound tags) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CompressedStreamTools.writeCompressed(tags, baos);
         baos.flush();
         byte[] data = baos.toByteArray();
         baos.close();
         int req = MathHelper.ceil((float)data.length / 20480.0F);
         List<NBTTagCompound> pkts = new ArrayList(req);

         for(int p = 0; p < req; ++p) {
            int idx = p * 20480;
            int s = Math.min(data.length - idx, 20480);
            NBTTagCompound container = new NBTTagCompound();
            byte[] part = new byte[s];
            System.arraycopy(data, idx, part, 0, s);
            container.setInteger("size", data.length);
            container.setInteger("index", idx);
            container.setBoolean("end", p == req - 1);
            container.setTag("data", new NBTTagByteArray(part));
            pkts.add(container);
         }

         return pkts;
      } catch (Exception var11) {
         BetterQuesting.logger.error("Unable to split build packet!", var11);
         return Collections.emptyList();
      }
   }

   public NBTTagCompound assemblePacket(EaglercraftUUID owner, NBTTagCompound tags) {
      int size = tags.getInteger("size");
      int index = tags.getInteger("index");
      boolean end = tags.getBoolean("end");
      byte[] data = tags.getByteArray("data");
      byte[] tmp = this.getBuffer(owner);
      if (tmp == null) {
         tmp = new byte[size];
         this.setBuffer(owner, tmp);
      } else if (tmp.length != size) {
         BetterQuesting.logger.error("Unexpected change in BQ packet byte length: " + size + " > " + tmp.length);
         this.clearBuffer(owner);
         return null;
      }

      System.arraycopy(data, 0, tmp, index, data.length);
      if (end) {
         this.clearBuffer(owner);

         try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(tmp))));
            NBTTagCompound tag = CompressedStreamTools.read(dis, NBTSizeTracker.INFINITE);
            dis.close();
            return tag;
         } catch (Exception var10) {
            throw new RuntimeException("Unable to assemble BQ packet", var10);
         }
      } else {
         return null;
      }
   }

   public byte[] getBuffer(EaglercraftUUID owner) {
      if (owner == null) {
         return this.serverBuf;
      } else {
         synchronized(this.buffer) {
            return (byte[])this.buffer.get(owner);
         }
      }
   }

   public void setBuffer(EaglercraftUUID owner, byte[] value) {
      if (owner == null) {
         this.serverBuf = value;
      } else {
         synchronized(this.buffer) {
            if (this.buffer.containsKey(owner)) {
               throw new IllegalStateException("Attepted to start more than one BQ packet assembly for EaglercraftUUID " + owner.toString());
            }

            this.buffer.put(owner, value);
         }
      }

   }

   public void clearBuffer(EaglercraftUUID owner) {
      if (owner == null) {
         this.serverBuf = null;
      } else {
         synchronized(this.buffer) {
            this.buffer.remove(owner);
         }
      }

   }
}
