package com.yyon.grapplinghook;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.yyon.grapplinghook.network.LoggedInMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ServerProxyClass extends CommonProxyClass {
   public ServerProxyClass() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
   }

   public void init(FMLInitializationEvent event, grapplemod grapplemod) {
      super.init(event, grapplemod);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      if (GrappleConfig.getconf().override_allowflight) {
         FMLCommonHandler.instance().getMinecraftServerInstance().setAllowFlight(true);
      }

   }

   public void register(EventBus bus) {
      super.register(bus);
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLoggedInEvent);
   }

   public void onPlayerLoggedInEvent(PlayerLoggedInEvent e) {
      if (e.player instanceof EntityPlayerMP) {
         grapplemod.network.sendTo(new LoggedInMessage(GrappleConfig.options), (EntityPlayerMP)e.player);
      } else {
         System.out.println("Not an EntityPlayerMP");
      }

   }
}
