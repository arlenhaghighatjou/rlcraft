package com.blamejared.ctgui.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public EntityPlayer getPlayer() {
      return Minecraft.getMinecraft().player;
   }
}
