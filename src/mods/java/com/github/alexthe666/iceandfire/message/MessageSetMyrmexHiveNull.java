package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.ClientProxy;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSetMyrmexHiveNull extends AbstractMessage<MessageSetMyrmexHiveNull> {
   public MessageSetMyrmexHiveNull() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageSetMyrmexHiveNull message, EntityPlayer player, MessageContext messageContext) {
      ClientProxy.setReferedClientHive((MyrmexHive)null);
   }

   public void onServerReceived(MinecraftServer server, MessageSetMyrmexHiveNull message, EntityPlayer player, MessageContext messageContext) {
   }
}
