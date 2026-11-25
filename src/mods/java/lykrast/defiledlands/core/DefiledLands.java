package lykrast.defiledlands.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "defiledlands",
   name = "Defiled Lands",
   version = "1.4.3",
   acceptedMinecraftVersions = "[1.12,1.13)",
   dependencies = "required-after:baubles;after:tconstruct"
)
public class DefiledLands {
   public static final String MODID = "defiledlands";
   public static final String NAME = "Defiled Lands";
   public static final String VERSION = "1.4.3";
   @Instance("defiledlands")
   public static DefiledLands instance;
   public static final Logger LOGGER = LogManager.getLogger("defiledlands");
   @SidedProxy(
      clientSide = "lykrast.defiledlands.core.ClientProxy",
      serverSide = "lykrast.defiledlands.core.CommonProxy"
   )
   public static CommonProxy proxy;

   public DefiledLands() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      proxy.preInit(e);
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
      proxy.init(e);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
      proxy.postInit(e);
   }
}
