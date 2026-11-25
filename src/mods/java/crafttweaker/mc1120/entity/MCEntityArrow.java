package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityArrow;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;

public class MCEntityArrow extends MCEntity implements IEntityArrow {
   private final EntityArrow entityArrow;

   public MCEntityArrow(EntityArrow entityArrow) {
      super(entityArrow);
      this.entityArrow = entityArrow;
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      this.entityArrow.shoot(x, y, z, velocity, inaccuracy);
   }

   public void shoot(IEntity shooter, float pitch, float yaw, float roll, float velocity, float inaccuracy) {
      this.entityArrow.shoot(CraftTweakerMC.getEntity(shooter), pitch, yaw, roll, velocity, inaccuracy);
   }

   public void setDamage(double damage) {
      this.entityArrow.setDamage(damage);
   }

   public double getDamage() {
      return this.entityArrow.getDamage();
   }

   public void setIsCritical(boolean critical) {
      this.entityArrow.setIsCritical(critical);
   }

   public boolean getIsCritical() {
      return this.entityArrow.getIsCritical();
   }

   public int getKnockbackStrength() {
      return this.entityArrow.knockbackStrength;
   }

   public void setKnockbackStrength(int knockbackStrength) {
      this.entityArrow.setKnockbackStrength(knockbackStrength);
   }

   public int arrowShake() {
      return this.entityArrow.arrowShake;
   }

   public IEntity getShooter() {
      return CraftTweakerMC.getIEntity(this.entityArrow.shootingEntity);
   }

   public void setShooter(IEntity shooter) {
      this.entityArrow.shootingEntity = CraftTweakerMC.getEntity(shooter);
   }

   public String getPickupStatus() {
      return String.valueOf(this.entityArrow.pickupStatus);
   }

   public void setPickupStatus(String pickupStatus) {
      PickupStatus.valueOf(pickupStatus);
   }

   public void setPickupDisallowed() {
      this.entityArrow.pickupStatus = PickupStatus.DISALLOWED;
   }

   public void setPickupAllowed() {
      this.entityArrow.pickupStatus = PickupStatus.ALLOWED;
   }

   public void setPickupCreativeOnly() {
      this.entityArrow.pickupStatus = PickupStatus.CREATIVE_ONLY;
   }
}
