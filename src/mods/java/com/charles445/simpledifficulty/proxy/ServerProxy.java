package com.charles445.simpledifficulty.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends CommonProxy {
   public ServerProxy() {
   }

   public void preInit() {
      super.preInit();
   }

   public void init() {
      super.init();
   }

   public void postInit() {
      super.postInit();
   }

   public Side getSide() {
      return Side.SERVER;
   }

   public EntityPlayer getClientMinecraftPlayer() {
      return null;
   }

   public Boolean isClientConnectedToServer() {
      return null;
   }
}
