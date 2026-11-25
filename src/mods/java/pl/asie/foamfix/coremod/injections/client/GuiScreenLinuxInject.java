package pl.asie.foamfix.coremod.injections.client;

import java.net.URI;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import pl.asie.foamfix.FoamFix;

public class GuiScreenLinuxInject {
   public GuiScreenLinuxInject() {
   }

   private void openWebLink(URI url) {
      EnumOS osType = Util.getOSType();
      if (osType != EnumOS.LINUX && osType != EnumOS.UNKNOWN) {
         this.openWebLink_foamfix_old(url);
      } else {
         FoamFix.proxy.openUrlLinux(url);
      }

   }

   private void openWebLink_foamfix_old(URI url) {
   }
}
