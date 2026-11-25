package com.alcatrazescapee.alcatrazcore.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerProxy implements IProxy {
   public ServerProxy() {
   }

   public IThreadListener getThreadListener(MessageContext context) {
      if (context.side.isServer()) {
         return context.getServerHandler().player.mcServer;
      } else {
         throw new IProxy.WrongSideException("Tried to get the IThreadListener from a client-side MessageContext on the dedicated server");
      }
   }

   public EntityPlayer getPlayer(MessageContext context) {
      if (context.side.isServer()) {
         return context.getServerHandler().player;
      } else {
         throw new IProxy.WrongSideException("Tried to get the player from a client-side MessageContext on the dedicated server");
      }
   }

   public World getClientWorld() {
      throw new IProxy.WrongSideException("Tried to get the world from a client-side MessageContext on the dedicated server");
   }

   public EntityPlayer getClientPlayer() {
      throw new IProxy.WrongSideException("Tried to get the client player on a dedicated server");
   }
}
