package codersafterdark.compatskills.common.compats.reskillable.skillhiding;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.event.CacheInvalidatedEvent;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.base.LevelLockHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class VisibilityLockHandler {
   public VisibilityLockHandler() {
   }

   @SubscribeEvent
   public void onPlayerJoinHide(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      PlayerData data = PlayerDataHandler.get(player);

      for(Skill skill : ReskillableRegistries.SKILLS.getValuesCollection()) {
         RequirementHolder holder = LevelLockHandler.getLockByKey(new VisibilityLock(skill));
         if (!holder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(holder)) {
            skill.setHidden(true);
         }
      }

   }

   @SubscribeEvent
   public void onCacheInvalidated(CacheInvalidatedEvent event) {
      EntityPlayer player = event.getPlayer();
      PlayerData data = PlayerDataHandler.get(player);

      for(Skill skill : ReskillableRegistries.SKILLS.getValuesCollection()) {
         RequirementHolder holder = LevelLockHandler.getLockByKey(new VisibilityLock(skill));
         if (!holder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(holder)) {
            skill.setHidden(true);
         } else {
            skill.setHidden(false);
         }
      }

   }
}
