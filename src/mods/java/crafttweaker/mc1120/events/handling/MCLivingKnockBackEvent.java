package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.LivingKnockBackEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;

public class MCLivingKnockBackEvent implements LivingKnockBackEvent {
   private final net.minecraftforge.event.entity.living.LivingKnockBackEvent event;

   public MCLivingKnockBackEvent(net.minecraftforge.event.entity.living.LivingKnockBackEvent event) {
      this.event = event;
   }

   public IEntity getAttacker() {
      return CraftTweakerMC.getIEntity(this.event.getAttacker());
   }

   public void setAttacker(IEntity attacker) {
      this.event.setAttacker(CraftTweakerMC.getEntity(attacker));
   }

   public float getStrength() {
      return this.event.getStrength();
   }

   public void setStrength(float strength) {
      this.event.setStrength(strength);
   }

   public double getRatioX() {
      return this.event.getRatioX();
   }

   public void setRatioX(double ratioX) {
      this.event.setRatioX(ratioX);
   }

   public double getRatioZ() {
      return this.event.getRatioZ();
   }

   public void setRatioZ(double ratioZ) {
      this.event.setRatioZ(ratioZ);
   }

   public IEntity getOriginalAttacker() {
      return CraftTweakerMC.getIEntity(this.event.getOriginalAttacker());
   }

   public float getOriginalStrength() {
      return this.event.getOriginalStrength();
   }

   public double getOriginalRatioX() {
      return this.event.getOriginalRatioX();
   }

   public double getOriginalRatioZ() {
      return this.event.getOriginalRatioZ();
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
