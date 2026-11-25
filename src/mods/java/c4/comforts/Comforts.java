package c4.comforts;

import c4.comforts.proxy.CommonProxy;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "comforts",
   name = "Comforts",
   version = "1.4.1.3",
   dependencies = "required-after:forge@[14.23.5.2776,);after:morpheus;after:toughasnails",
   acceptedMinecraftVersions = "[1.12, 1.13)",
   certificateFingerprint = "2484ef4d131fdc0dca0647aa21b7b944ddb935a1"
)
public class Comforts {
   public static final String MODID = "comforts";
   public static final String MODNAME = "Comforts";
   public static final String MODVER = "1.4.1.3";
   @SidedProxy(
      clientSide = "c4.comforts.proxy.ClientProxy",
      serverSide = "c4.comforts.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance
   public static Comforts instance;
   public static Logger logger;

   public Comforts() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent evt) {
      logger = evt.getModLog();
      proxy.preInit(evt);
   }

   @EventHandler
   public void init(FMLInitializationEvent evt) {
      proxy.init(evt);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent evt) {
      proxy.postInit(evt);
   }

   @EventHandler
   public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
      FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
   }
}
