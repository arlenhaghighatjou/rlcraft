package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EnderTeleportEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;

public class MCEnderTeleportEvent implements EnderTeleportEvent {
   private final net.minecraftforge.event.entity.living.EnderTeleportEvent event;

   public MCEnderTeleportEvent(net.minecraftforge.event.entity.living.EnderTeleportEvent event) {
      this.event = event;
   }

   public double getTargetX() {
      return this.event.getTargetX();
   }

   public void setTargetX(double targetX) {
      this.event.setTargetX(targetX);
   }

   public double getTargetY() {
      return this.event.getTargetY();
   }

   public void setTargetY(double targetY) {
      this.event.setTargetY(targetY);
   }

   public double getTargetZ() {
      return this.event.getTargetZ();
   }

   public void setTargetZ(double targetZ) {
      this.event.setTargetZ(targetZ);
   }

   public float getAttackDamage() {
      return this.event.getAttackDamage();
   }

   public void setAttackDamage(float attackDamage) {
      this.event.setAttackDamage(attackDamage);
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
