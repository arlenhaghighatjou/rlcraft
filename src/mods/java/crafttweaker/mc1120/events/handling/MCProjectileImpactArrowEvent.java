package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityArrow;
import crafttweaker.api.event.ProjectileImpactArrowEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Arrow;

public class MCProjectileImpactArrowEvent extends MCProjectileImpactEvent implements ProjectileImpactArrowEvent {
   private final Arrow event;
   private final EntityArrow arrow;

   public MCProjectileImpactArrowEvent(Arrow event) {
      super(event);
      this.event = event;
      this.arrow = event.getArrow();
   }

   public IEntity getArrow() {
      return CraftTweakerMC.getIEntity(this.arrow);
   }

   public IEntityArrow getArrowNew() {
      return CraftTweakerMC.getIEntityArrow(this.arrow);
   }

   public IEntity getShooter() {
      return CraftTweakerMC.getIEntity(this.arrow.shootingEntity);
   }

   public double getDamage() {
      return this.arrow.getDamage();
   }

   public void setDamage(double damage) {
      this.arrow.setDamage(damage);
   }

   public void setKnockbackStrength(int knockbackStrength) {
      this.arrow.setKnockbackStrength(knockbackStrength);
   }

   public boolean getIsCritical() {
      return this.arrow.getIsCritical();
   }

   public void setIsCritical(boolean isCritical) {
      this.arrow.setIsCritical(isCritical);
   }

   public String getPickupStatus() {
      return String.valueOf(this.arrow.pickupStatus);
   }

   public void setPickupDisallowed() {
      this.arrow.pickupStatus = PickupStatus.DISALLOWED;
   }

   public void setPickupAllowed() {
      this.arrow.pickupStatus = PickupStatus.ALLOWED;
   }

   public void setPickupCreativeOnly() {
      this.arrow.pickupStatus = PickupStatus.CREATIVE_ONLY;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
