package com.teamacronymcoders.base.proxies;

import com.teamacronymcoders.base.event.BaseRegistryEvent;
import com.teamacronymcoders.base.multiblocksystem.IMultiblockRegistry;
import com.teamacronymcoders.base.multiblocksystem.MultiblockEventHandler;
import com.teamacronymcoders.base.multiblocksystem.MultiblockRegistry;
import com.teamacronymcoders.base.recipesystem.loader.ILoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ModCommonProxy {
   private static MultiblockEventHandler s_multiblockHandler = null;

   public ModCommonProxy() {
   }

   public World getClientWorld() {
      return null;
   }

   public boolean isClient() {
      return false;
   }

   public boolean isServer() {
      return true;
   }

   public EntityPlayer getClientPlayer() {
      return null;
   }

   public String translateMessage(String label, String message) {
      return String.format("%s.%s.%s", label, "base", message);
   }

   public IMultiblockRegistry initMultiblockRegistry() {
      if (null == s_multiblockHandler) {
         MinecraftForge.EVENT_BUS.register(s_multiblockHandler = new MultiblockEventHandler());
      }

      return MultiblockRegistry.INSTANCE;
   }

   public void registerServerLoader(BaseRegistryEvent<ILoader> loaderRegistryEvent) {
   }
}
