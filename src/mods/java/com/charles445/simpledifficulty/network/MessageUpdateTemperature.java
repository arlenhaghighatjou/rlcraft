package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageUpdateTemperature implements IMessage {
   private NBTTagCompound nbt;

   public MessageUpdateTemperature() {
   }

   public MessageUpdateTemperature(NBTBase nbt) {
      this.nbt = (NBTTagCompound)nbt;
   }

   public void fromBytes(ByteBuf buf) {
      this.nbt = ByteBufUtils.readTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.nbt);
   }

   public NBTTagCompound getNBT() {
      return this.nbt;
   }

   public static class Handler implements IMessageHandler<MessageUpdateTemperature, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageUpdateTemperature message, MessageContext ctx) {
         if (ctx.side == Side.CLIENT) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null) {
               Minecraft.getMinecraft().addScheduledTask(() -> {
                  Capability<ITemperatureCapability> capability = SDCapabilities.TEMPERATURE;
                  capability.getStorage().readNBT(capability, player.getCapability(capability, (EnumFacing)null), (EnumFacing)null, message.getNBT());
               });
            }
         }

         return null;
      }
   }
}
