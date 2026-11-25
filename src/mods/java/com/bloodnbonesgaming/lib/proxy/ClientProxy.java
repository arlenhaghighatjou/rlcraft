package com.bloodnbonesgaming.lib.proxy;

import com.bloodnbonesgaming.lib.command.client.CopyToClipboardCommand;
import com.bloodnbonesgaming.lib.events.ClientEventHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerCommands(FMLServerStartingEvent event) {
      ClientCommandHandler.instance.registerCommand(new CopyToClipboardCommand());
   }

   public boolean client() {
      return true;
   }

   public void registerEventHandlers() {
      ClientEventHandler handler = new ClientEventHandler();
      MinecraftForge.EVENT_BUS.register(GuiOpenEvent.class, handler::onGuiOpen);
      ClientCommandHandler.instance.registerCommand(new CopyToClipboardCommand());
   }
}
