package codersafterdark.reskillable.api.unlockable;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.event.CacheInvalidatedEvent;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.toast.ToastHelper;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class AutoUnlocker {
   private static Set<Unlockable> unlockables = new HashSet();
   private static boolean hasUncacheable;
   private static boolean hasBeenSet;

   public AutoUnlocker() {
   }

   public static void setUnlockables() {
      hasBeenSet = true;
      if (unlockables.isEmpty()) {
         for(Unlockable u : ReskillableRegistries.UNLOCKABLES.getValuesCollection()) {
            if (u.isEnabled() && u.getCost() == 0) {
               addUnlockable(u);
            }
         }
      } else {
         recheckUnlockables();
      }

   }

   private static void addUnlockable(Unlockable u) {
      unlockables.add(u);
      if (!hasUncacheable) {
         for(Requirement requirement : u.getRequirements().getRequirements()) {
            if (!requirement.isCacheable()) {
               hasUncacheable = true;
               break;
            }
         }
      }

   }

   public static void recheckUnlockables() {
      if (hasBeenSet) {
         for(Unlockable u : ReskillableRegistries.UNLOCKABLES.getValuesCollection()) {
            if (u.isEnabled() && u.getCost() == 0) {
               addUnlockable(u);
            } else {
               unlockables.remove(u);
            }
         }

      }
   }

   public static void recheck(EntityPlayer player) {
      PlayerData data = PlayerDataHandler.get(player);
      if (data != null) {
         boolean anyUnlocked = false;

         for(Unlockable u : unlockables) {
            PlayerSkillInfo skillInfo = data.getSkillInfo(u.getParentSkill());
            if (!skillInfo.isUnlocked(u)) {
               RequirementHolder holder = u.getRequirements();
               if (holder.equals(LevelLockHandler.EMPTY_LOCK) || data.matchStats(holder)) {
                  skillInfo.unlock(u, player);
                  if (player instanceof EntityPlayerMP) {
                     ToastHelper.sendUnlockableToast((EntityPlayerMP)player, u);
                  }

                  anyUnlocked = true;
               }
            }
         }

         if (anyUnlocked) {
            data.saveAndSync();
         }

      }
   }

   public static void register(EventBus bus) {
      bus.register(CacheInvalidatedEvent.class, AutoUnlocker::onCacheInvalidated);
      bus.register(LivingUpdateEvent.class, AutoUnlocker::onEntityLiving);
   }

   public static void onCacheInvalidated(CacheInvalidatedEvent event) {
      recheck(event.getPlayer());
   }

   public static void onEntityLiving(LivingUpdateEvent event) {
      if (hasUncacheable) {
         EntityLivingBase entityLiving = event.getEntityLiving();
         if (entityLiving instanceof EntityPlayer && !entityLiving.world.isRemote && entityLiving.ticksExisted % 100 == 0) {
            recheck((EntityPlayer)entityLiving);
         }
      }

   }
}
