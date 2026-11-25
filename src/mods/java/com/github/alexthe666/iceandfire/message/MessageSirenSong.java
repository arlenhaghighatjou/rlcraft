package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.EntitySiren;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSirenSong extends AbstractMessage<MessageSirenSong> {
   public int sirenId;
   public boolean isSinging;

   public MessageSirenSong(int sirenId, boolean isSinging) {
      this.sirenId = sirenId;
      this.isSinging = isSinging;
   }

   public MessageSirenSong() {
   }

   public void fromBytes(ByteBuf buf) {
      this.sirenId = buf.readInt();
      this.isSinging = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.sirenId);
      buf.writeBoolean(this.isSinging);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageSirenSong message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.sirenId);
      if (entity != null && entity instanceof EntitySiren) {
         EntitySiren siren = (EntitySiren)entity;
         siren.setSinging(message.isSinging);
      }

   }

   public void onServerReceived(MinecraftServer server, MessageSirenSong message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.sirenId);
      if (entity != null && entity instanceof EntitySiren) {
         EntitySiren siren = (EntitySiren)entity;
         siren.setSinging(message.isSinging);
      }

   }
}
