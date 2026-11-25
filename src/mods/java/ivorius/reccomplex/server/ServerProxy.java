package ivorius.reccomplex.server;

import ivorius.reccomplex.RCProxy;
import java.io.File;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements RCProxy {
   public ServerProxy() {
   }

   public File getDataDirectory() {
      return FMLCommonHandler.instance().getSidedDelegate().getServer().getDataDirectory();
   }

   public void loadConfig(String configID) {
   }

   public void registerRenderers() {
   }

   public void preInit(FMLPreInitializationEvent event) {
   }
}
