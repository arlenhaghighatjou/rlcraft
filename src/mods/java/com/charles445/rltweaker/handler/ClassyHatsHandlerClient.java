package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.client.FixedHatLayer;
import com.charles445.rltweaker.client.gui.ClassyHatsGuiDelegate;
import com.charles445.rltweaker.client.gui.GuiDelegator;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClassyHatsHandlerClient {
   public ClassyHatsHandlerClient() {
      try {
         if (ModConfig.client.classyhats.mobendsCompatibility) {
            FixedHatLayer.init();
         }

         if (ModConfig.client.classyhats.fixBagScreen) {
            GuiDelegator.addDelegate("wiresegal.classyhats.client.gui.GuiHatBag", new ClassyHatsGuiDelegate());
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup ClassyHatsHandlerClient!", var2);
         ErrorUtil.logSilent("ClassyHats Client Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }
}
