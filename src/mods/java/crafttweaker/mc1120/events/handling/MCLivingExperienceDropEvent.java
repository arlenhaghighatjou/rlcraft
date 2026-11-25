package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.LivingExperienceDropEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCLivingExperienceDropEvent implements LivingExperienceDropEvent {
   private net.minecraftforge.event.entity.living.LivingExperienceDropEvent event;

   public MCLivingExperienceDropEvent(net.minecraftforge.event.entity.living.LivingExperienceDropEvent event) {
      this.event = event;
   }

   public int getDroppedExperience() {
      return this.event.getDroppedExperience();
   }

   public void setDroppedExperience(int experience) {
      this.event.setDroppedExperience(experience);
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getAttackingPlayer());
   }

   public int getOriginalExperience() {
      return this.event.getOriginalExperience();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }
}
