package ivorius.reccomplex;

import java.io.File;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface RCProxy {
   File getDataDirectory();

   void loadConfig(String var1);

   void registerRenderers();

   void preInit(FMLPreInitializationEvent var1);
}
