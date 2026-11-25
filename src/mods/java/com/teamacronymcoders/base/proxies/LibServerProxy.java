package com.teamacronymcoders.base.proxies;

import com.teamacronymcoders.base.modulesystem.IModule;
import com.teamacronymcoders.base.modulesystem.proxies.IModuleProxy;
import com.teamacronymcoders.base.registrysystem.pieces.RegistrySide;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LibServerProxy extends LibCommonProxy {
   public LibServerProxy() {
   }

   public IModuleProxy getModuleProxy(IModule module) {
      return this.getModuleProxy(module.getServerProxyPath());
   }

   public RegistrySide getRegistrySide() {
      return RegistrySide.SERVER;
   }

   public World getWorld(MessageContext ctx) {
      return ctx.getServerHandler().player.getServerWorld();
   }
}
