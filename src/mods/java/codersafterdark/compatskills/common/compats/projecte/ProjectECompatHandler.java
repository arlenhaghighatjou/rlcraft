package codersafterdark.compatskills.common.compats.projecte;

import codersafterdark.compatskills.utils.CompatModuleBase;
import codersafterdark.reskillable.base.ToolTipHandler;
import java.util.function.Function;
import moze_intel.projecte.gameObjs.gui.GUICondenser;
import moze_intel.projecte.gameObjs.gui.GUICondenserMK2;
import moze_intel.projecte.gameObjs.gui.GUITransmutation;
import net.minecraftforge.common.MinecraftForge;

public class ProjectECompatHandler extends CompatModuleBase {
   public static boolean ENABLED;
   private TransmutationLockHandler handler = new TransmutationLockHandler();

   public ProjectECompatHandler() {
   }

   public void preInit() {
      ENABLED = true;
      MinecraftForge.EVENT_BUS.register(this.handler);
   }

   public void clientPreInit() {
      TransmutationLockHandler var10001 = this.handler;
      this.handler.getClass();
      ToolTipHandler.addTooltipInjector(GUITransmutation.class, var10001::transmutationTooltip);
      var10001 = this.handler;
      this.handler.getClass();
      ToolTipHandler.addTooltipInjector(GUICondenser.class, var10001::condenserTooltip);
      if (!GUICondenser.class.isAssignableFrom(GUICondenserMK2.class)) {
         var10001 = this.handler;
         this.handler.getClass();
         ToolTipHandler.addTooltipInjector(GUICondenserMK2.class, var10001::condenserTooltip);
      }

   }
}
