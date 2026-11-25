package codersafterdark.compatskills.common.compats.thaumcraft.requirements;

import codersafterdark.reskillable.api.requirement.RequirementCache;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.research.ResearchEvent.Research;

public class TCInvalidateHandler {
   public TCInvalidateHandler() {
   }

   @SubscribeEvent
   public void onResearchEvent(Research event) {
      RequirementCache.invalidateCache(event.getPlayer(), new Class[]{ResearchRequirement.class});
   }
}
