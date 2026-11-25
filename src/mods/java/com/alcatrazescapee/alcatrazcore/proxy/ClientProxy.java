package com.alcatrazescapee.alcatrazcore.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy implements IProxy {
   public ClientProxy() {
   }

   @Nonnull
   public IThreadListener getThreadListener(MessageContext context) {
      return (IThreadListener)(context.side.isClient() ? Minecraft.getMinecraft() : context.getServerHandler().player.mcServer);
   }

   @Nullable
   public EntityPlayer getPlayer(MessageContext context) {
      return (EntityPlayer)(context.side.isClient() ? Minecraft.getMinecraft().player : context.getServerHandler().player);
   }

   public World getClientWorld() {
      return Minecraft.getMinecraft().world;
   }

   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }
}
