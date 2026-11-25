package com.charles445.rltweaker.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IServerMessageReceiver {
   void receiveMessage(IMessage var1, EntityPlayer var2);
}
