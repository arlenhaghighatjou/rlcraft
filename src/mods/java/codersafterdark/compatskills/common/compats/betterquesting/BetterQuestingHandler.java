package codersafterdark.compatskills.common.compats.betterquesting;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api2.registry.IRegistry;
import codersafterdark.compatskills.utils.CompatModuleBase;
import net.minecraftforge.common.MinecraftForge;

public class BetterQuestingHandler extends CompatModuleBase {
   public static boolean ENABLED;

   public BetterQuestingHandler() {
   }

   public void preInit() {
      ENABLED = true;
      MinecraftForge.EVENT_BUS.register(new BQTaskHandler());
   }

   public void postInit() {
      ((IRegistry)QuestingAPI.getAPI(ApiReference.TASK_REG)).register(FactoryTaskRequirement.INSTANCE);
   }
}
