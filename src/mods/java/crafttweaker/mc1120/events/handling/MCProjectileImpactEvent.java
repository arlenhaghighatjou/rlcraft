package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.IProjectileImpactEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IRayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class MCProjectileImpactEvent implements IProjectileImpactEvent {
   private final ProjectileImpactEvent event;

   public MCProjectileImpactEvent(ProjectileImpactEvent event) {
      this.event = event;
   }

   public IRayTraceResult getRayTraceResult() {
      return CraftTweakerMC.getIRayTraceResult(this.event.getRayTraceResult());
   }

   public IEntity getEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntity());
   }
}
