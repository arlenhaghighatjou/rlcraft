package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageStoneStatue extends AbstractMessage<MessageStoneStatue> {
   public int entityId;
   public boolean isStone;

   public MessageStoneStatue(int entityId, boolean isStone) {
      this.entityId = entityId;
      this.isStone = isStone;
   }

   public MessageStoneStatue() {
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.isStone = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeBoolean(this.isStone);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageStoneStatue message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.entityId);
      if (entity != null && entity instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(entity, StoneEntityProperties.class);
         properties.isStone = message.isStone;
      }

   }

   public void onServerReceived(MinecraftServer server, MessageStoneStatue message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.entityId);
      if (entity != null && entity instanceof EntityLiving) {
         StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(entity, StoneEntityProperties.class);
         properties.isStone = message.isStone;
      }

   }
}
