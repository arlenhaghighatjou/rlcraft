package lumien.resourceloader;

import java.util.List;
import lumien.resourceloader.loader.NormalResourceLoader;
import lumien.resourceloader.loader.OverridingResourceLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "resourceloader",
   name = "Resource Loader",
   version = "1.5.3",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12,1.13)",
   certificateFingerprint = "d72e0dd57935b3e9476212aea0c0df352dd76291"
)
public class ResourceLoader {
   public static final String MOD_ID = "resourceloader";
   public static final String MOD_NAME = "Resource Loader";
   public static final String MOD_VERSION = "1.5.3";
   @Instance("resourceloader")
   public static ResourceLoader INSTANCE;
   public static Logger logger = LogManager.getLogger("ResourceLoader");
   static NormalResourceLoader normalResourceLoader;
   static OverridingResourceLoader overridingResourceLoader;

   public ResourceLoader() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
   }

   public static void insertNormalPack(List resourceList) {
      if (normalResourceLoader == null) {
         normalResourceLoader = new NormalResourceLoader();
      }

      resourceList.add(normalResourceLoader);
   }

   public static void insertForcedPack(List resourcePackList) {
      if (overridingResourceLoader == null) {
         overridingResourceLoader = new OverridingResourceLoader();
      }

      resourcePackList.add(overridingResourceLoader);
   }
}
