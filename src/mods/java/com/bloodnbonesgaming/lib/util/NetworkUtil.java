package com.bloodnbonesgaming.lib.util;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class NetworkUtil {
   public NetworkUtil() {
   }

   public static void writeNBT(NBTTagCompound comp, ByteBuf buf) {
      (new PacketBuffer(buf)).writeCompoundTag(comp);
   }
}
