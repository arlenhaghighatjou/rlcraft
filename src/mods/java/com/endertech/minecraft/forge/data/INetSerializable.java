package com.endertech.minecraft.forge.data;

import io.netty.buffer.ByteBuf;

public interface INetSerializable<T> {
   T readFrom(ByteBuf var1);

   ByteBuf writeTo(ByteBuf var1);
}
