package com.Shultrea.Rin.Utility_Sector;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
   }

   public void onInit(FMLInitializationEvent event) {
      super.onInit(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
   }

   @Nullable
   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }
}
