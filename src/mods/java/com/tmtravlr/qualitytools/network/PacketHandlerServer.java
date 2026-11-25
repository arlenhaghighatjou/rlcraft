package com.tmtravlr.qualitytools.network;

import com.tmtravlr.qualitytools.reforging.TileEntityReforgingStation;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerServer implements IMessageHandler<CToSMessage, IMessage> {
   public static final int REFORGE_TOOL = 1;

   public PacketHandlerServer() {
   }

   public IMessage onMessage(CToSMessage packet, MessageContext context) {
      PacketBuffer buff = new PacketBuffer(Unpooled.wrappedBuffer(packet.getData()));
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      int type = buff.readInt();
      switch(type) {
      case 1:
         BlockPos pos = buff.readBlockPos();
         int dimension = buff.readInt();
         World world = null;
         if (server != null) {
            world = server.getWorld(dimension);
         }

         if (world != null && world.getTileEntity(pos) instanceof TileEntityReforgingStation) {
            TileEntityReforgingStation tile = (TileEntityReforgingStation)world.getTileEntity(pos);
            tile.reforgeTool();
         }
      default:
         return null;
      }
   }
}
