package com.alcatrazescapee.alcatrazcore.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IProxy {
   IThreadListener getThreadListener(MessageContext var1);

   EntityPlayer getPlayer(MessageContext var1);

   World getClientWorld();

   EntityPlayer getClientPlayer();

   public static class WrongSideException extends RuntimeException {
      WrongSideException(String message) {
         super(message);
      }
   }
}
