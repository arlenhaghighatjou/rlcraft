package codersafterdark.compatskills.common.compats.reskillable.skillchange.changelisteners;

import codersafterdark.compatskills.common.compats.reskillable.skillchange.SkillChangeHandler;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.UnlockableUnlock;
import codersafterdark.reskillable.api.event.UnlockUnlockableEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UnlockHandler {
   public UnlockHandler() {
   }

   @SubscribeEvent
   public void onUnlockableUnlock(Post event) {
      SkillChangeHandler.handleCommands(new UnlockableUnlock(event.getUnlockable()), event.getEntityPlayer());
   }
}
