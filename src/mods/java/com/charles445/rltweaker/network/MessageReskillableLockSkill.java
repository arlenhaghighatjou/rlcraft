package com.charles445.rltweaker.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReskillableLockSkill implements IMessage {
   public ResourceLocation skill;
   public ResourceLocation unlockable;

   public MessageReskillableLockSkill() {
      this.setPacketInvalid();
   }

   public MessageReskillableLockSkill(ResourceLocation skill, ResourceLocation unlockable) {
      this.skill = skill;
      this.unlockable = unlockable;
   }

   public void fromBytes(ByteBuf buf) {
      if (buf == null) {
         this.setPacketInvalid();
      } else {
         try {
            this.skill = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
            this.unlockable = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
         } catch (IndexOutOfBoundsException var3) {
            this.setPacketInvalid();
         }
      }
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.skill.toString());
      ByteBufUtils.writeUTF8String(buf, this.unlockable.toString());
   }

   private void setPacketInvalid() {
      this.skill = new ResourceLocation("invalid", "invalid");
      this.unlockable = new ResourceLocation("invalid", "invalid");
   }

   public static class Handler implements IMessageHandler<MessageReskillableLockSkill, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageReskillableLockSkill message, MessageContext ctx) {
         return ServerMessageHandler.executeMessage(message, ctx);
      }
   }
}
