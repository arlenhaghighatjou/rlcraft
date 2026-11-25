package com.tmtravlr.qualitytools.network;

import com.tmtravlr.qualitytools.config.ConfigLoader;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerClient implements IMessageHandler<SToCMessage, IMessage> {
   public static final int UPDATE_REFORGE_ITEMS = 1;

   public PacketHandlerClient() {
   }

   public IMessage onMessage(SToCMessage packet, MessageContext context) {
      PacketBuffer buff = new PacketBuffer(Unpooled.wrappedBuffer(packet.getData()));
      int type = buff.readInt();
      switch(type) {
      case 1:
         try {
            ConfigLoader.universalReforgeItem = null;
            ConfigLoader.customReforgeMaterials.clear();
            NBTTagCompound packetTag = buff.readCompoundTag();
            ConfigLoader.loadReforgingToolsFromNBT(packetTag);
            ConfigLoader.reloadGeneralConfig();
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      default:
         return null;
      }
   }
}
