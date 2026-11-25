package com.endertech.minecraft.forge.data;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;

public interface INetSerializable<T> {
   T readFrom(ByteBuf var1);

   ByteBuf writeTo(ByteBuf var1);
}
