package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.entity.IEntityThrowable;
import crafttweaker.api.event.ProjectileImpactThrowableEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Throwable;

public class MCProjectileImpactThrowableEvent extends MCProjectileImpactEvent implements ProjectileImpactThrowableEvent {
   private final Throwable event;
   private final EntityThrowable throwable;

   public MCProjectileImpactThrowableEvent(Throwable event) {
      super(event);
      this.event = event;
      this.throwable = event.getThrowable();
   }

   public IEntityThrowable getThrowableNew() {
      return CraftTweakerMC.getIEntityThrowable(this.throwable);
   }

   public IEntity getThrowable() {
      return CraftTweakerMC.getIEntity(this.throwable);
   }

   public IEntityLivingBase getThrower() {
      return CraftTweakerMC.getIEntityLivingBase(this.throwable.getThrower());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
