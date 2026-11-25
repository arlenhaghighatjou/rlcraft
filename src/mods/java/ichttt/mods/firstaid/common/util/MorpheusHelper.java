package ichttt.mods.firstaid.common.util;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.quetzi.morpheus.Morpheus;
import net.quetzi.morpheus.MorpheusRegistry;
import net.quetzi.morpheus.api.INewDayHandler;
import net.quetzi.morpheus.world.WorldSleepState;

public class MorpheusHelper implements INewDayHandler {
   private static final MorpheusHelper INSTANCE = new MorpheusHelper();
   private INewDayHandler oldHandler;

   public MorpheusHelper() {
   }

   public static void register() {
      if (INSTANCE.oldHandler != null) {
         throw new IllegalStateException("MorpheusHelper did already init!");
      } else {
         INSTANCE.oldHandler = (INewDayHandler)MorpheusRegistry.registry.get(Integer.valueOf(0));
         FirstAid.LOGGER.info("Morpheus present - enabling compatibility module. Parent: " + INSTANCE.oldHandler.getClass());
         Morpheus.register.registerHandler(INSTANCE, 0);
      }
   }

   public void startNewDay() {
      if (this.oldHandler != null) {
         this.oldHandler.startNewDay();
      }

      if (FirstAidConfig.externalHealing.sleepHealPercentage > 0.0D) {
         WorldSleepState sleepState = (WorldSleepState)Morpheus.playerSleepStatus.get(Integer.valueOf(0));
         if (sleepState != null) {
            if (areEnoughPlayersAsleep(sleepState)) {
               World world = DimensionManager.getWorld(0);

               for(EntityPlayer player : world.playerEntities) {
                  if (player.isPlayerFullyAsleep()) {
                     AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
                     ((AbstractPlayerDamageModel)Objects.requireNonNull(damageModel, "damage model")).sleepHeal(player);
                  }
               }
            }

         }
      }
   }

   private static boolean areEnoughPlayersAsleep(WorldSleepState sleepState) {
      return sleepState.getSleepingPlayers() > 0 && sleepState.getPercentSleeping() >= Morpheus.perc;
   }
}
