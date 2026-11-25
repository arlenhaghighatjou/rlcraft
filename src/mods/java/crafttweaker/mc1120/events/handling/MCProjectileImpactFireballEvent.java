package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.ProjectileImpactFireballEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Fireball;

public class MCProjectileImpactFireballEvent extends MCProjectileImpactEvent implements ProjectileImpactFireballEvent {
   private final Fireball event;
   private final EntityFireball fireball;

   public MCProjectileImpactFireballEvent(Fireball event) {
      super(event);
      this.event = event;
      this.fireball = event.getFireball();
   }

   public IEntity getFireball() {
      return CraftTweakerMC.getIEntity(this.fireball);
   }

   public IEntityLivingBase getShooter() {
      return CraftTweakerMC.getIEntityLivingBase(this.fireball.shootingEntity);
   }

   public double getAccelerationX() {
      return this.fireball.accelerationX;
   }

   public void setAccelerationX(double accelerationX) {
      this.fireball.accelerationX = accelerationX;
   }

   public double getAccelerationY() {
      return this.fireball.accelerationY;
   }

   public void setAccelerationY(double accelerationY) {
      this.fireball.accelerationY = accelerationY;
   }

   public double getAccelerationZ() {
      return this.fireball.accelerationZ;
   }

   public void setAccelerationZ(double accelerationZ) {
      this.fireball.accelerationZ = accelerationZ;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
