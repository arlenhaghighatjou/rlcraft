package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityFishHook;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityFishHook.State;

public class MCEntityFishHook extends MCEntity implements IEntityFishHook {
   private final EntityFishHook entityFishHook;

   public MCEntityFishHook(EntityFishHook entityFishHook) {
      super(entityFishHook);
      this.entityFishHook = entityFishHook;
   }

   public String getState() {
      return this.entityFishHook.currentState.name().toUpperCase();
   }

   public void setState(String state) {
      this.entityFishHook.currentState = State.valueOf(state.toUpperCase());
   }

   public IEntity getCaughtEntity() {
      return CraftTweakerMC.getIEntity(this.entityFishHook.caughtEntity);
   }

   public void setCaughtEntity(IEntity entity) {
      this.entityFishHook.caughtEntity = CraftTweakerMC.getEntity(entity);
   }

   public IPlayer getAngler() {
      return CraftTweakerMC.getIPlayer(this.entityFishHook.getAngler());
   }

   public void setLureSpeed(int lureSpeed) {
      this.entityFishHook.setLureSpeed(lureSpeed);
   }

   public void setLuck(int luck) {
      this.entityFishHook.setLuck(luck);
   }
}
