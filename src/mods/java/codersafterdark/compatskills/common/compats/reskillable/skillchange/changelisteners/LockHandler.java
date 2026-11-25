package codersafterdark.compatskills.common.compats.reskillable.skillchange.changelisteners;

import codersafterdark.compatskills.common.compats.reskillable.skillchange.SkillChangeHandler;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.UnlockableLock;
import codersafterdark.reskillable.api.event.LockUnlockableEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LockHandler {
   public LockHandler() {
   }

   @SubscribeEvent
   public void onUnlockableLock(Post event) {
      SkillChangeHandler.handleCommands(new UnlockableLock(event.getUnlockable()), event.getEntityPlayer());
   }
}
