package lumien.resourceloader;

import java.util.List;
import lumien.resourceloader.loader.NormalResourceLoader;
import lumien.resourceloader.loader.OverridingResourceLoader;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;

@Mod(
   modid = "resourceloader",
   name = "Resource Loader",
   version = "1.5.3",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class ResourceLoader {
   public static final String MOD_ID = "resourceloader";
   private static boolean enabled;

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         enabled = true;
      }
   }

   public static void insertNormalPack(List<IResourcePack> resourceList) {
      if (enabled) {
         resourceList.add(new NormalResourceLoader());
      }
   }

   public static void insertForcedPack(List<IResourcePack> resourceList) {
      if (enabled) {
         resourceList.add(new OverridingResourceLoader());
      }
   }
}
