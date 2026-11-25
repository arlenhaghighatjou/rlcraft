package ichttt.mods.firstaid.server;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.common.IProxy;

public class ServerProxy implements IProxy {
   public ServerProxy() {
   }

   public void preInit() {
      FirstAid.LOGGER.debug("Loading ServerProxy");
   }
}
