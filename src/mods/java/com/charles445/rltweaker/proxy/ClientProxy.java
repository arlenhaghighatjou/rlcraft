package com.charles445.rltweaker.proxy;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.client.ClientCommandDebug;
import com.charles445.rltweaker.client.gui.GuiDelegator;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.handler.ClassyHatsHandlerClient;
import com.charles445.rltweaker.handler.FBPHandlerClient;
import com.charles445.rltweaker.handler.GooglyEyesHandlerClient;
import com.charles445.rltweaker.handler.MantleHandlerClient;
import com.charles445.rltweaker.handler.PotionCoreHandlerClient;
import com.charles445.rltweaker.handler.ReskillableHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void postInit() {
      super.postInit();
      RLTweaker.clientHandlers.put("GuiDelegator", new GuiDelegator());
      ClientCommandHandler.instance.registerCommand(new ClientCommandDebug());
      if (Loader.isModLoaded("potioncore") && ModConfig.client.potioncore.enabled) {
         RLTweaker.clientHandlers.put("potioncore", new PotionCoreHandlerClient());
      }

      if (Loader.isModLoaded("classyhats") && ModConfig.client.classyhats.enabled) {
         RLTweaker.clientHandlers.put("classyhats", new ClassyHatsHandlerClient());
      }

      if (Loader.isModLoaded("reskillable") && ModConfig.client.reskillable.enabled) {
         RLTweaker.clientHandlers.put("reskillable", new ReskillableHandlerClient());
      }

   }

   public void loadComplete() {
      super.loadComplete();
      if (Loader.isModLoaded("mantle") && ModConfig.client.mantle.enabled) {
         RLTweaker.clientHandlers.put("mantle", new MantleHandlerClient());
      }

      if (Loader.isModLoaded("fbp") && ModConfig.client.fbp.enabled) {
         RLTweaker.clientHandlers.put("fbp", new FBPHandlerClient());
      }

      if (Loader.isModLoaded("googlyeyes") && ModConfig.client.googlyeyes.enabled) {
         RLTweaker.clientHandlers.put("googlyeyes", new GooglyEyesHandlerClient());
      }

   }

   public EntityPlayer getClientMinecraftPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public Boolean isClientConnectedToServer() {
      return Minecraft.getMinecraft().getConnection().getNetworkManager().isChannelOpen();
   }
}
