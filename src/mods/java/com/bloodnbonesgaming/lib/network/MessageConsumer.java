package com.bloodnbonesgaming.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface MessageConsumer {
   IMessage consume(ConsumableMessage var1, byte var2, BNBMessageContext var3);
}
