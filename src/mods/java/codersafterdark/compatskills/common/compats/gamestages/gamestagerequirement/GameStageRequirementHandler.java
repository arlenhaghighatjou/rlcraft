package codersafterdark.compatskills.common.compats.gamestages.gamestagerequirement;

import codersafterdark.reskillable.api.requirement.RequirementCache;
import net.darkhax.gamestages.event.GameStageEvent.Added;
import net.darkhax.gamestages.event.GameStageEvent.Removed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GameStageRequirementHandler {
   public GameStageRequirementHandler() {
   }

   @SubscribeEvent
   public void gameStageAdded(Added event) {
      RequirementCache.invalidateCache(event.getEntityPlayer(), new Class[]{GameStageRequirement.class});
   }

   @SubscribeEvent
   public void gameStageRemoved(Removed event) {
      RequirementCache.invalidateCache(event.getEntityPlayer(), new Class[]{GameStageRequirement.class});
   }
}
