package codersafterdark.compatskills.common.compats.dynamicswordskills;

import codersafterdark.reskillable.api.requirement.RequirementCache;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DSSEventHandler {
   public DSSEventHandler() {
   }

   @SubscribeEvent
   public void interactEvent(RightClickItem event) {
      RequirementCache.invalidateCache(event.getEntityPlayer(), new Class[]{DSSkillRequirement.class});
   }

   @SubscribeEvent
   public void interactEvent(RightClickBlock event) {
      RequirementCache.invalidateCache(event.getEntityPlayer(), new Class[]{DSSkillRequirement.class});
   }
}
