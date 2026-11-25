package c4.corpsecomplex.common.modules;

import c4.corpsecomplex.common.Module;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExperienceModule extends Module {
   private static boolean keepXP;
   private static double xpLoss;
   private static double xpRecover;
   private static int maxXPRecover;
   private static boolean cfgEnabled;

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onPlayerXPDrop(LivingExperienceDropEvent e) {
      if (e.getEntityLiving() instanceof EntityPlayer) {
         setExperiencesValues(e);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onPlayerRespawnBegin(Clone e) {
      if (e.isWasDeath()) {
         restoreXP(e.getEntityPlayer(), e.getOriginal());
      }

   }

   public ExperienceModule() {
      super("Experience", "Customize experience loss on death");
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Experience Module", false, "Set to true to enable experience module", false);
      keepXP = this.getBool("Keep All XP", false, "Set to true to keep all XP on death", false);
      xpLoss = this.getDouble("Lost XP Percent", 1.0D, 0.0F, 1.0F, "Percent of experience lost on death", false);
      xpRecover = this.getDouble("Recoverable XP Percent", 0.20000000298023224D, 0.0F, 1.0F, "Percent of lost experience that can be recovered", false);
      maxXPRecover = this.getInt("Maximum Recoverable XP", 0, 0, 10000, "Maximum amount of experience that can be recovered, 0 to disable", false);
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Collections.singletonList("Enable Experience Module"));
   }

   public void setEnabled() {
      this.enabled = cfgEnabled && !Loader.isModLoaded("tombstone");
   }

   private static void restoreXP(EntityPlayer player, EntityPlayer oldPlayer) {
      resetXP(player);
      player.addExperience(oldPlayer.experienceTotal);
   }

   private static void setExperiencesValues(LivingExperienceDropEvent e) {
      EntityPlayer player = (EntityPlayer)e.getEntityLiving();
      if (keepXP) {
         e.setCanceled(true);
      } else {
         int dropXP = (int)Math.round((double)player.experienceTotal * xpLoss * xpRecover);
         int keptXP = (int)Math.round((double)player.experienceTotal * (1.0D - xpLoss));
         if (maxXPRecover > 0) {
            dropXP = Math.min(maxXPRecover, dropXP);
         }

         e.setDroppedExperience(dropXP);
         resetXP(player);
         addExperience(player, keptXP);
      }

   }

   private static void addExperience(EntityPlayer player, int amount) {
      int i = Integer.MAX_VALUE - player.experienceTotal;
      if (amount > i) {
         amount = i;
      }

      player.experience += (float)amount / (float)player.xpBarCap();

      for(player.experienceTotal += amount; player.experience >= 1.0F; player.experience /= (float)player.xpBarCap()) {
         player.experience = (player.experience - 1.0F) * (float)player.xpBarCap();
         ++player.experienceLevel;
         if (player.experienceLevel < 0) {
            resetXP(player);
         }
      }

   }

   private static void resetXP(EntityPlayer player) {
      player.experienceLevel = 0;
      player.experience = 0.0F;
      player.experienceTotal = 0;
   }
}
