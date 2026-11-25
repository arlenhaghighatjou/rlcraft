package com.bloodnbonesgaming.lib.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class NetworkUtil {
   public NetworkUtil() {
   }

   public static void writeNBT(NBTTagCompound comp, ByteBuf buf) {
      (new PacketBuffer(buf)).writeCompoundTag(comp);
   }
}
